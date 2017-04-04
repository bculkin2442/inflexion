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
	private static final String TOSTRING_FMT = "CompoundNounInflection [compoundMatcher=%s, singularPattern=%s,"
			+ " modernPluralPattern=%s, classicalPluralPattern=%s, hasPreposition=%s]";
	/*
	 * Data stores for use.
	 */
	private Nouns		nounDB;
	private Prepositions	prepositionDB;

	private Pattern compoundMatcher;

	private String singularPattern;

	private String	modernPluralPattern;
	private String	classicalPluralPattern;

	/*
	 * Whether or not this inflection takes a preposition.
	 */
	private boolean hasPreposition;

	/*
	 * Whether or not there is a scratch word in place.
	 */
	private boolean hasScratch;

	/**
	 * TODO fill in documentation.
	 * 
	 * @param nounDB
	 * @param prepositionDB
	 * @param compoundMatcher
	 * @param singularPattern
	 * @param modernPluralPattern
	 * @param classicalPluralPattern
	 * @param hasPreposition
	 * @param hasScrtch
	 */
	public CompoundNounInflection(Nouns nounDB, Prepositions prepositionDB, Pattern compoundMatcher,
			String singularPattern, String modernPluralPattern, String classicalPluralPattern,
			boolean hasPreposition, boolean hasScrtch) {
		this.nounDB = nounDB;
		this.prepositionDB = prepositionDB;
		this.compoundMatcher = compoundMatcher;
		this.singularPattern = singularPattern;
		this.modernPluralPattern = modernPluralPattern;
		this.classicalPluralPattern = classicalPluralPattern;
		this.hasPreposition = hasPreposition;
		hasScratch = hasScrtch;
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
		Noun actNoun = getNoun(matcher);

		if(hasPreposition && hasScratch) {
			return String.format(singularPattern, actNoun.singular(), matcher.group("preposition"),
					matcher.group("scratch"));
		} else if(hasScratch) {
			return String.format(singularPattern, actNoun.singular(), matcher.group("scratch"));
		} else if(hasPreposition) {
			return String.format(singularPattern, actNoun.singular(), matcher.group("preposition"));
		} else {
			return String.format(singularPattern, actNoun.singular());
		}
	}

	@Override
	public String pluralize(String singular) {
		Matcher matcher = compoundMatcher.matcher(singular);
		Noun actNoun = getNoun(matcher);

		String patt = modernPluralPattern == null ? classicalPluralPattern : modernPluralPattern;

		if(hasPreposition && hasScratch) {
			return String.format(patt, actNoun.plural(), matcher.group("preposition"),
					matcher.group("scratch"));
		} else if(hasScratch) {
			return String.format(patt, actNoun.plural(), matcher.group("scratch"));
		} else if(hasPreposition) {
			return String.format(patt, actNoun.plural(), matcher.group("preposition"));
		} else {
			return String.format(patt, actNoun.plural());
		}
	}

	@Override
	public String pluralizeModern(String singular) {
		if(modernPluralPattern == null) return pluralizeClassical(singular);

		Matcher matcher = compoundMatcher.matcher(singular);
		Noun actNoun = getNoun(matcher);

		if(hasPreposition && hasScratch) {
			return String.format(modernPluralPattern, actNoun.modernPlural(), matcher.group("preposition"),
					matcher.group("scratch"));
		} else if(hasScratch) {
			return String.format(modernPluralPattern, actNoun.modernPlural(), matcher.group("scratch"));
		} else if(hasPreposition) {
			return String.format(modernPluralPattern, actNoun.modernPlural(), matcher.group("preposition"));
		} else {
			return String.format(modernPluralPattern, actNoun.modernPlural());
		}
	}

	@Override
	public String pluralizeClassical(String singular) {
		if(classicalPluralPattern == null) return pluralizeModern(singular);

		Matcher matcher = compoundMatcher.matcher(singular);
		Noun actNoun = getNoun(matcher);

		if(hasPreposition && hasScratch) {
			return String.format(classicalPluralPattern, actNoun.classicalPlural(),
					matcher.group("preposition"), matcher.group("scratch"));
		} else if(hasScratch) {
			return String.format(classicalPluralPattern, actNoun.classicalPlural(),
					matcher.group("scratch"));
		} else if(hasPreposition) {
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;

		result = prime * result + ((classicalPluralPattern == null) ? 0 : classicalPluralPattern.hashCode());
		result = prime * result + ((compoundMatcher == null) ? 0 : compoundMatcher.hashCode());
		result = prime * result + (hasPreposition ? 1231 : 1237);
		result = prime * result + ((modernPluralPattern == null) ? 0 : modernPluralPattern.hashCode());
		result = prime * result + ((singularPattern == null) ? 0 : singularPattern.hashCode());

		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if(this == obj) return true;
		if(obj == null) return false;
		if(!(obj instanceof CompoundNounInflection)) return false;

		CompoundNounInflection other = (CompoundNounInflection) obj;

		if(singularPattern == null) {
			if(other.singularPattern != null) return false;
		} else if(!singularPattern.equals(other.singularPattern)) return false;

		if(classicalPluralPattern == null) {
			if(other.classicalPluralPattern != null) return false;
		} else if(!classicalPluralPattern.equals(other.classicalPluralPattern)) return false;

		if(hasPreposition != other.hasPreposition) return false;

		if(modernPluralPattern == null) {
			if(other.modernPluralPattern != null) return false;
		} else if(!modernPluralPattern.equals(other.modernPluralPattern)) return false;

		if(compoundMatcher == null) {
			if(other.compoundMatcher != null) return false;
		} else if(!compoundMatcher.equals(other.compoundMatcher)) return false;

		return true;
	}

	@Override
	public String toString() {
		return String.format(TOSTRING_FMT, compoundMatcher, singularPattern, modernPluralPattern,
				classicalPluralPattern, hasPreposition);
	}
}