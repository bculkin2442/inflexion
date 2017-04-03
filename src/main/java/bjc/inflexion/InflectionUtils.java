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
 * Utility functions for inflections.
 * 
 * Whenever a suffix is mentioned, it should be provided in the format of a
 * leading '-', followed by a regular expression.
 * 
 * @author bjculkin
 *
 */
public class InflectionUtils {
	/**
	 * Returns true if the given word ends in the given suffix.
	 * 
	 * @param word
	 *            The word to check.
	 * 
	 * @param suffix
	 *            The suffix to check for.
	 * 
	 * @return Whether or not the provided word ends in the provided suffix.
	 */
	public static boolean suffix(String word, String suffix) {
		/*
		 * TODO implement me.
		 */
		return false;
	}

	/**
	 * Check if the given word inflects from the given singular suffix to the
	 * given plural suffix.
	 * 
	 * @param word
	 *            The word to check.
	 * 
	 * @param singular
	 *            The singular suffix.
	 * 
	 * @param plural
	 *            The plural suffix.
	 * 
	 * @return Whether or not the provided word is in that inflection category.
	 */
	public static boolean category(String word, String singular, String plural) {
		/*
		 * TODO implement me.
		 */
		return false;
	}

	/**
	 * Replace the provided singular suffix in the word with the given plural
	 * suffix.
	 * 
	 * @param word
	 *            The word to inflect.
	 * 
	 * @param singular
	 *            The singular suffix to remove.
	 * 
	 * @param plural
	 *            The plural suffix to add. Unlike normal suffixes, this must be
	 *            a '-' followed by a string.
	 * 
	 * @return The word with the suffix replaced.
	 */
	public static String inflect(String word, String singular, String plural) {
		/*
		 * TODO implement me.
		 */
		return null;
	}

	/**
	 * Remove the specified suffix from the word.
	 * 
	 * @param word
	 *            The word to stem.
	 * 
	 * @param suffix
	 *            The suffix to remove from the word.
	 * 
	 * @return The word with the suffix removed.
	 */
	public static String stem(String word, String suffix) {
		/*
		 * TODO implement me.
		 */
		return null;
	}
}