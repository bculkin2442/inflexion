/*
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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * General utils for dealing with english.
 *
 * @author student
 */
public class EnglishUtils {
	private static String[] smallNums = new String[] { 
		"zero", "one", "two", "three", "four", "five", "six", "seven",
			"eight", "nine", "ten" };

	private static String[] summaryNums     = new String[] { "no", "one", "a couple of", "a few", "several" };

	private static int[] summaryMap = new int[] {
		/* no */
		0,
			/* one */
			1,
			/* a couple of */
			2,
			/* a few */
			3, 3, 3,
			/* several */
			4, 4, 4, 4
	};

	/**
	 * Convert small integers to words.
	 *
	 * @param num
	 * 	The number to convert.
	 *
	 * @return
	 * 	The word for the number, if it's less than ten.
	 */
	public static String smallIntToWord(final int num) {
		if (num >= 0 && num <= 10) return smallNums[num];

		return Integer.toString(num);
	}

	/**
	 * Summarize an integer.
	 *
	 * @param num
	 * 	The number to summarize.
	 *
	 * @param atEnd
	 * 	Whether or not the integer is at the end of a string.
	 *
	 * @return
	 * 	A string summarizing the integer.
	 */
	public static String intSummarize(final int num, final boolean atEnd) {
		if (num >= 0 && num < 10) return summaryNums[summaryMap[num]];

		return "many";
	}


	public static String pickIndefinite(String phrase) {
		Pattern pattern;
		Matcher matcher;
		String word, lowercaseWord;

		if (phrase.length() == 0) {
			return "a";
		}

		// Getting the first word 
		pattern = Pattern.compile("(\\w+)\\s*.*");
		matcher = pattern.matcher(phrase);
		if(matcher.matches() == true) {
			word = matcher.group(1);
		} else {
			return "an";
		}

		lowercaseWord = word.toLowerCase();

		// Specific start of words that should be preceded by 'an'
		String [] altCases = { "euler", "heir", "honest", "hono" };
		for (String altCase : altCases) {
			if (lowercaseWord.startsWith(altCase) == true) {
				return "an";
			}
		}

		if (lowercaseWord.startsWith("hour") == true && lowercaseWord.startsWith("houri") == false) {
			return "an";
		}


		// Single letter word which should be preceded by 'an'
		if (lowercaseWord.length() == 1) {
			if ("aedhilmnorsx".indexOf(lowercaseWord) >= 0) {
				return "an";
			} else {
				return "a";
			}
		}

		// Capital words which should likely be preceded by 'an'
		if (word.matches("(?!FJO|[HLMNS]Y.|RY[EO]|SQU|(F[LR]?|[HL]|MN?|N|RH?|S[CHKLMNPTVW]?|X(YL)?)[AEIOU])[FHLMNRSX][A-Z]")) {
			return "an";
		}

		// Special cases where a word that begins with a vowel should be preceded by 'a'
		String [] regexes = { "^e[uw]", "^onc?e\\b", "^uni([^nmd]|mo)", "^u[bcfhjkqrst][aeiou]" };

		for (String regex : regexes) {
			if (lowercaseWord.matches(regex+".*") == true) {
				return "a";
			}
		}

		// Special capital words (UK, UN)
		if (word.matches("^U[NK][AIEO].*") == true) {
			return "a";
		} else if (word == word.toUpperCase()) {
			if ("aedhilmnorsx".indexOf(lowercaseWord.substring(0, 1)) >= 0) {
				return "an";
			} else {
				return "a";
			}
		}

		// Basic method of words that begin with a vowel being preceded by 'an'
		if ("aeiou".indexOf(lowercaseWord.substring(0, 1)) >= 0) {
			return "an";
		}

		// Instances where y followed by specific letters is preceded by 'an'
		if (lowercaseWord.matches("^y(b[lor]|cl[ea]|fere|gg|p[ios]|rou|tt).*")) {
			return "an";
		}

		return "a";
	}
}
