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

import static bjc.inflexion.InflectionAffixes.*;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * @author EVE
 *
 */
public class Nouns {
	private static final DefaultNounInflection DEFAULT_INFLECTION = new DefaultNounInflection();

	private Prepositions prepositionDB;

	private Map<String, NounInflection>	userIrregulars;
	private List<NounInflection>		userInflections;

	private Map<String, NounInflection>	predefinedIrregulars;
	private List<NounInflection>		predefinedInflections;

	/**
	 * Create a new empty noun DB.
	 * 
	 * @param prepDB
	 *                The source for prepositions.
	 */
	public Nouns(Prepositions prepDB) {
		prepositionDB = prepDB;

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

				if(ln.contains("-")) {
					handleLine(ln);
					handleLine(ln.replace('-', ' '));
				} else {
					handleLine(ln);
				}
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

		if(singular.contains("(SING)")) {
			handleCompoundPlural(singular, modernPlural, classicalPlural);
		} else if(singular.startsWith("*")) {
			handleCompletePlural(singular, modernPlural, classicalPlural);
		} else if(singular.startsWith("-")) {
			handleIncompletePlural(singular, modernPlural, classicalPlural);
		} else {
			handleIrregularPlural(singular, modernPlural, classicalPlural);
		}
	}

	private void handleCompoundPlural(String singular, String modernPlural, String classicalPlural) {
		String actSingular = singular;
		String actModern = modernPlural == null ? "" : modernPlural;
		String actClassical = classicalPlural == null ? "" : classicalPlural;

		String singularPatt = actSingular.replaceAll(Pattern.quote("(SING)"), "(?<noun>\\\\w+)");
		String modernPatt = actModern.replaceAll(Pattern.quote("(PL)"), "(?<noun>\\\\w+)");
		String classicalPatt = actClassical.replaceAll(Pattern.quote("(PL)"), "(?<noun>\\\\w+)");

		actSingular = actSingular.replaceAll(Pattern.quote("(SING)"), "%1\\$s");
		actModern = actModern.replaceAll(Pattern.quote("(PL)"), "%1\\$s");
		actClassical = actClassical.replaceAll(Pattern.quote("(PL)"), "%1\\$s");

		List<CompoundNounInflection> inflections = new ArrayList<>(3);

		if(singular.contains("(PREP)")) {
			handleCompoundPreposition(actSingular, actModern, actClassical, singularPatt, modernPatt,
					classicalPatt, inflections);
		} else {
			handleCompound(actSingular, actModern, actClassical, singularPatt, modernPatt, classicalPatt,
					inflections);
		}

		for(NounInflection inf : inflections) {
			predefinedInflections.add(inf);
		}
	}

	private void handleCompound(String actSingular, String actModern, String actClassical, String singularPatt,
			String modernPatt, String classicalPatt, List<CompoundNounInflection> inflections) {
		if(singularPatt.contains("*")) {
			singularPatt = singularPatt.replaceAll(Pattern.quote("*"), "(?<scratch>\\\\w+)");
			modernPatt = modernPatt.replaceAll(Pattern.quote("*"), "(?<scratch>\\\\w+)");
			classicalPatt = classicalPatt.replaceAll(Pattern.quote("*"), "(?<scratch>\\\\w+)");

			actSingular = actSingular.replaceAll(Pattern.quote("*"), "%2\\$s");
			actModern = actModern.replaceAll(Pattern.quote("*"), "%2\\$s");
			actClassical = actClassical.replaceAll(Pattern.quote("*"), "%2\\$s");

			handleNonpluralCompound(actSingular, actModern, actClassical, singularPatt, modernPatt,
					classicalPatt, inflections, true);
		} else {
			handleNonpluralCompound(actSingular, actModern, actClassical, singularPatt, modernPatt,
					classicalPatt, inflections, false);
		}
	}

	private void handleNonpluralCompound(String actSingular, String actModern, String actClassical,
			String singularPatt, String modernPatt, String classicalPatt,
			List<CompoundNounInflection> inflections, boolean hasScratch) {
		actModern = actModern.equals("") ? null : actModern;
		actClassical = actClassical.equals("") ? null : actClassical;

		CompoundNounInflection singularInflection = new CompoundNounInflection(this, prepositionDB,
				Pattern.compile(singularPatt), actSingular, actModern, actClassical, false, hasScratch);

		inflections.add(singularInflection);

		if(!modernPatt.equals("")) {
			CompoundNounInflection modernInflection = new CompoundNounInflection(this, prepositionDB,
					Pattern.compile(modernPatt), actSingular, actModern, actClassical, false,
					hasScratch);

			inflections.add(modernInflection);
		}

		if(!classicalPatt.equals("")) {
			CompoundNounInflection classicalInflection = new CompoundNounInflection(this, prepositionDB,
					Pattern.compile(classicalPatt), actSingular, actModern, actClassical, false,
					hasScratch);

			inflections.add(classicalInflection);
		}
	}

	private void handleCompoundPreposition(String actSingular, String actModern, String actClassical,
			String singularPatt, String modernPatt, String classicalPatt,
			List<CompoundNounInflection> inflections) {
		singularPatt = singularPatt.replaceAll(Pattern.quote("(PREP)"), "(?<preposition>\\\\w+)");
		modernPatt = modernPatt.replaceAll(Pattern.quote("(PREP)"), "(?<preposition>\\\\w+)");
		classicalPatt = classicalPatt.replaceAll(Pattern.quote("(PREP)"), "(?<preposition>\\\\w+)");

		actSingular = actSingular.replaceAll(Pattern.quote("(PREP)"), "%2\\$s");
		actModern = actModern.replaceAll(Pattern.quote("(PREP)"), "%2\\$s");
		actClassical = actClassical.replaceAll(Pattern.quote("(PREP)"), "%2\\$s");

		if(singularPatt.contains("*")) {
			singularPatt = singularPatt.replaceAll(Pattern.quote("*"), "(?<scratch>\\\\w+)");
			modernPatt = modernPatt.replaceAll(Pattern.quote("*"), "(?<scratch>\\\\w+)");
			classicalPatt = classicalPatt.replaceAll(Pattern.quote("*"), "(?<scratch>\\\\w+)");

			actSingular = actSingular.replaceAll(Pattern.quote("*"), "%3\\$s");
			actModern = actModern.replaceAll(Pattern.quote("*"), "%3\\$s");
			actClassical = actClassical.replaceAll(Pattern.quote("*"), "%3\\$s");

			actModern = actModern.equals("") ? null : actModern;
			actClassical = actClassical.equals("") ? null : actClassical;

			addCompoundInflections(actSingular, actModern, actClassical, singularPatt, modernPatt,
					classicalPatt, inflections, true);
		} else {
			addCompoundInflections(actSingular, actModern, actClassical, singularPatt, modernPatt,
					classicalPatt, inflections, false);
		}
	}

	private void addCompoundInflections(String actSingular, String actModern, String actClassical,
			String singularPatt, String modernPatt, String classicalPatt,
			List<CompoundNounInflection> inflections, boolean hasScratch) {
		CompoundNounInflection singularInflection = new CompoundNounInflection(this, prepositionDB,
				Pattern.compile(singularPatt), actSingular, actModern, actClassical, true, hasScratch);

		inflections.add(singularInflection);

		if(!modernPatt.equals("")) {
			CompoundNounInflection modernInflection = new CompoundNounInflection(this, prepositionDB,
					Pattern.compile(modernPatt), actSingular, actModern, actClassical, true,
					hasScratch);

			inflections.add(modernInflection);
		}

		if(!classicalPatt.equals("")) {
			CompoundNounInflection classicalInflection = new CompoundNounInflection(this, prepositionDB,
					Pattern.compile(classicalPatt), actSingular, actModern, actClassical, true,
					hasScratch);

			inflections.add(classicalInflection);
		}
	}

	private void handleIncompletePlural(String singular, String modernPlural, String classicalPlural) {
		InflectionAffix singularAffix = incomplete(singular.substring(1));

		InflectionAffix modernAffix = null;
		InflectionAffix classicalAffix = null;

		if(modernPlural != null) modernAffix = incomplete(modernPlural.substring(1));
		if(classicalPlural != null) classicalAffix = incomplete(classicalPlural.substring(1));

		CategoricalNounInflection inflection = new CategoricalNounInflection(singularAffix, modernAffix,
				classicalAffix);

		predefinedInflections.add(inflection);
	}

	private void handleCompletePlural(String singular, String modernPlural, String classicalPlural) {
		InflectionAffix singularAffix = complete(singular.substring(1));

		InflectionAffix modernAffix = null;
		InflectionAffix classicalAffix = null;

		if(modernPlural != null) modernAffix = complete(modernPlural.substring(1));
		if(classicalPlural != null) classicalAffix = complete(classicalPlural.substring(1));

		CategoricalNounInflection inflection = new CategoricalNounInflection(singularAffix, modernAffix,
				classicalAffix);

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