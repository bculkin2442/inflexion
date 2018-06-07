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

	private static Pattern AN_ORD = Pattern.compile("(?i)\\A[aefhilmnorsx]-?th\\Z");
	private static Pattern A_ORD  = Pattern.compile("(?i)\\A[bcdgjkpqtuvwyz]-?th\\Z");
	private static Pattern EXP_AN = Pattern.compile("(?i)\\A(?:euler|hour(?!i)|heir|honest|hono)");
	private static Pattern SIN_AN = Pattern.compile("(?i)\\A[aefhilmnorst]\\Z");
	private static Pattern SIN_A  = Pattern.compile("(?i)\\A[bcdgjkpqtuvwyz]\\Z");

	private static Pattern ABBREV_AN = Pattern.compile("\\A(?!FJO|[HLMNS]Y|RY[EQ]|SQU|(F[LR]?|[HL]|MN?|N|RH?|S[CHKLMNPTVW]?|X(YL)?)[AEIOU])[FHLMNRSX][A-Z]");

	private static Pattern IN_Y_AN = Pattern.compile("(?i)\\Ay(?:b[lor]|cl[ea]|fere|gg|p[ios]|rou|tt)");

	private static Pattern ABBREV_C2 = Pattern.compile("(?i)\\A[aefhilmnorsx][.-]");
	private static Pattern ABBREV_C3 = Pattern.compile("(?i)\\A[a-z][.-]");

	private static Pattern CONSONANT = Pattern.compile("(?i)\\A[^aeiouy]");

	private static Pattern SPECVOWEL_C1 = Pattern.compile("(?i)\\Ae[uw]");
	private static Pattern SPECVOWEL_C2 = Pattern.compile("(?i)\\Aonc?e\b");
	private static Pattern SPECVOWEL_C3 = Pattern.compile("(?i)\\Auni(?:[^nmd]|mo)");
	private static Pattern SPECVOWEL_C4 = Pattern.compile("(?i)\\Aut[th]");
	private static Pattern SPECVOWEL_C5 = Pattern.compile("(?i)\\Au[bcfhjkqrst][aeiou]");

	private static Pattern SPECCAP_C1 = Pattern.compile("\\AU[NK][AIEO]?");

	private static Pattern VOWEL = Pattern.compile("(?i)\\A[aeiou]\\Z");

	public static String pickIndefinite(String word) {
		// Handle ordinal forms
		if(A_ORD.matcher(word).find()) return "a";
		if(AN_ORD.matcher(word).find()) return "an";

		// Handle special cases
		if(EXP_AN.matcher(word).find()) return "an";
		if(SIN_AN.matcher(word).find()) return "an";
		if(SIN_A.matcher(word).find()) return "a";

		// Handle abbreviations
		if(ABBREV_AN.matcher(word).find()) return "an";
		if(ABBREV_C2.matcher(word).find()) return "an";
		if(ABBREV_C3.matcher(word).find()) return "a";

		// Handle consonants
		if(CONSONANT.matcher(word).find()) return "a";

		// Handle special vowel forms
		if(SPECVOWEL_C1.matcher(word).find()) return "a";
		if(SPECVOWEL_C2.matcher(word).find()) return "a";
		if(SPECVOWEL_C3.matcher(word).find()) return "a";
		if(SPECVOWEL_C4.matcher(word).find()) return "an";
		if(SPECVOWEL_C5.matcher(word).find()) return "a";

		// Handle special capitals
		if(SPECCAP_C1.matcher(word).find()) return "a";

		// Handle vowels
		if(VOWEL.matcher(word).find()) return "an";

		// Handle Y (before certain consonants, it implies a
		// (unnaturalized) "I" sound)
		if(IN_Y_AN.matcher(word).find()) return "an";

		// Guess "A"
		return "a";
	}
}
