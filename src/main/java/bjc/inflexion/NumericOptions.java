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

import java.util.List;

/**
 * Options for a numeric directive.
 *
 * @author bjculkin
 *
 */
public class NumericOptions extends Options {
	/**
	 * Increment the numeric value before doing anything with it.
	 *
	 * Corresponds to the 'i' option.
	 */
	public boolean increment;
	/**
	 * Amount to increase the value by.
	 *
	 * Attached to the 'i' option.
	 */
	public int incrementAmt = 1;

	/**
	 * Treat zero as singular.
	 *
	 * Doesn't correspond directly to the 's' option, but splitting between
	 * singular zero and using 'no' for zero is useful.
	 */
	public boolean singular;

	/**
	 * Print zero as 'no'.
	 *
	 * Corresponds to 'n' option.
	 */
	public boolean zeroNo;

	/**
	 * Print 'a'/'an' for one.
	 *
	 * Corresponds to 'a' option.
	 */
	public boolean article;

	/**
	 * Don't print any text.
	 *
	 * Corresponds to 'd' option.
	 */
	public boolean nonPrint;

	/**
	 * Print the number as a cardinal.
	 *
	 * Corresponds to 'w' option.
	 */
	public boolean cardinal;
	/**
	 * Threshold for when to stop printing the number as a cardinal.
	 *
	 * Attached to the 'w' and 'o' options.
	 */
	public int cardinalThresh = 11;

	/**
	 * Print the number as an ordinal.
	 *
	 * Corresponds to the 'o' option.
	 */
	public boolean ordinal;
	/**
	 * Threshold for when to stop printing the number as an ordinal.
	 *
	 * If the current count is greater than 'cardinalThresh', ordinals like 1st
	 * and 2nd will be printed instead of first and second.
	 *
	 * Attached to the 'o' option.
	 */
	public int ordinalThresh = Integer.MAX_VALUE;

	/**
	 * Summarize a number.
	 *
	 * Corresponds to the 'f' option.
	 */
	public boolean summarize;

	/**
	 * Mark the summarization as occurring at the end of the string, regardless of
	 * its current position.
	 */
	public boolean atEnd = false;

	/**
	 * Create a new set of numeric options from a string.
	 *
	 * @param options
	 *                    The string to create options from.
	 * @param curPos
	 *                    The current position into the string.
	 * @param startFold Whether to start with folding on
	 * @param parseErrors
	 *                    The current list of parsing errors.
	 */
	public NumericOptions(String options, int curPos, boolean startFold, List<String> parseErrors) {
		if (options.equals(""))
			return;

		char prevOption = ' ';
		StringBuilder currNum = new StringBuilder();

		boolean doingCaseFolding = startFold;

		for (int i = 0; i < options.length(); i++) {
			char ci = options.charAt(i);

			if (Character.isDigit(ci) || ci == '-' || ci == '+') {
				currNum.append(ci);

				continue;
			}

			if (doingCaseFolding && Character.isLowerCase(ci)) {
				continue;
			} else if (Character.isUpperCase(ci)) {
				doingCaseFolding = true;

				ci = Character.toLowerCase(ci);
			}

			if (currNum.length() > 0) {
				parseNumericParam(curPos, parseErrors, prevOption, currNum, i);

				currNum = new StringBuilder();
			}

			switch (ci) {
			case 'n':
				zeroNo = true;
				break;
			case 's':
				singular = true;
				break;
			case 'a':
				article = true;
				break;
			case 'w':
				cardinal = true;
				break;
			case 'o':
				ordinal = true;
				break;
			case 'f':
				summarize = true;
				break;
			case 'e':
				article = true;
				singular = true;
				zeroNo = true;
				cardinal = true;
				break;
			case 'i':
				increment = true;
				break;
			case 'd':
				nonPrint = true;
				break;
			default:
				parseErrors.add(error(curPos, i, "Unhandled option %c", ci));
			}

			prevOption = ci;
		}

		if (currNum.length() > 0) {
			parseNumericParam(curPos, parseErrors, prevOption, currNum,
					options.length() - 1);

			currNum = new StringBuilder();
		}
	}

	/**
	 * Create a blank set of numeric options.
	 */
	public NumericOptions() {
	}

	private void parseNumericParam(int curPos, List<String> parseErrors,
			char prevOption, StringBuilder currNum, int i) {
		int nVal = 0;
		try {
			nVal = Integer.parseInt(currNum.toString());
		} catch (NumberFormatException nfex) {
			parseErrors.add(error(curPos, i,
					"Improperly formatted numeric parameter %s to option '%c'",
					currNum.toString(), prevOption));
		}
		switch (prevOption) {
		case 'w':
			cardinalThresh = nVal;
			break;
		case 'o':
			ordinalThresh = nVal;
			break;
		case 'f':
			if (nVal == 1) {
				atEnd = true;
			} else if (nVal == 0) {
				atEnd = false;
			} else {
				parseErrors.add(error(curPos, i,
						"'f' parameter only takes parameters of zero or one, not %d",
						nVal));
			}
			break;
		case 'i':
			incrementAmt = nVal;
			break;
		default:
			parseErrors.add(error(curPos, i,
					"Option '%c' does not take a numeric parameter (value %s)",
					prevOption, currNum));
		}
	}

	// Emit error message
	private static String error(int curPos, int i, String msg, Object... props) {
		return InflectionDirective.error("#", curPos, i, msg, props);
	}
}