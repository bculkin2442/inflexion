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

import bjc.inflexion.v2.Noun;
import bjc.inflexion.v2.Nouns;
import bjc.inflexion.v2.Prepositions;

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

/**
 * Test inflecting words.
 * 
 * @author EVE
 *
 */
public class InflexionTester {
	private static final String OUTPUT_FMT = "Word: %s\n\tSingular: %s\n\tModern Plural: %s"
			+ "\n\tClassical Plural: %s\n\n";

	/**
	 * Main method.
	 * 
	 * @param args
	 *                Unused CLI args.
	 */
	public static void main(String[] args) {
		Prepositions prepositionDB = new Prepositions();
		prepositionDB.loadFromStream(InflexionTester.class.getResourceAsStream("/prepositions.txt"));

		Nouns nounDB = new Nouns(prepositionDB);
		nounDB.loadFromStream(InflexionTester.class.getResourceAsStream("/nouns.txt"));

		Scanner scn = new Scanner(System.in);

		wikitest(scn, nounDB);

		/*
		 * System.out.
		 * print("Enter a noun to inflect (blank line to quit): ");
		 * String ln = scn.nextLine().trim();
		 * 
		 * while(!ln.equals("")) { System.out.println();
		 * 
		 * Noun noun = nounDB.getNoun(ln);
		 * 
		 * if(noun == null) {
		 * System.out.println("No inflection available for noun " + ln);
		 * } else { System.out.printf(OUTPUT_FMT, ln, noun.singular(),
		 * noun.modernPlural(), noun.classicalPlural()); }
		 * 
		 * System.out.
		 * print("Enter a noun to inflect (blank line to quit): "); ln =
		 * scn.nextLine().trim(); }
		 */

		scn.close();
	}

	@SuppressWarnings("unused")
	private static void wikitest(Scanner scn, Nouns nounDB) {
		System.out.print("Enter name of dump file: ");

		String fname = scn.nextLine().trim();

		try(InputStream compressedStream = new FileInputStream(fname)) {
			InputStream stream = new BZip2CompressorInputStream(compressedStream);
			BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

			/*
			 * Pattern find word name
			 */
			Pattern titlePattern = Pattern.compile("<title>([^<]+)</title>");
			/*
			 * Pattern to find beginning of wiki text
			 */
			Pattern textPattern = Pattern.compile("<text");
			/*
			 * Pattern to find rank definition
			 */
			Pattern rankPattern = Pattern.compile("\\{\\{rank");
			/*
			 * Pattern to find noun definition
			 */
			Pattern enNounPattern = Pattern.compile("\\{\\{en-noun([a-z0-9\\|\\-\\[\\]\\?\\!=]*)\\}\\}");

			Pattern wordPattern = Pattern.compile("([a-zA-Z\\-]+)");

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
			while((line = reader.readLine()) != null) {
				Matcher titleMatcher = titlePattern.matcher(line);
				if(titleMatcher.find()) {
					word = titleMatcher.group(1);
					if(word.startsWith("Wiktionary:")) {
						continue;
					}
					basicWord = false;
					text = 0;
					continue;
				}
				Matcher textMatcher = textPattern.matcher(line);
				if(textMatcher.find()) {
					text++;
					continue;
				}
				Matcher rankMatcher = rankPattern.matcher(line);
				if(rankMatcher.find()) {
					basicWord = true;
					basicCount++;
				}
				if(text != 1) {
					continue;
				}
				Matcher enNounMatcher = enNounPattern.matcher(line);
				if(enNounMatcher.find()) {
					// only first
					/*
					 * if (text != 1) { continue; }
					 */
					text++;
					count++;
					if(count % 5000 == 0) {
						System.out.println(count);
					}
					String[] rules = enNounMatcher.group(1).split("\\|");
					List<String> plurals = new ArrayList<>();

					boolean uncountable = false;
					boolean noPlural = false;
					for(String rule : rules) {
						if(rule.isEmpty()) {
							continue;
						}
						if("-".equals(rule)) {
							plurals.add(word);
							uncountable = true;
						} else if("s".equals(rule)) {
							plurals.add(word + "s");
						} else if("es".equals(rule)) {
							plurals.add(word + "es");
						} else if("!".equals(rule)) {
							plurals.add("plural not attested");
							uncountable = true;
						} else if("?".equals(rule)) {
							plurals.add("unknown");
							noPlural = true;
						} else {
							Matcher matcher = wordPattern.matcher(rule);
							if(matcher.matches()) {
								plurals.add(rule);
							}
						}
					}
					if(plurals.isEmpty()) {
						plurals.add(word + "s");
					}

					String calculatedPlural = nounDB.getNoun(word).plural();
					boolean ok = false;
					for(String plural : plurals) {
						if(plural.equals(calculatedPlural)) {
							ok = true;
							break;
						}
					}

					if(!ok) {
						if(!uncountable) wrong++;
						if(uncountable) {
							wrongUncountable++;
						} else if(noPlural) {
							wrongNoPlural++;
						}
						if(basicWord) {
							System.out.println("basic word: " + word + " got: "
									+ calculatedPlural + ", but expected "
									+ enNounMatcher.group(1));
							basicWrong++;
						} else if(!uncountable) {
							System.out.println(word + " got: " + calculatedPlural
									+ ", but expected " + enNounMatcher.group(1));
						}
					}
				}
			}
			reader.close();
			compressedStream.close();

			float correct = (count - wrong) * 100 / (float) count;
			float basicCorrect = (basicCount - basicWrong) * 100 / (float) basicCount;
			float wrongNoPluralPercent = wrongNoPlural * 100 / (float) count;
			int justPlainWrong = wrong - wrongNoPlural;
			float justPlainWrongPercent = justPlainWrong * 100 / (float) count;
			System.out.println("Words checked: " + count + " (" + basicCount + " basic words)");
			System.out.println("Correct: " + correct + "% (" + basicCorrect + "% basic words)");
			System.out.println("Errors: ");
			System.out.println("    No plural form specified: " + wrongNoPlural + " ("
					+ wrongNoPluralPercent + "%)");
			System.out.println("    Incorrect answer: " + justPlainWrong + " (" + justPlainWrongPercent
					+ "%)");
		} catch(FileNotFoundException fnfex) {
			fnfex.printStackTrace();
		} catch(IOException ioex) {
			ioex.printStackTrace();
		}
	}
}