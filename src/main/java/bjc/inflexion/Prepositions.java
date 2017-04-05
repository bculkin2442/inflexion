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

import java.io.InputStream;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

/**
 * List of prepositions.
 * 
 * @author EVE
 *
 */
public class Prepositions {
	private Set<String> prepositions;

	/**
	 * Create an empty preposition DB.
	 */
	public Prepositions() {
		prepositions = new HashSet<>();
	}

	/**
	 * Check if a word is a preposition.
	 * 
	 * @param word
	 *                The word as a preposition.
	 * 
	 * @return Whether or not the word is a preposition.
	 */
	public boolean isPreposition(String word) {
		return prepositions.contains(word);
	}

	/**
	 * Load the contents of the stream into this DB.
	 * 
	 * @param stream
	 *                The stream to load from.
	 */
	public void loadFromStream(InputStream stream) {
		try(Scanner scn = new Scanner(stream)) {
			while(scn.hasNextLine()) {
				String ln = scn.nextLine().trim();

				/*
				 * Ignore comments
				 */
				if(ln.startsWith("#")) continue;

				prepositions.add(ln);
			}
		}
	}
}