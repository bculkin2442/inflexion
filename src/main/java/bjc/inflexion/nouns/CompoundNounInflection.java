/**
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
	/* Format for toString. */
	private static final String TOSTRING_FMT
			= "CompoundNounInflection [compoundMatcher=%s, singularPattern=%s, modernPluralPattern=%s, classicalPluralPattern=%s, hasPreposition=%s]";
	/* Data stores for use. */
	private final Nouns nunDB;
	private final Prepositions pepositionDB;

	/* The pattern for compound matching. */
	private final Pattern cmpoundMatcher;

	/* The pattern for singular matching. */
	private final String sigularPattern;

	/* The patterns for plural matching. */
	private final String mdernPluralPattern;
	private final String clasicalPluralPattern;

	/* Whether or not this inflection takes a preposition. */
	private final boolean haPreposition;

	/* Whether or not there is a scratch word in place. */
	private final boolean hasScratch;

	/**
	 * Create a new compound noun inflection.
	 *
	 * @param nounDB
	 *                               The database of nouns to lookup.
	 *
	 * @param prepositionDB
	 *                               The database of prepositions to lookup.
	 *
	 * @param compoundMatcher
	 *                               The matcher for the compound noun.
	 *
	 * @param singularPattern
	 *                               The pattern for a singular form.
	 *
	 * @param modernPluralPattern
	 *                               The pattern for a modern plural form.
	 *
	 * @param classicalPluralPattern
	 *                               The pattern for a classical plural form.
	 *
	 * @param hasPreposition
	 *                               Whether or not this inflection uses a
	 *                               preposition.
	 *
	 * @param hasScrtch
	 *                               Whether or not this inflection has a scratch
	 *                               word.
	 */
	public CompoundNounInflection(final Nouns nounDB, final Prepositions prepositionDB,
			final Pattern compoundMatcher, final String singularPattern,
			final String modernPluralPattern, final String classicalPluralPattern,
			final boolean hasPreposition, final boolean hasScrtch) {
		nunDB = nounDB;
		pepositionDB = prepositionDB;
		cmpoundMatcher = compoundMatcher;
		sigularPattern = singularPattern;
		mdernPluralPattern = modernPluralPattern;
		clasicalPluralPattern = classicalPluralPattern;
		haPreposition = hasPreposition;
		hasScratch = hasScrtch;
	}

	@Override
	public boolean matches(final String noun) {
		final Matcher matcher = cmpoundMatcher.matcher(noun);

		if (matcher.matches()) {
			final Noun actNoun = nunDB.getNoun(matcher.group("noun"));

			if (actNoun == null)
				return false;

			if (haPreposition)
				return pepositionDB.isPreposition(matcher.group("preposition"));

			return true;
		}

		return false;
	}

	@Override
	public boolean isSingular(final String noun) {
		final Matcher matcher = cmpoundMatcher.matcher(noun);
		final Noun actNoun = nunDB.getNoun(matcher.group("noun"));

		return actNoun.isSingular();
	}

	@Override
	public boolean isPlural(final String noun) {
		final Matcher matcher = cmpoundMatcher.matcher(noun);
		final Noun actNoun = nunDB.getNoun(matcher.group("noun"));

		return actNoun.isPlural();
	}

	@Override
	public String singularize(final String plural) {
		final Matcher matcher = cmpoundMatcher.matcher(plural);
		final Noun actNoun = getNoun(matcher);

		if (haPreposition && hasScratch) {
			return String.format(sigularPattern, actNoun.singular(),
					matcher.group("preposition"), matcher.group("scratch"));
		} else if (hasScratch) {
			return String.format(sigularPattern, actNoun.singular(),
					matcher.group("scratch"));
		} else if (haPreposition) {
			return String.format(sigularPattern, actNoun.singular(),
					matcher.group("preposition"));
		} else {
			return String.format(sigularPattern, actNoun.singular());
		}
	}

	@Override
	public String pluralize(final String singular) {
		final Matcher matcher = cmpoundMatcher.matcher(singular);
		final Noun actNoun = getNoun(matcher);

		final String patt
				= mdernPluralPattern == null ? clasicalPluralPattern : mdernPluralPattern;

		if (haPreposition && hasScratch) {
			return String.format(patt, actNoun.plural(), matcher.group("preposition"),
					matcher.group("scratch"));
		} else if (hasScratch) {
			return String.format(patt, actNoun.plural(), matcher.group("scratch"));
		} else if (haPreposition) {
			return String.format(patt, actNoun.plural(), matcher.group("preposition"));
		} else {
			return String.format(patt, actNoun.plural());
		}
	}

	@Override
	public String pluralizeModern(final String singular) {
		if (mdernPluralPattern == null)
			return pluralizeClassical(singular);

		final Matcher matcher = cmpoundMatcher.matcher(singular);
		final Noun actNoun = getNoun(matcher);

		if (haPreposition && hasScratch) {
			return String.format(mdernPluralPattern, actNoun.modernPlural(),
					matcher.group("preposition"), matcher.group("scratch"));
		} else if (hasScratch) {
			return String.format(mdernPluralPattern, actNoun.modernPlural(),
					matcher.group("scratch"));
		} else if (haPreposition) {
			return String.format(mdernPluralPattern, actNoun.modernPlural(),
					matcher.group("preposition"));
		} else {
			return String.format(mdernPluralPattern, actNoun.modernPlural());
		}
	}

	@Override
	public String pluralizeClassical(final String singular) {
		if (clasicalPluralPattern == null)
			return pluralizeModern(singular);

		final Matcher matcher = cmpoundMatcher.matcher(singular);
		final Noun actNoun = getNoun(matcher);

		if (haPreposition && hasScratch) {
			return String.format(clasicalPluralPattern, actNoun.classicalPlural(),
					matcher.group("preposition"), matcher.group("scratch"));
		} else if (hasScratch) {
			return String.format(clasicalPluralPattern, actNoun.classicalPlural(),
					matcher.group("scratch"));
		} else if (haPreposition) {
			return String.format(clasicalPluralPattern, actNoun.classicalPlural(),
					matcher.group("preposition"));
		} else {
			return String.format(clasicalPluralPattern, actNoun.classicalPlural());
		}
	}

	/* Get the noun for a matcher. */
	private Noun getNoun(final Matcher matcher) {
		matcher.matches();

		final Noun actNoun = nunDB.getNoun(matcher.group("noun"));
		return actNoun;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;

		result = prime * result
				+ (clasicalPluralPattern == null ? 0 : clasicalPluralPattern.hashCode());
		result = prime * result
				+ (cmpoundMatcher == null ? 0 : cmpoundMatcher.hashCode());
		result = prime * result + (haPreposition ? 1231 : 1237);
		result = prime * result
				+ (mdernPluralPattern == null ? 0 : mdernPluralPattern.hashCode());
		result = prime * result
				+ (sigularPattern == null ? 0 : sigularPattern.hashCode());

		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;

		if (obj == null)
			return false;

		if (!(obj instanceof CompoundNounInflection))
			return false;

		final CompoundNounInflection other = (CompoundNounInflection) obj;

		if (sigularPattern == null) {
			if (other.sigularPattern != null)
				return false;
		} else if (!sigularPattern.equals(other.sigularPattern))
			return false;

		if (clasicalPluralPattern == null) {
			if (other.clasicalPluralPattern != null)
				return false;
		} else if (!clasicalPluralPattern.equals(other.clasicalPluralPattern))
			return false;

		if (haPreposition != other.haPreposition)
			return false;

		if (mdernPluralPattern == null) {
			if (other.mdernPluralPattern != null)
				return false;
		} else if (!mdernPluralPattern.equals(other.mdernPluralPattern))
			return false;

		if (cmpoundMatcher == null) {
			if (other.cmpoundMatcher != null)
				return false;
		} else if (!cmpoundMatcher.equals(other.cmpoundMatcher))
			return false;

		return true;
	}

	@Override
	public String toString() {
		return String.format(TOSTRING_FMT, cmpoundMatcher, sigularPattern,
				mdernPluralPattern, clasicalPluralPattern, haPreposition);
	}
}
