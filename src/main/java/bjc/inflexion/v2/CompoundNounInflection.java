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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Implementation of {@link NounInflection} for words that don't inflect at the
 * end.
 * 
 * @author EVE
 *
 */
public class CompoundNounInflection implements NounInflection {
	/*
	 * Data stores for use.
	 */
	private Nouns		nounDB;
	private Prepositions	prepositionDB;

	private Pattern compoundMatcher;

	private String singularPattern;

	private String	modernPluralPattern;
	private String	classicalPluralPattern;

	private boolean preferClassical;

	/*
	 * Whether or not this inflection takes a preposition.
	 */
	private boolean hasPreposition;

	/**
	 * TODO fill in documentation.
	 * 
	 * @param nounDB
	 * @param prepositionDB
	 * @param compoundMatcher
	 * @param singularPattern
	 * @param modernPluralPattern
	 * @param classicalPluralPattern
	 * @param preferClassical
	 * @param hasPreposition
	 */
	public CompoundNounInflection(Nouns nounDB, Prepositions prepositionDB, Pattern compoundMatcher,
			String singularPattern, String modernPluralPattern, String classicalPluralPattern,
			boolean preferClassical, boolean hasPreposition) {
		this.nounDB = nounDB;
		this.prepositionDB = prepositionDB;
		this.compoundMatcher = compoundMatcher;
		this.singularPattern = singularPattern;
		this.modernPluralPattern = modernPluralPattern;
		this.classicalPluralPattern = classicalPluralPattern;
		this.preferClassical = preferClassical;
		this.hasPreposition = hasPreposition;
	}

	@Override
	public boolean matches(String noun) {
		Matcher matcher = compoundMatcher.matcher(noun);

		if(matcher.matches()) {
			Noun actNoun = nounDB.getNoun(matcher.group("noun"));

			if(actNoun == null) return false;

			if(hasPreposition) {
				return prepositionDB.isPreposition(matcher.group("preposition"));
			} else
				return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean isSingular(String noun) {
		Matcher matcher = compoundMatcher.matcher(noun);
		Noun actNoun = nounDB.getNoun(matcher.group("noun"));

		return actNoun.isSingular();
	}

	@Override
	public boolean isPlural(String noun) {
		Matcher matcher = compoundMatcher.matcher(noun);
		Noun actNoun = nounDB.getNoun(matcher.group("noun"));

		return actNoun.isPlural();
	}

	@Override
	public String singularize(String plural) {
		Matcher matcher = compoundMatcher.matcher(plural);
		Noun actNoun = nounDB.getNoun(matcher.group("noun"));

		if(hasPreposition) {
			return String.format(singularPattern, actNoun.singular(), matcher.group("preposition"));
		} else {
			return String.format(singularPattern, actNoun.singular());
		}
	}

	@Override
	public String pluralize(String singular) {
		Matcher matcher = compoundMatcher.matcher(singular);
		Noun actNoun = getNoun(matcher);

		/*
		 * TODO adapt this to take preferClassical into account.
		 */
		if(hasPreposition) {
			return String.format(modernPluralPattern, actNoun.plural(), matcher.group("preposition"));
		} else {
			return String.format(modernPluralPattern, actNoun.plural());
		}
	}

	@Override
	public String pluralizeModern(String singular) {
		if(modernPluralPattern == null) return pluralizeClassical(singular);

		Matcher matcher = compoundMatcher.matcher(singular);
		Noun actNoun = getNoun(matcher);

		if(hasPreposition) {
			return String.format(modernPluralPattern, actNoun.modernPlural(), matcher.group("preposition"));
		} else {
			return String.format(modernPluralPattern, actNoun.modernPlural());
		}
	}

	@Override
	public String pluralizeClassical(String singular) {
		if(modernPluralPattern == null) return pluralizeModern(singular);

		Matcher matcher = compoundMatcher.matcher(singular);
		Noun actNoun = getNoun(matcher);

		if(hasPreposition) {
			return String.format(classicalPluralPattern, actNoun.classicalPlural(),
					matcher.group("preposition"));
		} else {
			return String.format(classicalPluralPattern, actNoun.classicalPlural());
		}
	}

	private Noun getNoun(Matcher matcher) {
		matcher.matches();

		Noun actNoun = nounDB.getNoun(matcher.group("noun"));
		return actNoun;
	}
}