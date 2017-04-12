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
package bjc.inflexion.nouns;

import static bjc.inflexion.nouns.InflectionAffixes.complete;
import static bjc.inflexion.nouns.InflectionAffixes.incomplete;

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

	private final Prepositions prepositionDB;

	private final Map<String, NounInflection>	userIrregulars;
	private final List<NounInflection>		userInflections;

	private final Map<String, NounInflection>	predefinedIrregulars;
	private final List<NounInflection>		predefinedInflections;

	/**
	 * Create a new empty noun DB.
	 *
	 * @param prepDB
	 *                The source for prepositions.
	 */
	public Nouns(final Prepositions prepDB) {
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
	public Noun getNoun(final String noun) {
		if (userIrregulars.containsKey(noun)) return new Noun(noun, userIrregulars.get(noun));
		for (final NounInflection inflect : userInflections) {
			if (inflect.matches(noun)) return new Noun(noun, inflect);
		}

		if (predefinedIrregulars.containsKey(noun)) return new Noun(noun, predefinedIrregulars.get(noun));
		for (final NounInflection inflect : predefinedInflections) {
			if (inflect.matches(noun)) return new Noun(noun, inflect);
		}

		return new Noun(noun, DEFAULT_INFLECTION);
	}

	/**
	 * Load the contents of the stream into this DB.
	 *
	 * @param stream
	 *                The stream to load from.
	 */
	public void loadFromStream(final InputStream stream) {
		try (Scanner scn = new Scanner(stream)) {
			while (scn.hasNextLine()) {
				final String ln = scn.nextLine().trim();

				/*
				 * Ignore comments and blank lines.
				 */
				if (ln.startsWith("#")) {
					continue;
				}
				if (ln.equals("")) {
					continue;
				}

				if (ln.contains("-")) {
					handleLine(ln);
					handleLine(ln.replace('-', ' '));
				} else {
					handleLine(ln);
				}
			}
		}
	}

	private void handleLine(final String ln) {
		final String[] parts = ln.split(Pattern.quote("=>"));

		if (parts.length != 2) {
			final String msg = String.format("Improperly formatted noun defn '%s'", ln);

			throw new InflectionException(msg);
		}

		final String singular = parts[0].trim();
		final String plural = parts[1].trim();

		String modernPlural = "";
		String classicalPlural = "";

		if (plural.contains("|")) {
			final String[] plurals = plural.split(Pattern.quote("|"));

			if (plurals.length == 1) {
				modernPlural = plurals[0].trim();
			} else {
				modernPlural = plurals[0].trim();
				classicalPlural = plurals[1].trim();
			}

			if (modernPlural.equals("")) {
				modernPlural = null;
			}
			if (classicalPlural.equals("")) {
				classicalPlural = null;
			}
		} else {
			modernPlural = plural;
			classicalPlural = null;
		}

		if (singular.contains("(SING)")) {
			handleCompoundPlural(singular, modernPlural, classicalPlural);
		} else if (singular.startsWith("*")) {
			handleCompletePlural(singular, modernPlural, classicalPlural);
		} else if (singular.startsWith("-")) {
			handleIncompletePlural(singular, modernPlural, classicalPlural);
		} else {
			handleIrregularPlural(singular, modernPlural, classicalPlural);
		}
	}

	private void handleCompoundPlural(final String singular, final String modernPlural,
			final String classicalPlural) {
		String actSingular = singular;
		String actModern = modernPlural == null ? "" : modernPlural;
		String actClassical = classicalPlural == null ? "" : classicalPlural;

		final String singularPatt = actSingular.replaceAll(Pattern.quote("(SING)"), "(?<noun>\\\\w+)");
		final String modernPatt = actModern.replaceAll(Pattern.quote("(PL)"), "(?<noun>\\\\w+)");
		final String classicalPatt = actClassical.replaceAll(Pattern.quote("(PL)"), "(?<noun>\\\\w+)");

		actSingular = actSingular.replaceAll(Pattern.quote("(SING)"), "%1\\$s");
		actModern = actModern.replaceAll(Pattern.quote("(PL)"), "%1\\$s");
		actClassical = actClassical.replaceAll(Pattern.quote("(PL)"), "%1\\$s");

		final List<CompoundNounInflection> inflections = new ArrayList<>(3);

		if (singular.contains("(PREP)")) {
			handleCompoundPreposition(actSingular, actModern, actClassical, singularPatt, modernPatt,
					classicalPatt, inflections);
		} else {
			handleCompound(actSingular, actModern, actClassical, singularPatt, modernPatt, classicalPatt,
					inflections);
		}

		for (final NounInflection inf : inflections) {
			predefinedInflections.add(inf);
		}
	}

	private void handleCompound(final String actSinglar, final String actModrn, final String actClasscal,
			final String singularPtt, final String modernPtt, final String classicalPtt,
			final List<CompoundNounInflection> inflections) {
		if (singularPtt.contains("*")) {
			final String singularPatt = singularPtt.replaceAll(Pattern.quote("*"), "(?<scratch>\\\\w+)");
			final String modernPatt = modernPtt.replaceAll(Pattern.quote("*"), "(?<scratch>\\\\w+)");
			final String classicalPatt = classicalPtt.replaceAll(Pattern.quote("*"), "(?<scratch>\\\\w+)");

			final String actSingular = actSinglar.replaceAll(Pattern.quote("*"), "%2\\$s");
			final String actModern = actModrn.replaceAll(Pattern.quote("*"), "%2\\$s");
			final String actClassical = actClasscal.replaceAll(Pattern.quote("*"), "%2\\$s");

			handleNonpluralCompound(actSingular, actModern, actClassical, singularPatt, modernPatt,
					classicalPatt, inflections, true);
		} else {
			handleNonpluralCompound(actSinglar, actModrn, actClasscal, singularPtt, modernPtt, classicalPtt,
					inflections, false);
		}
	}

	private void handleNonpluralCompound(final String actSinglar, final String actModrn, final String actClasscal,
			final String singularPatt, final String modernPatt, final String classicalPatt,
			final List<CompoundNounInflection> inflections, final boolean hasScratch) {
		final String actModern = actModrn.equals("") ? null : actModrn;
		final String actClassical = actClasscal.equals("") ? null : actClasscal;

		final CompoundNounInflection singularInflection = new CompoundNounInflection(this, prepositionDB,
				Pattern.compile(singularPatt), actSinglar, actModern, actClassical, false, hasScratch);

		inflections.add(singularInflection);

		if (!modernPatt.equals("")) {
			final CompoundNounInflection modernInflection = new CompoundNounInflection(this, prepositionDB,
					Pattern.compile(modernPatt), actSinglar, actModern, actClassical, false,
					hasScratch);

			inflections.add(modernInflection);
		}

		if (!classicalPatt.equals("")) {
			final CompoundNounInflection classicalInflection = new CompoundNounInflection(this,
					prepositionDB, Pattern.compile(classicalPatt), actSinglar, actModern,
					actClassical, false, hasScratch);

			inflections.add(classicalInflection);
		}
	}

	private void handleCompoundPreposition(final String actSinglar, final String actModrn, final String actClasscal,
			final String singularPtt, final String modernPtt, final String classicalPtt,
			final List<CompoundNounInflection> inflections) {
		String singularPatt = singularPtt.replaceAll(Pattern.quote("(PREP)"), "(?<preposition>\\\\w+)");
		String modernPatt = modernPtt.replaceAll(Pattern.quote("(PREP)"), "(?<preposition>\\\\w+)");
		String classicalPatt = classicalPtt.replaceAll(Pattern.quote("(PREP)"), "(?<preposition>\\\\w+)");

		String actSingular = actSinglar.replaceAll(Pattern.quote("(PREP)"), "%2\\$s");
		String actModern = actModrn.replaceAll(Pattern.quote("(PREP)"), "%2\\$s");
		String actClassical = actClasscal.replaceAll(Pattern.quote("(PREP)"), "%2\\$s");

		if (singularPatt.contains("*")) {
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

	private void addCompoundInflections(final String actSingular, final String actModern, final String actClassical,
			final String singularPatt, final String modernPatt, final String classicalPatt,
			final List<CompoundNounInflection> inflections, final boolean hasScratch) {
		final CompoundNounInflection singularInflection = new CompoundNounInflection(this, prepositionDB,
				Pattern.compile(singularPatt), actSingular, actModern, actClassical, true, hasScratch);

		inflections.add(singularInflection);

		if (!modernPatt.equals("")) {
			final CompoundNounInflection modernInflection = new CompoundNounInflection(this, prepositionDB,
					Pattern.compile(modernPatt), actSingular, actModern, actClassical, true,
					hasScratch);

			inflections.add(modernInflection);
		}

		if (!classicalPatt.equals("")) {
			final CompoundNounInflection classicalInflection = new CompoundNounInflection(this,
					prepositionDB, Pattern.compile(classicalPatt), actSingular, actModern,
					actClassical, true, hasScratch);

			inflections.add(classicalInflection);
		}
	}

	private void handleIncompletePlural(final String singular, final String modernPlural,
			final String classicalPlural) {
		final InflectionAffix singularAffix = incomplete(singular.substring(1));

		InflectionAffix modernAffix = null;
		InflectionAffix classicalAffix = null;

		if (modernPlural != null) {
			modernAffix = incomplete(modernPlural.substring(1));
		}
		if (classicalPlural != null) {
			classicalAffix = incomplete(classicalPlural.substring(1));
		}

		final CategoricalNounInflection inflection = new CategoricalNounInflection(singularAffix, modernAffix,
				classicalAffix);

		predefinedInflections.add(inflection);
	}

	private void handleCompletePlural(final String singular, final String modernPlural,
			final String classicalPlural) {
		final InflectionAffix singularAffix = complete(singular.substring(1));

		InflectionAffix modernAffix = null;
		InflectionAffix classicalAffix = null;

		if (modernPlural != null) {
			modernAffix = complete(modernPlural.substring(1));
		}
		if (classicalPlural != null) {
			classicalAffix = complete(classicalPlural.substring(1));
		}

		final CategoricalNounInflection inflection = new CategoricalNounInflection(singularAffix, modernAffix,
				classicalAffix);

		predefinedInflections.add(inflection);
	}

	private void handleIrregularPlural(final String singular, final String modernPlural,
			final String classicalPlural) {
		final IrregularNounInflection inflection = new IrregularNounInflection(singular, modernPlural,
				classicalPlural, false);

		if (!predefinedIrregulars.containsKey(singular)) {
			predefinedIrregulars.put(singular, inflection);
		}

		if (modernPlural != null && !predefinedIrregulars.containsKey(modernPlural)) {
			predefinedIrregulars.put(modernPlural, inflection);
		}
		if (classicalPlural != null && !predefinedIrregulars.containsKey(classicalPlural)) {
			predefinedIrregulars.put(classicalPlural, inflection);
		}
	}
}