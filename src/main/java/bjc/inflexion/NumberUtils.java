package bjc.inflexion;

/**
 * A variety of functions for doing useful stuff with numbers.
 * 
 * @author EVE
 *
 */
public class NumberUtils {
	/*
	 * @TODO 2/12/18 Ben Culkin :RomanExpansion
	 * 
	 * Use U+305 for large roman numerals, as well as excels 'concise'
	 * numerals (as implemented by roman()).
	 */

	/**
	 * Convert a number into a roman numeral.
	 * 
	 * @param number
	 *                The number to convert.
	 * @param classic
	 *                Whether to use classic roman numerals (use IIII
	 *                instead of IV, and such).
	 * @return The number as a roman numeral.
	 */
	public static String toRoman(long number, boolean classic) {
		StringBuilder work = new StringBuilder();

		long currNumber = number;

		if (currNumber == 0) { return "N"; }

		if (currNumber < 0) {
			currNumber *= -1;

			work.append("-");
		}

		if (currNumber >= 1000) {
			int numM = (int) (currNumber / 1000);
			currNumber = currNumber % 1000;

			for (int i = 0; i < numM; i++) {
				work.append("M");
			}
		}

		if (currNumber >= 900 && !classic) {
			currNumber = currNumber % 900;

			work.append("CM");
		}

		if (currNumber >= 500) {
			currNumber = currNumber % 500;

			work.append("D");
		}

		if (currNumber >= 400 && !classic) {
			currNumber = currNumber % 400;

			work.append("CD");
		}

		if (currNumber >= 100) {
			int numC = (int) (currNumber / 100);
			currNumber = currNumber % 100;

			for (int i = 0; i < numC; i++) {
				work.append("C");
			}
		}

		if (currNumber >= 90 && !classic) {
			currNumber = currNumber % 90;

			work.append("XC");
		}

		if (currNumber >= 50) {
			currNumber = currNumber % 50;

			work.append("L");
		}

		if (currNumber >= 40 && !classic) {
			currNumber = currNumber % 40;

			work.append("XL");
		}

		if (currNumber >= 10) {
			int numX = (int) (currNumber / 10);
			currNumber = currNumber % 10;

			for (int i = 0; i < numX; i++) {
				work.append("X");
			}
		}

		if (currNumber >= 9 && !classic) {
			currNumber = currNumber % 9;

			work.append("IX");
		}

		if (currNumber >= 5) {
			currNumber = currNumber % 5;

			work.append("V");
		}

		if (currNumber >= 4 && !classic) {
			currNumber = currNumber % 4;

			work.append("IV");
		}

		if (currNumber >= 1) {
			int numI = (int) (currNumber / 1);
			currNumber = currNumber % 1;

			for (int i = 0; i < numI; i++) {
				work.append("I");
			}
		}

		return work.toString();
	}

	private static String[] summaryNums = new String[] { "no", "one", "a couple of", "a few", "several" };

	private static String[] summaryNumsEnd = new String[] { "none", "one", "a couple", "a few", "several" };

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
			4, 4, 4, 4 };

	/**
	 * Summarize an integer.
	 *
	 * @param num
	 *                The number to summarize.
	 *
	 * @param atEnd
	 *                Whether or not the integer is at the end of a string.
	 *
	 * @return A string summarizing the integer.
	 */
	public static String summarizeNumber(final long num, final boolean atEnd) {
		if (num >= 0 && num < 10) {
			if (atEnd) return summaryNumsEnd[summaryMap[(int) num]];

			return summaryNums[summaryMap[(int) num]];
		}

		return "many";
	}

	/**
	 * Convert a number into a cardinal number, up to a threshold.
	 * 
	 * @param number
	 *                The number to convert
	 * @param thresh
	 *                The threshold to stop at.
	 * @return The number as a cardinal.
	 */
	public static String toCardinal(long number, long thresh) {
		if (number < thresh) return toCardinal(number, null);

		return Long.toString(number);
	}

	/**
	 * Convert a number into a cardinal number.
	 * 
	 * @param number
	 *                The number to convert
	 * @return The number as a cardinal.
	 */
	public static String toCardinal(long number) {
		return toCardinal(number, null);
	}

	private static String[] cardinals = new String[] { "zero", "one", "two", "three", "four", "five", "six",
			"seven", "eight", "nine", "ten", "eleven", "twelve", "thirteen", "fourteen", "fifteen",
			"sixteen", "seventeen", "eighteen", "nineteen", "twenty", };

	/**
	 * Convert a number into a cardinal number.
	 * 
	 * @param number
	 *                The number to convert to a cardinal.
	 * @param custom
	 *                The customizations to use.
	 * @return The number as a cardinal.
	 */
	public static String toCardinal(long number, CardinalState custom) {
		if (custom != null) {
			String res = custom.handleCustom(number);

			if (res != null) return res;
		}

		if (number < 0) return "negative " + toCardinal(number * -1, custom);

		if (number <= 20) return cardinals[(int) number];

		if (number < 100) {
			if (number % 10 == 0) {
				switch ((int) number) {
				case 30:
					return "thirty";
				case 40:
					return "forty";
				case 50:
					return "fifty";
				case 60:
					return "sixty";
				case 70:
					return "seventy";
				case 80:
					return "eighty";
				case 90:
					return "ninety";
				default:
					/*
					 * Shouldn't happen.
					 */
					assert (false);
				}
			}

			long numTens = number / 10;
			long numOnes = number % 10;

			return toCardinal(numTens, custom) + "-" + toCardinal(numOnes, custom);
		}

		if (number < 1000) {
			long numHundreds = number / 100;
			long rest = number % 100;

			return toCardinal(numHundreds, custom) + " hundred and " + toCardinal(rest, custom);
		}

		long MILLION = (long) (Math.pow(10, 6));
		if (number < MILLION) {
			long numThousands = number / 1000;
			long rest = number % 1000;

			return toCardinal(numThousands, custom) + " thousand, " + toCardinal(rest, custom);
		}

		long BILLION = (long) (Math.pow(10, 9));
		if (number < BILLION) {
			long numMillions = number / MILLION;
			long rest = number % MILLION;

			return toCardinal(numMillions, custom) + " million, " + toCardinal(rest, custom);
		}

		long TRILLION = (long) (Math.pow(10, 12));
		if (number < TRILLION) {
			long numBillions = number / BILLION;
			long rest = number % BILLION;

			return toCardinal(numBillions, custom) + " billion, " + toCardinal(rest, custom);
		}

		throw new IllegalArgumentException(
				"Numbers greater than or equal to 1 trillion are not supported yet.");
	}

	/**
	 * Convert a number into an ordinal, up to a certain value.
	 * 
	 * @param number
	 *                The number to convert to an ordinal.
	 * @param thresh
	 *                The threshold value to stop converting at.
	 * @return The number as an ordinal.
	 */
	public static String toOrdinal(long number, long thresh) {
		return toOrdinal(number, thresh, true);
	}

	/**
	 * Convert a number into an ordinal, up to a certain value.
	 * 
	 * @param number
	 *                The number to convert to an ordinal.
	 * @param thresh
	 *                The threshold value to stop converting at.
	 * @param longForm
	 *                Whether or not to use long-form ordinals (zeroth,
	 *                first, etc.) instead of (0th, 1st, etc.)
	 * @return The number as an ordinal.
	 */
	public static String toOrdinal(long number, long thresh, boolean longForm) {
		if (number < thresh) return toOrdinal(number, longForm);

		return Long.toString(number);
	}

	/**
	 * Convert a number into an ordinal.
	 * 
	 * @param number
	 *                The number to convert to an ordinal.
	 * @return The number as an ordinal.
	 */
	public static String toOrdinal(long number) {
		return toOrdinal(number, true);
	}

	/**
	 * Convert a number into an ordinal.
	 * 
	 * @param number
	 *                The number to convert to an ordinal.
	 * @param longForm
	 *                Whether or not to use long-form ordinals (zeroth,
	 *                first, etc.) instead of (0th, 1st, etc.)
	 * @return The number as an ordinal.
	 */
	public static String toOrdinal(long number, boolean longForm) {
		if (number < 0) { return "minus " + toOrdinal(number, longForm); }

		if (longForm) {
			if (number < 20) {
				switch ((int) number) {
				case 0:
					return "zeroth";
				case 1:
					return "first";
				case 2:
					return "second";
				case 3:
					return "third";
				case 4:
					return "fourth";
				case 5:
					return "fifth";
				case 6:
					return "sixth";
				case 7:
					return "seventh";
				case 8:
					return "eighth";
				case 9:
					return "ninth";
				case 10:
					return "tenth";
				case 11:
					return "eleventh";
				case 12:
					return "twelfth";
				case 13:
					return "thirteenth";
				case 14:
					return "fourteenth";
				case 15:
					return "fifteenth";
				case 16:
					return "sixteenth";
				case 17:
					return "seventeenth";
				case 18:
					return "eighteenth";
				case 19:
					return "nineteenth";
				default:
					/*
					 * Shouldn't happen.
					 */
					assert (false);
				}
			}

			if (number < 100) {
				if (number % 10 == 0) {
					switch ((int) number) {
					case 20:
						return "twentieth";
					case 30:
						return "thirtieth";
					case 40:
						return "fortieth";
					case 50:
						return "fiftieth";
					case 60:
						return "sixtieth";
					case 70:
						return "seventieth";
					case 80:
						return "eightieth";
					case 90:
						return "ninetieth";
					default:
						throw new IllegalArgumentException(
								String.format("Illegal number %d", number));
					}
				}

				long numPostfix = number % 10;
				return toCardinal(number - numPostfix) + "-" + toOrdinal(numPostfix, longForm);
			}
		}

		long procNum = number % 100;
		long tens = procNum / 10;
		long ones = procNum % 10;

		if (tens == 1) { return Long.toString(number) + "th"; }

		switch ((int) ones) {
		case 1:
			return Long.toString(number) + "st";
		case 2:
			return Long.toString(number) + "nd";
		case 3:
			return Long.toString(number) + "rd";
		default:
			return Long.toString(number) + "th";
		}
	}

	private static char[] radixChars = new char[62];
	static {
		int idx = 0;

		for (char i = 0; i < 10; i++) {
			radixChars[idx] = (char) ('0' + i);

			idx += 1;
		}

		for (char i = 0; i < 26; i++) {
			radixChars[idx] = (char) ('A' + i);

			idx += 1;
		}

		for (char i = 0; i < 26; i++) {
			radixChars[idx] = (char) ('a' + i);

			idx += 1;
		}
	}

	/**
	 * Convert a number into a commafied string.
	 * 
	 * @param val
	 *                The number to convert.
	 * @param mincols
	 *                The minimum number of columns to use.
	 * @param padchar
	 *                The padding char to use.
	 * @param commaInterval
	 *                The interval to place commas at.
	 * @param commaChar
	 *                The character to use as a comma
	 * @param signed
	 *                Whether or not to always display a sign
	 * @param radix
	 *                The radix to use
	 * @return The number as a commafied string.
	 */
	public static String toCommaString(long val, int mincols, char padchar, int commaInterval, char commaChar,
			boolean signed, int radix) {
		if (radix > radixChars.length) { throw new IllegalArgumentException(String.format(
				"Radix %d is larger than largest supported radix %d", radix, radixChars.length)); }

		StringBuilder work = new StringBuilder();

		boolean isNeg = false;
		long currVal = val;
		if (currVal < 0) {
			isNeg = true;
			currVal *= -1;
		}

		if (currVal == 0) {
			work.append(radixChars[0]);
		} else {
			int valCounter = 0;

			while (currVal != 0) {
				valCounter += 1;

				int radDigit = (int) (currVal % radix);
				work.append(radixChars[radDigit]);
				currVal = currVal / radix;

				if (commaInterval != 0 && valCounter % commaInterval == 0 && currVal != 0)
					work.append(commaChar);
			}
		}

		if (isNeg)
			work.append("-");
		else if (signed) work.append("+");

		work.reverse();

		/*
		 * @TODO
		 *
		 * Should we have some way to specify how to pad?
		 *
		 * By this, I mean specify padding direction (left, right,
		 * balanced...)
		 */
		StringBuilder pad = new StringBuilder();

		if (work.length() < mincols) {
			@SuppressWarnings("unused")
			int padCount = 0;
			for (int i = work.length(); i < mincols; i++) {
				// @NOTE 9/6/18 :CommaPad
				//
				// I have no idea if this is the intended
				// behavior, or if something is wrong with the
				// example case in the menu
				// if (commaInterval != 0 && padCount != 0) {
				// 	if (Character.isDigit(padchar) && padCount % commaInterval == 0)
				// 		pad.append(commaChar);
				// 	else
				pad.append(padchar);
				// }

				padCount++;
			}
		}

		return pad.toString() + work.toString();
	}

	/**
	 * Convert a number to a normal commafied string.
	 * 
	 * @param val
	 *                The value to convert.
	 * @param mincols
	 *                The minimum number of columns.
	 * @param padchar
	 *                The padding char to use.
	 * @param signed
	 *                Whether or not to display the sign.
	 * @param radix
	 *                The radix to use.
	 * @return The number as a normal commafied string.
	 */
	public static String toNormalString(long val, int mincols, char padchar, boolean signed, int radix) {
		return toCommaString(val, mincols, padchar, 0, ',', signed, radix);
	}
}
