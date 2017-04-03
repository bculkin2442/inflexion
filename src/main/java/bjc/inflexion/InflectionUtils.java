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
package bjc.inflexion;

/**
 * @author EVE
 *
 */
public class InflectionUtils {
	/**
	 * Inflect a word, replacing the singular suffix with the plural suffix.
	 * 
	 * @param word
	 *                The word to inflect.
	 * 
	 * @param singular
	 *                The singular prefix for the word.
	 * 
	 * @param plural
	 *                The plural prefix for the word.
	 * 
	 * @return The word, with the singular prefix replaced with the plural
	 *         prefix.
	 */
	public static String inflect(String word, String singular, String plural) {
		/*
		 * Remove leading '-' from the patterns.
		 */
		return replaceLast(word, singular.substring(1), plural.substring(1));
	}

	private static String replaceLast(String text, String regex, String replacement) {
		return text.replaceFirst("(?s)(.*)" + regex, "$1" + replacement);
	}
}
