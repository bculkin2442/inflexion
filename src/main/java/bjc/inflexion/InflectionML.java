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

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bjc.inflexion.examples.InflexionTester;
import bjc.inflexion.nouns.InflectionException;
import bjc.inflexion.nouns.Noun;
import bjc.inflexion.nouns.Nouns;
import bjc.inflexion.nouns.Prepositions;

/**
 * @author student
 *
 */
public class InflectionML {
	private static final List<String>	ESUB_OPT	= Arrays.asList("a", "s", "w");
	private static Pattern			FORM_MARKER	= Pattern
			.compile("<(?<command>[#N])(?<options>[^:]*):(?<text>[^>]*)>");

	private static Nouns nounDB;

	static {
		Prepositions prepositionDB = new Prepositions();
		prepositionDB.loadFromStream(InflexionTester.class.getResourceAsStream("/prepositions.txt"));

		nounDB = new Nouns(prepositionDB);
		nounDB.loadFromStream(InflexionTester.class.getResourceAsStream("/nouns.txt"));
	}

	/**
	 * Apply inflection to marked forms in the string.
	 * 
	 * @param form
	 *                The string to inflect.
	 * 
	 * @return The inflected string.
	 */
	public static String inflect(String form) {
		Matcher formMatcher = FORM_MARKER.matcher(form);

		StringBuffer formBuffer = new StringBuffer();

		int curCount = 1;
		boolean inflectSingular = true;

		while (formMatcher.find()) {
			String command = formMatcher.group("command");
			String options = formMatcher.group("options");
			String text = formMatcher.group("text");

			Set<String> optionSet = new HashSet<>();
			for (int i = 1; i <= options.length(); i++) {
				optionSet.add(options.substring(i - 1, i));
			}

			switch (command) {
			case "#":
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
						if (curCount == 0 && optionSet.contains("s"))
							inflectSingular = true;
						else inflectSingular = false;
					} else {
						inflectSingular = true;
					}

					/*
					 * Break out of switch.
					 */
					if (optionSet.contains("d")) {
						formMatcher.appendReplacement(formBuffer, "");
						break;
					}

					String rep = text;

					if (optionSet.contains("n")) {
						if (curCount == 0) rep = "no";
					}

					if (optionSet.contains("s")) {
						if (curCount == 0) {
							rep = "no";
						}
					}

					if (optionSet.contains("a")) {
						/*
						 * TODO implement a/an for nouns
						 */
					}

					boolean shouldOverride = !(rep.equals("no") || rep.equals("a")
							|| rep.equals("an"));

					if (optionSet.contains("w") && shouldOverride) {
						rep = EnglishUtils.smallIntToWord(curCount);
					}

					if (optionSet.contains("f") && shouldOverride) {
						rep = EnglishUtils.intSummarize(curCount, false);
					}

					formMatcher.appendReplacement(formBuffer, rep);
				} catch (NumberFormatException nfex) {
					throw new InflectionException("Count setter must take a number as a parameter",
							nfex);
				}
				break;
			case "N":
				Noun noun = nounDB.getNoun(text);
				if (optionSet.contains("p") || !inflectSingular) {
					if (optionSet.contains("c")) {
						formMatcher.appendReplacement(formBuffer, noun.classicalPlural());
					} else {
						formMatcher.appendReplacement(formBuffer, noun.modernPlural());
					}
				} else {
					formMatcher.appendReplacement(formBuffer, noun.singular());
				}
				break;
			default:
				String msg = String.format("Unknown command '%s'", command);

				throw new InflectionException(msg);
			}
		}

		formMatcher.appendTail(formBuffer);

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
	 * @return The string, formatted & inflected.
	 */
	public static String iprintf(String format, Object... objects) {
		return inflect(String.format(format, objects));
	}
}
