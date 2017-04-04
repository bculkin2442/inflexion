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
package bjc.inflexion.v2;

import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Pattern;

import static bjc.inflexion.v2.InflectionAffixes.*;

/**
 * @author EVE
 *
 */
public class Nouns {
	private static final DefaultNounInflection DEFAULT_INFLECTION = new DefaultNounInflection();

	private Map<String, NounInflection>	userIrregulars;
	private List<NounInflection>		userInflections;

	private Map<String, NounInflection>	predefinedIrregulars;
	private List<NounInflection>		predefinedInflections;

	/**
	 * Create a new empty noun DB.
	 */
	public Nouns() {
		userIrregulars = new HashMap<>();
		userInflections = new LinkedList<>();

		predefinedIrregulars = new HashMap<>();
		predefinedInflections = new LinkedList<>();
	}

	/**
	 * Retrieve a noun with its inflection from the database of inflections.
	 * 
	 * @param noun
	 *                The noun to retrieve.
	 * 
	 * @return The noun with its inflection.
	 * 
	 * @throws InflectionException
	 *                 If the noun matched no inflection.
	 */
	public Noun getNoun(String noun) {
		if(userIrregulars.containsKey(noun)) return new Noun(noun, userIrregulars.get(noun));
		for(NounInflection inflect : userInflections) {
			if(inflect.matches(noun)) return new Noun(noun, inflect);
		}

		if(predefinedIrregulars.containsKey(noun)) return new Noun(noun, predefinedIrregulars.get(noun));
		for(NounInflection inflect : predefinedInflections) {
			if(inflect.matches(noun)) return new Noun(noun, inflect);
		}

		return new Noun(noun, DEFAULT_INFLECTION);
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
				 * Ignore comments and blank lines.
				 */
				if(ln.startsWith("#")) continue;
				if(ln.equals("")) continue;

				handleLine(ln);
			}
		}
	}

	private void handleLine(String ln) {
		String[] parts = ln.split(Pattern.quote("=>"));

		if(parts.length != 2) {
			String msg = String.format("Improperly formatted noun defn '%s'", ln);

			throw new InflectionException(msg);
		}

		String singular = parts[0].trim();
		String plural = parts[1].trim();

		String modernPlural = "";
		String classicalPlural = "";

		if(plural.contains("|")) {
			String[] plurals = plural.split(Pattern.quote("|"));

			if(plurals.length == 1) {
				modernPlural = plurals[0].trim();
			} else {
				modernPlural = plurals[0].trim();
				classicalPlural = plurals[1].trim();
			}

			if(modernPlural.equals("")) modernPlural = null;
			if(classicalPlural.equals("")) classicalPlural = null;
		} else {
			modernPlural = plural;
			classicalPlural = null;
		}

		if(singular.startsWith("*")) {
			handleCompletePlural(singular, modernPlural, classicalPlural);
		} else if(singular.startsWith("-")) {
			handleIncompletePlural(singular, modernPlural, classicalPlural);
		} else {
			handleIrregularPlural(singular, modernPlural, classicalPlural);
		}
	}

	private void handleIncompletePlural(String singular, String modernPlural, String classicalPlural) {
		InflectionAffix singularAffix = incomplete(singular.substring(1));

		InflectionAffix modernAffix = null;
		InflectionAffix classicalAffix = null;

		if(modernPlural != null) modernAffix = incomplete(modernPlural.substring(1));
		if(classicalPlural != null) classicalAffix = incomplete(classicalPlural.substring(1));

		CategoricalNounInflection inflection = new CategoricalNounInflection(singularAffix, modernAffix,
				classicalAffix, false);

		predefinedInflections.add(inflection);
	}

	private void handleCompletePlural(String singular, String modernPlural, String classicalPlural) {
		InflectionAffix singularAffix = complete(singular.substring(1));

		InflectionAffix modernAffix = null;
		InflectionAffix classicalAffix = null;

		if(modernPlural != null) modernAffix = complete(modernPlural.substring(1));
		if(classicalPlural != null) classicalAffix = complete(classicalPlural.substring(1));

		CategoricalNounInflection inflection = new CategoricalNounInflection(singularAffix, modernAffix,
				classicalAffix, false);

		predefinedInflections.add(inflection);
	}

	private void handleIrregularPlural(String singular, String modernPlural, String classicalPlural) {
		IrregularNounInflection inflection = new IrregularNounInflection(singular, modernPlural,
				classicalPlural, false);

		if(!predefinedIrregulars.containsKey(singular)) {
			predefinedIrregulars.put(singular, inflection);
		}

		if(modernPlural != null && !predefinedIrregulars.containsKey(modernPlural))
			predefinedIrregulars.put(modernPlural, inflection);
		if(classicalPlural != null && !predefinedIrregulars.containsKey(classicalPlural))
			predefinedIrregulars.put(classicalPlural, inflection);
	}
}