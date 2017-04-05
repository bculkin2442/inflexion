/**
 * (C) Copyright 2017 Benjamin Culkin.
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
package bjc.inflexion.v2;

import java.util.regex.Pattern;

/**
 * Utility methods for constructing inflection affixes.
 * 
 * @author EVE
 *
 */
public class InflectionAffixes {
	/*
	 * Template for 'complete' affix patterns.
	 * 
	 * Match the start of the word, followed by zero or more word
	 * characters, followed by the suffix, then the end of the string.
	 * 
	 * The word is in a capturing group named 'stem'.
	 */
	private static final String COMPLETE_PATT_FMT = "(?<stem>\\w*)%s$";

	/*
	 * Template for 'incomplete' affix patterns.
	 * 
	 * Match the start of the word, followed by one or more word characters,
	 * followed by the suffix, then the end of the string.
	 * 
	 * The word is in a capturing group named 'stem'.
	 */
	private static final String INCOMPLETE_PATT_FMT = "(?<stem>\\w+)%s$";

	/**
	 * Create an affix that's a word by itself.
	 * 
	 * @param suffix
	 *                The suffix to use.
	 * 
	 * @return A affix that represents the suffix.
	 */
	public static InflectionAffix complete(String suffix) {
		Pattern patt = Pattern.compile(String.format(COMPLETE_PATT_FMT, suffix));

		return new SimpleInflectionAffix("%s" + suffix, patt);
	}

	/**
	 * Create an affix that's not a word by itself.
	 * 
	 * @param suffix
	 *                The suffix to use.
	 * 
	 * @return An affix that represents the suffix.
	 */
	public static InflectionAffix incomplete(String suffix) {
		Pattern patt = Pattern.compile(String.format(INCOMPLETE_PATT_FMT, suffix));

		return new SimpleInflectionAffix("%s" + suffix, patt);
	}
}
