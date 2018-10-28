/**
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

import static bjc.inflexion.InflectionString.InflectionDirective.literal;
import static bjc.inflexion.InflectionString.InflectionDirective.noun;
import static bjc.inflexion.InflectionString.InflectionDirective.numeric;
import static bjc.inflexion.InflectionString.InflectionDirective.variable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bjc.inflexion.InflectionString.InflectionDirective.NounOptions;
import bjc.inflexion.InflectionString.InflectionDirective.NumericOptions;
import bjc.inflexion.nouns.Noun;
import bjc.inflexion.nouns.Nouns;
import bjc.inflexion.nouns.Prepositions;

/**
 * A compiled inflection markup string
 * 
 * @author bjculkin
 *
 */
public class InflectionString {
	/**
	 * Represents a directive in a inflection string.
	 * 
	 * @author bjculkin
	 *
	 */
	public static final class InflectionDirective {
		/**
		 * The type of the directive in the inflection string.
		 * 
		 * @author bjculkin
		 *
		 */
		public static enum DirectiveType {
			/**
			 * A literal string. Not inflected in any way.
			 */
			LITERAL,
			/**
			 * A variable reference. Not inflected in any way.
			 */
			VARIABLE,
			/**
			 * Sets the current number.
			 */
			NUMERIC,
			/**
			 * Prints an inflected noun.
			 */
			NOUN
		}

		/**
		 * Options for a numeric directive.
		 * 
		 * @author bjculkin
		 *
		 */
		public static class NumericOptions {
			/**
			 * Increment the numeric value before doing anything
			 * with it.
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
			 * Doesn't correspond directly to the 's' option, but
			 * splitting between singular zero and using 'no' for
			 * zero is useful.
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
			 * Threshold for when to stop printing the number as a
			 * cardinal.
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
			 * Threshold for when to stop printing the number as an
			 * ordinal.
			 * 
			 * If the current count is greater than
			 * 'cardinalThresh', ordinals like 1st and 2nd will be
			 * printed instead of first and second.
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
			 * Mark the summarization as occurring at the end of the
			 * string, regardless of its current position.
			 */
			public boolean atEnd = false;

			// Emit error message
			private static String error(int curPos, int i, String msg, Object... props) {
				return String.format("%s (at position %d in # directive starting at %d)",
						String.format(msg, props), curPos + i, curPos);
			}

			/**
			 * Create a new set of numeric options from a string.
			 * 
			 * @param options
			 *                The string to create options from.
			 * @param curPos
			 *                The current position into the string.
			 * @param parseErrors
			 *                The current list of parsing errors.
			 */
			public NumericOptions(String options, int curPos, List<String> parseErrors) {
				if (options.equals("")) return;

				char prevOption = ' ';
				StringBuilder currNum = new StringBuilder();

				boolean doingCaseFolding = false;

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
			 * 
			 */
			public NumericOptions() {
			}

			private void parseNumericParam(int curPos, List<String> parseErrors, char prevOption,
					StringBuilder currNum, int i) {
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
		}

		/**
		 * Options for a noun directive.
		 * 
		 * @author bjculkin
		 *
		 */
		public static class NounOptions {
			/**
			 * Use the classical inflection for the noun.
			 */
			public boolean classical;

			/**
			 * Inflect as plural, regardless of current count.
			 */
			public boolean plural;

			/**
			 * Inflect as singular, regardless of current count.
			 */
			public boolean singular;

			/**
			 * Create a new set of noun options from a string.
			 * 
			 * @param options
			 *                The string to create options from.
			 * @param curPos
			 *                The current position into the string.
			 * @param parseErrors
			 *                The current list of parsing errors.
			 */
			public NounOptions(String options, int curPos, List<String> parseErrors) {
				if (options.equals("")) return;

				boolean doingCaseFolding = false;

				for (int i = 0; i < options.length(); i++) {
					char ci = options.charAt(i);

					if (doingCaseFolding && Character.isLowerCase(ci)) {
						continue;
					} else if (Character.isUpperCase(ci)) {
						doingCaseFolding = true;

						ci = Character.toLowerCase(ci);
					}

					switch (ci) {
					case 'c':
						classical = true;
						break;
					case 'p':
						plural = true;
						break;
					case 's':
						singular = true;
						break;
					default:
						parseErrors.add(error(curPos, i, "Unhandled option %c", ci));
					}
				}
			}

			/**
			 * 
			 */
			public NounOptions() {
			}

			// Emit error message
			private static String error(int curPos, int i, String msg, Object... props) {
				return String.format("%s (at position %d in N directive starting at %d)",
						String.format(msg, props), curPos + i, curPos);
			}
		}

		/**
		 * The type of the directive.
		 */
		public final DirectiveType type;

		/**
		 * The string value of the directive.
		 * 
		 * Currently, set for literals and variable references, as well
		 * as nouns.
		 */
		public String litString;

		/**
		 * The integer value of the directive.
		 * 
		 * Currently set for numeric values.
		 */
		public int numNumber;

		/**
		 * Is this directives body referencing a variable instead of a
		 * literal?
		 */
		public boolean isVRef = false;

		/**
		 * The options for a numeric directive.
		 */
		public NumericOptions numOpts = new NumericOptions();

		/**
		 * The options for a noun directive.
		 */
		public NounOptions nounOpts = new NounOptions();

		/**
		 * Create a new inflection directive.
		 * 
		 * @param type
		 *                The type of the directive.
		 */
		public InflectionDirective(DirectiveType type) {
			this.type = type;

			switch (type) {
			default:
				throw new IllegalArgumentException(
						"Unhandled or wrong arguments (none) for directive type " + type);
			}
		}

		/**
		 * Create a new inflection directive.
		 * 
		 * @param type
		 *                The type of the directive.
		 * @param strang
		 *                The string value for the directive.
		 */
		public InflectionDirective(DirectiveType type, String strang) {
			this.type = type;

			switch (type) {
			case LITERAL:
			case VARIABLE:
			case NUMERIC: // Reference to a numeric variable
			case NOUN:
				this.litString = strang;
				break;
			default:
				throw new IllegalArgumentException(
						"Unhandled or wrong arguments (1 string) for directive type " + type);

			}
		}

		/**
		 * Create a new inflection directive.
		 * 
		 * @param type
		 *                The type of the directive.
		 * @param num
		 *                The number value for the directive.
		 */
		public InflectionDirective(DirectiveType type, int num) {
			this.type = type;

			switch (type) {
			case NUMERIC:
				this.numNumber = num;
				break;
			default:
				throw new IllegalArgumentException(
						"Unhandled or wrong arguments (1 number) for directive type " + type);

			}
		}

		/**
		 * Create a new literal directive.
		 * 
		 * @param strang
		 *                The literal string the directive represents.
		 * @return A literal directive for the given string.
		 */
		public static InflectionDirective literal(String strang) {
			return new InflectionDirective(DirectiveType.LITERAL, strang);
		}

		/**
		 * Create a new variable directive.
		 * 
		 * @param strang
		 *                The name of the variable to interpolate into
		 *                the string.
		 * @return A directive that says to interpolate the given value.
		 */
		public static InflectionDirective variable(String strang) {
			return new InflectionDirective(DirectiveType.VARIABLE, strang);
		}

		/**
		 * Create a new numeric directive.
		 * 
		 * @param num
		 *                The value of the directive,
		 * @return A directive that sets the current number to the
		 *         specific value.
		 */
		public static InflectionDirective numeric(int num) {
			return new InflectionDirective(DirectiveType.NUMERIC, num);
		}

		/**
		 * Create a new numeric directive.
		 * 
		 * @param strang
		 *                The name of a variable that holds the value of
		 *                the directive,
		 * @return A directive that sets the current number to the
		 *         specific value.
		 */
		public static InflectionDirective numeric(String strang) {
			return new InflectionDirective(DirectiveType.NUMERIC, strang);
		}

		/**
		 * Create a new noun directive.
		 * 
		 * @param strang
		 *                The noun, or the name of the variable for the
		 *                noun.
		 * @return A directive that inflects the specified noun.
		 */
		public static InflectionDirective noun(String strang) {
			return new InflectionDirective(DirectiveType.NOUN, strang);
		}

		/**
		 * Set the numeric options for this directive.
		 * 
		 * @param numOpts
		 *                The numeric options of the directive.
		 * @return The directive.
		 */
		public InflectionDirective options(NumericOptions numOpts) {
			this.numOpts = numOpts;

			return this;
		}

		/**
		 * Set the noun options for this directive.
		 * 
		 * @param nounOpts
		 *                The noun options of the directive.
		 * @return The directive.
		 */
		public InflectionDirective options(NounOptions nounOpts) {
			this.nounOpts = nounOpts;

			return this;
		}
	}

	// Marker for finding articles to replace
	private static Pattern AN_MARKER = Pattern.compile("\\{an(\\d+)\\}");

	// Noun storage
	private static Nouns nounDB;

	/* Load DBs from files. */
	static {
		final Prepositions prepositionDB = new Prepositions();
		prepositionDB.loadFromStream(InflectionML.class.getResourceAsStream("/prepositions.txt"));

		nounDB = new Nouns(prepositionDB);
		nounDB.loadFromStream(InflectionML.class.getResourceAsStream("/nouns.txt"));
	}

	/*
	 * The directives that make up the string.
	 */
	private List<InflectionDirective> dirs;

	/*
	 * String we were formed from.
	 */
	private String rawString;
	
	/**
	 * Create a new empty inflection string.
	 */
	public InflectionString() {
		dirs = new ArrayList<>();
	}

	/**
	 * Create a new compiled inflection string.
	 * 
	 * @param inp
	 *                The string to compile.
	 */
	public InflectionString(String inp) {
		this();

		rawString = inp;
		
		int curPos = 0;

		List<String> parseErrors = new ArrayList<>();

		// Split input on spaces, preserving the delimiters
		for (String strang : inp.split("(?<=\\s+)|(?=\\s+)")) {
			InflectionDirective dir = literal("<ERRROR>");

			// Variables start with $
			if (strang.startsWith("$")) {
				dir = variable(strang.substring(1));
				dir.isVRef = true;
				// A string starting with $ can be escaped
			} else if (strang.startsWith("\\$")) {
				dir = literal(strang.substring(2));
			} else if (strang.startsWith("<") && strang.endsWith(">")) {
				String dirBody = strang.substring(2, strang.length() - 1);

				int idx = dirBody.indexOf(":");
				if (idx == -1) parseErrors.add(error(strang, curPos, "Missing body for %c directive",
						strang.charAt(1)));

				String options = dirBody.substring(0, idx);
				dirBody = dirBody.substring(idx + 1);

				switch (strang.charAt(1)) {
				case '#': {
					NumericOptions numOpts = new NumericOptions(options, curPos, parseErrors);

					if (dirBody.startsWith("$")) {
						dir = numeric(dirBody.substring(1));
						dir.isVRef = true;
					} else {
						try {
							dir = numeric(Integer.parseInt(dirBody));
						} catch (NumberFormatException nfex) {
							parseErrors.add(error(strang, curPos,
									"Non-integer parameter '%s' to # directive",
									dirBody));
						}
					}

					dir.options(numOpts);
				}
					break;
				case 'N': {
					NounOptions nounOpts = new NounOptions(options, curPos, parseErrors);

					if (dirBody.startsWith("$")) {
						dir = noun(dirBody.substring(1));
						dir.isVRef = true;
					} else {
						dir = noun(dirBody);
					}

					dir.options(nounOpts);
				}
					break;
				default:
					parseErrors.add(error(strang, curPos, "Unhandled directive type %c",
							strang.charAt(1)));
				}
			} else if (strang.startsWith("\\<")) {
				dir = literal(strang.substring(2));
			} else {
				dir = literal(strang);
			}

			if (dir != null) dirs.add(dir);

			// Bump forward position.
			curPos += strang.length();
		}

	}

	// Emit an error message
	private static String error(String substr, int curPos, String msg, Object... props) {
		return String.format("%s (starting at position %d inside part %s)", String.format(msg, props), curPos,
				substr);
	}

	/**
	 * Execute inflection of the string.
	 * 
	 * @param vars
	 *                The variables to insert into the string.
	 * 
	 * @return The inflected form of the string.
	 */
	public String inflect(Object... vars) {
		Map<String, Object> mep = new HashMap<>();

		int i = 0;
		for (Object var : vars) {
			mep.put(Integer.toString(++i), var);
		}

		return inflect(mep);
	}

	/**
	 * Execute inflection of the string.
	 * 
	 * @param vars
	 *                The variables to insert into the string.
	 * 
	 * @return The inflected form of the string.
	 */
	public String inflect(Map<String, Object> vars) {
		StringBuilder sb = new StringBuilder();

		int curNum = 0;
		int anNum = 0;

		boolean inflectSingular = false;
		boolean pendingAn = false;

		List<String> anVals = new ArrayList<>();
		
		for (InflectionDirective dir : dirs) {
			switch (dir.type) {
			case LITERAL:
				sb.append(dir.litString);
				break;
			case VARIABLE: {
				String vName = dir.litString;

				if (vars.containsKey(vName)) {
					sb.append(vars.get(vName));
				} else {
					throw new IllegalArgumentException("Unbound variable " + vName);
				}

				break;
			}
			case NUMERIC: {
				int actNum;

				if (dir.isVRef) {
					Object val = vars.get(dir.litString);

					if (!(val instanceof Integer))
						throw new IllegalArgumentException("Non-numeric variable "
								+ dir.litString + " passed to # directive");

					actNum = (Integer) val;
				} else {
					actNum = dir.numNumber;
				}

				curNum = actNum;
				{
					NumericOptions opts = dir.numOpts;
					String rep = Integer.toString(curNum);

					if (opts.increment) curNum += opts.incrementAmt;
					if (curNum == 1) {
						inflectSingular = true;
					} else if (curNum == 0 && opts.singular) {
						inflectSingular = true;
					} else {
						inflectSingular = false;
					}

					if (opts.zeroNo && curNum == 0) rep = "no";
					
					if (opts.article && curNum == 1) {
						anNum += 1;
						rep = "{an" + anNum + "}";

						pendingAn = true;
					}

					if (opts.nonPrint) break;

					boolean override = true;
					if (rep.equals("no") || rep.matches("\\{an\\d+\\}")) {
						override = false;
					}

					if (override) {
						if (opts.cardinal) {
							rep = NumberUtils.toCardinal(curNum, opts.cardinalThresh);
						}

						if (opts.ordinal) {
							if (opts.cardinal) {
								rep = NumberUtils.toOrdinal(curNum, opts.ordinalThresh,
										curNum < opts.cardinalThresh);
							} else {
								rep = NumberUtils.toOrdinal(curNum, opts.ordinalThresh,
										false);
							}

							if (curNum < opts.ordinalThresh) {
								// Respect english usage of ordinals
								curNum = 1;

								inflectSingular = true;
							}
						}

						if (opts.summarize) {
							rep = NumberUtils.summarizeNumber(curNum, opts.atEnd);
						}
					}

					sb.append(rep);
				}

				break;
			}
			case NOUN: {
				String actNoun;
				
				if (dir.isVRef) {
					Object val = vars.get(dir.litString);

					if (!(val instanceof String))
						throw new IllegalArgumentException("Non-numeric variable "
								+ dir.litString + " passed to # directive");

					actNoun = (String) val;
				} else {
					actNoun = dir.litString;
				}
				
				final Noun noun = nounDB.getNoun(actNoun);

				String nounVal;

				if (dir.nounOpts.plural || !inflectSingular) {
					if (dir.nounOpts.classical) {
						nounVal = noun.classicalPlural();
					} else {
						nounVal = noun.plural();
					}
				} else {
					nounVal = noun.singular();
				}

				sb.append(nounVal);
				if (pendingAn) {
					anVals.add(EnglishUtils.pickIndefinite(nounVal));

					pendingAn = false;
				}
			}
				break;
			default:
				throw new IllegalArgumentException("Unhandled directive type " + dir.type);
			}
		}

		String res = sb.toString();
		
		StringBuffer work = new StringBuffer();
		
		Matcher anMat = AN_MARKER.matcher(res);
		
		Iterator<String> anItr = anVals.iterator();
		while (anMat.find()) {
			anMat.appendReplacement(work, anItr.next());
		}
		anMat.appendTail(work);
		
		return work.toString();
	}
	
	@Override
	public String toString() {
		if (rawString != null) return rawString;
		else return super.toString();
	}
}
