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

import static bjc.inflexion.InflectionDirective.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
	// Create an iterable from an iterator
	private static Iterable<String> I(Iterator<String> itr) {
		return () -> itr;
	}

	// Marker for finding articles to replace
	private static Pattern AN_MARKER = Pattern.compile("\\{an(\\d+)\\}");

	// Noun storage
	private static Nouns nounDB;

	/* Load DBs from files. */
	static {
		final Prepositions prepositionDB = new Prepositions();
		try (InputStream strim
				= InflectionML.class.getResourceAsStream("/prepositions.txt")) {
			prepositionDB.loadFromStream(strim);
		} catch (IOException ioex) {
			ioex.printStackTrace();
		}

		nounDB = new Nouns(prepositionDB);
		try (InputStream strim = InflectionML.class.getResourceAsStream("/nouns.txt")) {
			nounDB.loadFromStream(strim);
		} catch (IOException ioex) {
			ioex.printStackTrace();
		}
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
	 *            The string to compile.
	 */
	public InflectionString(String inp) {
		this();

		rawString = inp;

		int curPos = 0;

		List<String> parseErrors = new ArrayList<>();

		// Split input on spaces, preserving the delimiters
		// for (String strang : inp.split("(?<=\\s+)|(?=\\s+)")) {
		for (String strang : I(new DirectiveIterator(inp))) {
			InflectionDirective dir = literal("<ERRROR>");

			// Variables start with $
			if (strang.startsWith("$")) {
				dir = variable(strang.substring(1));
				dir.isVRef = true;
			} else if (strang.startsWith("<") && strang.endsWith(">")) {
				String dirBody = strang.substring(2, strang.length() - 1);
				char dirName = strang.charAt(1);

				int idx = dirBody.indexOf(":");
				if (idx == -1)
					parseErrors.add(error(strang, curPos, "Missing body for %c directive",
							dirName));

				String options = dirBody.substring(0, idx);
				dirBody = dirBody.substring(idx + 1);

				boolean startFold = false;
				if (Character.isUpperCase(dirName)) {
					startFold = true;
				}
				switch (dirName) {
				case '#': {
					NumericOptions numOpts
							= new NumericOptions(options, curPos, startFold, parseErrors);

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
				case 'n':
				case 'N': {
					NounOptions nounOpts = new NounOptions(options, curPos, startFold, parseErrors);

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
							dirName));
				}
			} else {
				dir = literal(strang);
			}

			if (dir != null)
				dirs.add(dir);

			// Bump forward position.
			curPos += strang.length();
		}

		if (!parseErrors.isEmpty())
			throw new InflectionFormatException(inp, parseErrors);
	}

	// Emit an error message
	private static String error(String substr, int curPos, String msg, Object... props) {
		return String.format("%s (starting at position %d inside part %s)",
				String.format(msg, props), curPos, substr);
	}

	/**
	 * Execute inflection of the string.
	 *
	 * @param vars
	 *             The variables to insert into the string.
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
	 *             The variables to insert into the string.
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

		QueuedIterator<InflectionDirective> itrDirs = new QueuedIterator<>(dirs);
		Iterable<InflectionDirective> itrb = () -> itrDirs;
		for (InflectionDirective dir : itrb) {
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

			}
				break;
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
					NumericOptions opts = (NumericOptions) dir.opts;
					String rep = Integer.toString(curNum);

					if (opts.increment)
						curNum += opts.incrementAmt;
					if (curNum == 1) {
						inflectSingular = true;
					} else if (curNum == 0 && opts.singular) {
						inflectSingular = true;
					} else {
						inflectSingular = false;
					}

					if (opts.zeroNo && curNum == 0)
						rep = "no";

					if (opts.article && curNum == 1) {
						anNum += 1;
						rep = "{an" + anNum + "}";

						pendingAn = true;
					}

					if (opts.nonPrint)
						break;

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

			}
				break;
			case NOUN: {
				NounOptions nounOpts = (NounOptions) dir.opts;

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

				if (nounOpts.plural || !inflectSingular) {
					if (nounOpts.classical) {
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
			case SEQ:
				itrDirs.before(dir.listDir);
				break;
			default:
				throw new IllegalArgumentException(
						"Unhandled directive type " + dir.type);
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
		if (rawString != null)
			return rawString;

		return super.toString();
	}
}
