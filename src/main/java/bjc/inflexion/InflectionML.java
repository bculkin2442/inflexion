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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bjc.inflexion.nouns.InflectionException;
import bjc.inflexion.nouns.Noun;
import bjc.inflexion.nouns.Nouns;
import bjc.inflexion.nouns.Prepositions;

/*
 * @TODO 10/11/17 Ben Culkin :InflectionML
 *
 * Complete the implementation of this from the documentation for Lingua::EN:Inflexion.
 *
 * ADDENDA 10/25/18
 * 	Everything that doesn't require doing verbs is done.
 */
/**
 * Implementation of a simple format language for inflections.
 *
 * @author student
 */
public class InflectionML {
	/* The options implied by the E option. */
	private static final List<String> ESUB_OPT = Arrays.asList("a", "s", "w");

	/* The regex that marks an inflection form. */
	private static Pattern FORM_MARKER
			= Pattern.compile("<(?<command>[#N])(?<options>[^:]*):(?<text>[^>]*)>");

	private static Pattern AN_MARKER = Pattern.compile("\\{an(\\d+)\\}");

	/* The database of nouns. */
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

	/**
	 * Apply inflection to marked forms in the string.
	 *
	 * @param form
	 *             The string to inflect.
	 *
	 * @return The inflected string.
	 */
	public static String inflect(String form) {
		Matcher formMatcher = FORM_MARKER.matcher(form);
		StringBuffer formBuffer = new StringBuffer();

		int curCount = 1;

		boolean inflectSingular = true;

		int anCount = 0;
		List<String> anVals = new ArrayList<>();

		boolean pendingAN = false;

		while (formMatcher.find()) {
			final String command = formMatcher.group("command");
			final String options = formMatcher.group("options");
			final String text = formMatcher.group("text");

			final Set<String> optionSet = new HashSet<>();

			char ch = command.charAt(0);
			boolean doCaseFold = Character.isUpperCase(ch);

			final Map<Character, Integer> numOpts = new HashMap<>();
			numOpts.put('w', 11);
			numOpts.put('o', Integer.MAX_VALUE);
			numOpts.put('f', 0);

			if (!options.equals("")) {
				if (options.matches("(?:[a-z]*[A-Z]+[a-z])+")) {
					doCaseFold = true;
				}

				char prevOption = ' ';

				StringBuilder currNum = new StringBuilder();

				for (int i = 0; i < options.length(); i++) {
					char ci = options.charAt(i);

					if (Character.isDigit(ci)) {
						currNum.append(ci);
						continue;
					}

					if (currNum.length() > 0) {
						numOpts.put(prevOption, Integer.parseInt(currNum.toString()));

						currNum = new StringBuilder();
					}

					String opt = Character.toString(ci);

					// @TODO Ben Culkin 10/14/18
					//
					// There is some weird bug that I think is related to case folding,
					// where having options with a capitalized letter followed by more
					// than 1 lowercase letter gets ignored.
					if (doCaseFold) {
						if (Character.isUpperCase(ci)) {
							System.err.printf("Case-folding '%c'\n", ci);

							opt = opt.toLowerCase();
						} else {

							System.err.printf("Ignoring '%c' due to case folding\n", ci);
							continue;
						}
					}

					prevOption = ci;
					optionSet.add(opt);
				}

				if (currNum.length() > 0) {
					numOpts.put(prevOption, Integer.parseInt(currNum.toString()));

					currNum = new StringBuilder();
				}
			}

			switch (command) {
			case "#":
				/*
				 * @NOTE These should maybe be moved into their own function. This will
				 * also allow the use of custom inflection forms.
				 */
				try {
					if (optionSet.contains("e")) {
						optionSet.remove("e");
						optionSet.addAll(ESUB_OPT);
					}

					curCount = Integer.parseInt(text);

					if (optionSet.contains("i")) {
						curCount += 1;
					}

					if (curCount != 1) {
						if (curCount == 0 && optionSet.contains("s")) {
							inflectSingular = true;
						} else {
							inflectSingular = false;
						}
					} else {
						inflectSingular = true;
					}

					String rep = text;

					if (optionSet.contains("n")) {
						if (curCount == 0) {
							rep = "no";
						}
					}

					if (optionSet.contains("s")) {
						if (curCount == 0) {
							rep = "no";
						}
					}

					if (optionSet.contains("a")) {
						if (curCount == 1) {
							anCount += 1;
							rep = "{an" + anCount + "}";

							pendingAN = true;
						}
					}

					/* Break out of switch. */
					if (optionSet.contains("d")) {
						formMatcher.appendReplacement(formBuffer, "");
						break;
					}

					boolean shouldOverride = true;
					if (rep.equals("no") || rep.matches("\\{an\\d+\\}")) {
						shouldOverride = false;
					}

					if (optionSet.contains("w") && shouldOverride) {
						rep = NumberUtils.toCardinal(curCount, numOpts.get('w'));

					}

					if (optionSet.contains("o") && shouldOverride) {
						if (optionSet.contains("w")) {
							if (curCount < numOpts.get('w'))
								rep = NumberUtils.toOrdinal(curCount, numOpts.get('o'),
										true);
							else
								rep = NumberUtils.toOrdinal(curCount, numOpts.get('o'),
										false);
						} else {
							rep = NumberUtils.toOrdinal(curCount, numOpts.get('o'),
									false);
						}

						if (curCount < numOpts.get('o')) {
							// Respect english usage of ordinals
							curCount = 1;
							inflectSingular = true;
						}
					}

					if (optionSet.contains("f") && shouldOverride) {
						rep = NumberUtils.summarizeNumber(curCount,
								numOpts.get('f') != 0);
					}

					numOpts.put('o', Integer.MAX_VALUE);
					numOpts.put('w', 11);
					numOpts.put('f', 0);

					formMatcher.appendReplacement(formBuffer, rep);
				} catch (final NumberFormatException nfex) {
					throw new InflectionException(
							"Count setter must take a number as a parameter", nfex);
				}
				break;
			case "n":
			case "N":
				final Noun noun = nounDB.getNoun(text);

				String nounVal;

				if (optionSet.contains("p") || !inflectSingular) {
					if (optionSet.contains("c")) {
						nounVal = noun.classicalPlural();
					} else {
						nounVal = noun.plural();
					}
				} else {
					nounVal = noun.singular();
				}

				formMatcher.appendReplacement(formBuffer, nounVal);
				if (pendingAN) {
					anVals.add(EnglishUtils.pickIndefinite(nounVal));

					pendingAN = false;
				}

				break;
			default:
				final String msg = String.format("Unknown command '%s'", command);

				throw new InflectionException(msg);
			}
		}

		formMatcher.appendTail(formBuffer);

		String res = formBuffer.toString();
		formBuffer = new StringBuffer();

		Matcher anMat = AN_MARKER.matcher(res);

		Iterator<String> anItr = anVals.iterator();
		while (anMat.find()) {
			anMat.appendReplacement(formBuffer, anItr.next());
		}
		anMat.appendTail(formBuffer);

		return formBuffer.toString();
	}

	/**
	 * Alias method to format a string, then inflect it.
	 *
	 * @param format
	 *                The combined format/inflection string.
	 *
	 * @param objects
	 *                The parameters for the format string.
	 *
	 * @return The string, formatted &amp; inflected.
	 */
	public static String iprintf(final String format, final Object... objects) {
		return inflect(String.format(format, objects));
	}
}
