/**
 * (C) Copyright 2022 Benjamin Culkin.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package bjc.inflexion;

import java.util.Iterator;

/**
 * Performs the parsing of directives from a string.
 * 
 * @author bjculkin
 */
public class DirectiveIterator implements Iterator<String> {
	private String strang;
	private int pos;

	/**
	 * Create a new directive iterator over a string.
	 *
	 * @param strang
	 *               The string to parse directives from.
	 */
	public DirectiveIterator(String strang) {
		this.strang = strang;
	}

	@Override
	public boolean hasNext() {
		return pos < strang.length();
	}

	@Override
	public String next() {
		if (!hasNext())
			return null;

		// Directive nesting level
		int level = 0;
		int prevPos = pos;

		char prevChar = ' ';
		boolean parsingVar = false;

		for (; pos < strang.length(); pos++) {
			// Backslash escapes a character
			if (prevChar == '\\')
				continue;

			char c = strang.charAt(pos);
			switch (c) {
			case '<':
				// Stop parsing at the start of a
				// directive, unless the directive is
				// the first thing in the string.
				if (level == 0 && prevPos != pos) {
					return strang.substring(prevPos, pos);
				}
				level += 1;
				break;
			case '>':
				// :ErrorHandling 11/19/18
				if (level == 0)
					throw new IllegalArgumentException(
							"Attempted to close inflection directive without one open at position "
									+ prevPos + " in string '" + strang
									+ "', current token is '"
									+ strang.substring(prevPos, pos) + "'");
				// Denest a level
				level = Math.max(0, level - 1);
				// Stop parsing at the end of a
				// directive.
				if (level == 0) {
					// Advance past the '>'
					pos += 1;

					return strang.substring(prevPos, pos);

				}
				break;
			case '$':
				// Ignore v-refs when inside a directive
				if (level > 0)
					break;
				// Stop parsing if this isn't at the
				// start of a string
				if (prevPos != pos)
					return strang.substring(prevPos, pos);
				parsingVar = true;
				break;
			case ' ':
				// If we're parsing a v-ref, this
				// finishes it.
				if (parsingVar)
					return strang.substring(prevPos, pos);
				break;
			default:
				// Do nothing for ordinary characters
				break;
			}
		}

		/*
		 * @TODO 11/19/18 Ben Culkin :ErrorHandling Do something better than this
		 * exception, if possible.
		 *
		 * In the rest of the inflection string code, we use the whole 'list of
		 * errors/warnings' thing. Is there a way to do something similar here?
		 */
		if (level > 0)
			throw new IllegalArgumentException(
					"Unclosed inflection directive, starting at position " + prevPos
							+ " in string '" + strang + "'");

		return strang.substring(prevPos, pos);
	}
}