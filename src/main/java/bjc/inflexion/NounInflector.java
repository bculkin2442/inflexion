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

import static bjc.inflexion.InflectionUtils.*;

/**
 * Inflect English nouns.
 * 
 * @author student
 *
 */
public class NounInflector {
	/**
	 * 
	 */
	public static String inflectNoun(String noun) {
		if (isUserPlural(noun)) {
			return userPlural(noun);
		}

		if (dontPluralize(noun))
			return noun;

		return "";
	}

	/**
	 * Check if a given noun shouldn't be pluralized.
	 * 
	 * @param noun
	 *            The noun to check.
	 * 
	 * @return Whether or not the noun has no plural.
	 */
	private static boolean dontPluralize(String noun) {
		String[] suffixes = new String[] { "-fish", "-ois", "-sheep", "-deer", "-pox", "-[A-Z].*ese", "-itis", };

		for (String sfx : suffixes) {
			if (suffix(noun, sfx))
				return true;
		}

		if (category(noun, "", ""))
			return true;

		return false;
	}

	/**
	 * Inflect a noun according to a user-defined plural.
	 * 
	 * @param noun
	 *            The noun to inflect.
	 * 
	 * @return The noun, inflected by the user form.
	 */
	private static String userPlural(String noun) {
		/*
		 * TODO Auto-generated method stub
		 */
		return null;
	}

	/**
	 * Check if there is a user defined plural form for the given noun.
	 * 
	 * @param noun
	 *            The noun to check.
	 * 
	 * @return Whether or not a user-defined plural exists for a particular
	 *         noun.
	 */
	private static boolean isUserPlural(String noun) {
		/*
		 * TODO Auto-generated method stub
		 */
		return false;
	}
}
