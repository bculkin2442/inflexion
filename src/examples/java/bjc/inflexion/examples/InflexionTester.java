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
package bjc.inflexion.examples;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;

import bjc.inflexion.InflectionML;
import bjc.inflexion.nouns.Nouns;
import bjc.inflexion.nouns.Prepositions;

/**
 * Test inflecting words.
 *
 * @author EVE
 *
 */
public class InflexionTester {
	/**
	 * Main method.
	 *
	 * @param args
	 *                Unused CLI args.
	 */
	public static void main(final String[] args) {
		final Prepositions prepositionDB = new Prepositions();
		prepositionDB.loadFromStream(InflexionTester.class.getResourceAsStream("/prepositions.txt"));

		final Nouns nounDB = new Nouns(prepositionDB);
		nounDB.loadFromStream(InflexionTester.class.getResourceAsStream("/nouns.txt"));

		final Scanner scn = new Scanner(System.in);

		System.out.print("Enter a string to inflect (blank line to quit): ");
		String ln = scn.nextLine().trim();

		while (!ln.equals("")) {
			System.out.println();

			final String inflected = InflectionML.inflect(ln);

			System.out.println("Inflected string: " + inflected);

			System.out.print("\nEnter a string to inflect (blank line to quit): ");
			ln = scn.nextLine().trim();
		}

		scn.close();
	}

	@SuppressWarnings("unused")
	private static void wikitest(final Scanner scn, final Nouns nounDB) {
		System.out.print("Enter name of dump file: ");

		final String fname = scn.nextLine().trim();

		try (InputStream compressedStream = new FileInputStream(fname)) {
			final InputStream stream = new BZip2CompressorInputStream(compressedStream);
			final BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

			/*
			 * Pattern find word name
			 */
			final Pattern titlePattern = Pattern.compile("<title>([^<]+)</title>");
			/*
			 * Pattern to find beginning of wiki text
			 */
			final Pattern textPattern = Pattern.compile("<text");
			/*
			 * Pattern to find rank definition
			 */
			final Pattern rankPattern = Pattern.compile("\\{\\{rank");
			/*
			 * Pattern to find noun definition
			 */
			final Pattern enNounPattern = Pattern
					.compile("\\{\\{en-noun([a-z0-9\\|\\-\\[\\]\\?\\!=]*)\\}\\}");

			final Pattern wordPattern = Pattern.compile("([a-zA-Z\\-]+)");

			String line;
			String word = "";
			int text = 0;
			int count = 0;
			int basicCount = 0;
			int wrong = 0;
			int basicWrong = 0;
			int wrongNoPlural = 0;
			int wrongUncountable = 0;
			boolean basicWord = false;
			while ((line = reader.readLine()) != null) {
				final Matcher titleMatcher = titlePattern.matcher(line);
				if (titleMatcher.find()) {
					word = titleMatcher.group(1);
					if (word.startsWith("Wiktionary:")) {
						continue;
					}
					basicWord = false;
					text = 0;
					continue;
				}
				final Matcher textMatcher = textPattern.matcher(line);
				if (textMatcher.find()) {
					text++;
					continue;
				}
				final Matcher rankMatcher = rankPattern.matcher(line);
				if (rankMatcher.find()) {
					basicWord = true;
					basicCount++;
				}
				if (text != 1) {
					continue;
				}
				final Matcher enNounMatcher = enNounPattern.matcher(line);
				if (enNounMatcher.find()) {
					// only first
					/*
					 * if (text != 1) { continue; }
					 */
					text++;
					count++;
					if (count % 5000 == 0) {
						System.out.println(count);
					}
					final String[] rules = enNounMatcher.group(1).split("\\|");
					final List<String> plurals = new ArrayList<>();

					boolean uncountable = false;
					boolean noPlural = false;
					for (final String rule : rules) {
						if (rule.isEmpty()) {
							continue;
						}
						if ("-".equals(rule)) {
							plurals.add(word);
							uncountable = true;
						} else if ("s".equals(rule)) {
							plurals.add(word + "s");
						} else if ("es".equals(rule)) {
							plurals.add(word + "es");
						} else if ("!".equals(rule)) {
							plurals.add("plural not attested");
							uncountable = true;
						} else if ("?".equals(rule)) {
							plurals.add("unknown");
							noPlural = true;
						} else {
							final Matcher matcher = wordPattern.matcher(rule);
							if (matcher.matches()) {
								plurals.add(rule);
							}
						}
					}
					if (plurals.isEmpty()) {
						plurals.add(word + "s");
					}

					final String calculatedPlural = nounDB.getNoun(word).plural();
					boolean ok = false;
					for (final String plural : plurals) {
						if (plural.equals(calculatedPlural)) {
							ok = true;
							break;
						}
					}

					if (!ok) {
						if (!uncountable) {
							wrong++;
						}
						if (uncountable) {
							wrongUncountable++;
						} else if (noPlural) {
							wrongNoPlural++;
						}
						if (basicWord) {
							System.out.println("basic word: " + word + " got: "
									+ calculatedPlural + ", but expected "
									+ enNounMatcher.group(1));
							basicWrong++;
						} else if (!uncountable) {
							System.out.println(word + " got: " + calculatedPlural
									+ ", but expected " + enNounMatcher.group(1));
						}
					}
				}
			}
			reader.close();
			compressedStream.close();

			final float correct = (count - wrong) * 100 / (float) count;
			final float basicCorrect = (basicCount - basicWrong) * 100 / (float) basicCount;
			final float wrongNoPluralPercent = wrongNoPlural * 100 / (float) count;
			final int justPlainWrong = wrong - wrongNoPlural;
			final float justPlainWrongPercent = justPlainWrong * 100 / (float) count;
			System.out.println("Words checked: " + count + " (" + basicCount + " basic words)");
			System.out.println("Correct: " + correct + "% (" + basicCorrect + "% basic words)");
			System.out.println("Errors: ");
			System.out.println("    No plural form specified: " + wrongNoPlural + " ("
					+ wrongNoPluralPercent + "%)");
			System.out.println("    Incorrect answer: " + justPlainWrong + " (" + justPlainWrongPercent
					+ "%)");
		} catch (final FileNotFoundException fnfex) {
			fnfex.printStackTrace();
		} catch (final IOException ioex) {
			ioex.printStackTrace();
		}
	}
}