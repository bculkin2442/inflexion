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
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bjc.inflexion.nouns.InflectionException;
import bjc.inflexion.nouns.Noun;
import bjc.inflexion.nouns.Nouns;

/**
 * @author student
 *
 */
public class InflectionML {
	private static Pattern FORM_MARKER = Pattern.compile("<(?<command>[#A])(?<options>[^:]*):(?<text>[^>]*)>");

	/**
	 * Apply inflection to marked forms in the string.
	 * 
	 * @param form
	 *            The string to inflect.
	 * 
	 * @param nounDB
	 *            The source to load nouns from.
	 * 
	 * @return The inflected string.
	 */
	public static String inflect(String form, Nouns nounDB) {
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
					curCount = Integer.parseInt(text);

					if (curCount != 1)
						inflectSingular = false;
					else
						inflectSingular = true;

					String rep = text;

					if (optionSet.contains("n")) {
						if (curCount == 0)
							rep = "no";
					}

					if (optionSet.contains("s")) {
						if (curCount == 0) {
							rep = "no";
							inflectSingular = true;
						}
					}

					if (optionSet.contains("a")) {
						/*
						 * TODO implement a/an for nouns
						 */
					}

					boolean shouldOverride = !(rep.equals("no") || rep.equals("a") || rep.equals("an"));
					if (optionSet.contains("w") && shouldOverride) {
						rep = EnglishUtils.smallIntToWord(curCount);
					} else if (optionSet.contains("f")) {
						rep = EnglishUtils.intSummarize(curCount, false);
					}

					formMatcher.appendReplacement(formBuffer, rep);
				} catch (NumberFormatException nfex) {
					throw new InflectionException("Count setter must take a number as a parameter", nfex);
				}

			case "N":
				Noun noun = nounDB.getNoun(text);
				if (inflectSingular || optionSet.contains("s")) {
					formMatcher.appendReplacement(formBuffer, noun.singular());
				} else if (optionSet.contains("p")) {
					if (optionSet.contains("c")) {
						formMatcher.appendReplacement(formBuffer, noun.classicalPlural());
					} else {
						formMatcher.appendReplacement(formBuffer, noun.modernPlural());
					}
				}
			default:
				String msg = String.format("Unknown command '%s'", command);

				throw new InflectionException(msg);
			}
		}

		formMatcher.appendTail(formBuffer);

		return formBuffer.toString();
	}
}
