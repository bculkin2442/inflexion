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
}
