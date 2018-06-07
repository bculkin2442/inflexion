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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bjc.inflexion.nouns.InflectionException;
import bjc.inflexion.nouns.Noun;
import bjc.inflexion.nouns.Nouns;
import bjc.inflexion.nouns.Prepositions;

/*
 * @TODO 10/11/17 Ben Culkin :InflectionML
 * 	Complete the implementation of this from the documentation for
 * 	Lingua::EN:Inflexion
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
	private static Pattern FORM_MARKER =
		Pattern.compile("<(?<command>[#N])(?<options>[^:]*):(?<text>[^>]*)>");

	private static Pattern AN_MARKER = Pattern.compile("\\{an(\\d+)\\}");

	/* The database of nouns. */
	private static Nouns nounDB;

	/* Load DBs from files. */
	static {
		final Prepositions prepositionDB = new Prepositions();
		prepositionDB.loadFromStream(InflectionML.class.getResourceAsStream("/prepositions.txt"));

		nounDB = new Nouns(prepositionDB);
		nounDB.loadFromStream(InflectionML.class.getResourceAsStream("/nouns.txt"));
	}

	/**
	 * Apply inflection to marked forms in the string.
	 *
	 * @param form
	 * 	The string to inflect.
	 *
	 * @return 
	 * 	The inflected string.
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
			final String text    = formMatcher.group("text");

			final Set<String> optionSet = new HashSet<>();

			for (int i = 1; i <= options.length(); i++) {
				optionSet.add(options.substring(i - 1, i));
			}

			switch (command) {
			case "#":
				/* @NOTE
				 * 	These should maybe be moved into their
				 * 	own function. This will also allow the
				 * 	use of custom inflection forms.
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
						formMatcher.appendReplacement(formBuffer, rep);
						break;
					}
					
					final boolean shouldOverride = !(rep.equals("no") || rep.matches("\\{an\\d+\\}"));

					if (optionSet.contains("w") && shouldOverride) {
						rep = EnglishUtils.smallIntToWord(curCount);
					}

					if (optionSet.contains("f") && shouldOverride) {
						rep = EnglishUtils.intSummarize(curCount, false);
					}

					formMatcher.appendReplacement(formBuffer, rep);
				} catch (final NumberFormatException nfex) {
					throw new InflectionException("Count setter must take a number as a parameter",
					                              nfex);
				}
				break;
			case "N":
				final Noun noun = nounDB.getNoun(text);

				String nounVal;

				if (optionSet.contains("p") || !inflectSingular) {
					if (optionSet.contains("c")) {
						nounVal = noun.classicalPlural();
					} else {
						nounVal = noun.modernPlural();
					}
				} else {
					nounVal = noun.singular();
				}

				formMatcher.appendReplacement(formBuffer, nounVal);
				if(pendingAN) {
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
		while(anMat.find()) {
			anMat.appendReplacement(formBuffer, anItr.next());
		}
		anMat.appendTail(formBuffer);

		return formBuffer.toString();
	}

	/**
	 * Alias method to format a string, then inflect it.
	 *
	 * @param format
	 * 	The combined format/inflection string.
	 *
	 * @param objects
	 * 	The parameters for the format string.
	 *
	 * @return
	 * 	The string, formatted &amp; inflected.
	 */
	public static String iprintf(final String format, final Object... objects) {
		return inflect(String.format(format, objects));
	}
}
