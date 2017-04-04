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

import java.util.Scanner;

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

		System.out.print("Enter a noun to inflect (blank line to quit): ");
		String ln = scn.nextLine().trim();

		while(!ln.equals("")) {
			System.out.println();

			Noun noun = nounDB.getNoun(ln);

			if(noun == null) {
				System.out.println("No inflection available for noun " + ln);
			} else {
				System.out.printf(OUTPUT_FMT, ln, noun.singular(), noun.modernPlural(),
						noun.classicalPlural());
			}

			System.out.print("Enter a noun to inflect (blank line to quit): ");
			ln = scn.nextLine().trim();
		}

		scn.close();
	}
}