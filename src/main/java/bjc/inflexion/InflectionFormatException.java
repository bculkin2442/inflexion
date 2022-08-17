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

import java.util.Collections;
import java.util.List;

/**
 * Exception thrown if the string we are attempting to compile has invalid
 * syntax.
 *
 * @author bjculkin
 *
 */
public class InflectionFormatException extends RuntimeException {
	private static final long serialVersionUID = -5306003088746525691L;

	/**
	 * The string we attempted to parse.
	 */
	public final String inp;

	/**
	 * The errors we encountered parsing the string.
	 */
	public final List<String> parseErrors;

	/**
	 * Create a new format exception.
	 *
	 * @param inp
	 *                    The string we are attempting to compile
	 * @param parseErrors
	 *                    The errors we encountered parsing the string.
	 */
	public InflectionFormatException(String inp, List<String> parseErrors) {
		this.inp = inp;
		// Can't modify the list of parse errors.
		this.parseErrors = Collections.unmodifiableList(parseErrors);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Throwable#toString()
	 */
	@Override
	public String toString() {
		boolean doBrief = false;

		if (doBrief)
			return String.format("Encountered %d errors attempting to parse string %s",
					parseErrors.size(), inp);

		StringBuilder sb = new StringBuilder(parseErrors.size());
		sb.append("Encountered errors attempting to parse the following string:\n\t");
		sb.append(inp);
		sb.append("\nErrors:");
		for (int i = 0; i < parseErrors.size(); i++) {
			String msg = parseErrors.get(i);
			sb.append("\n\t");
			sb.append(msg);
		}
		sb.append("\n(total of ");
		sb.append(parseErrors.size());
		sb.append(" errors)");

		return sb.toString();
	}
}