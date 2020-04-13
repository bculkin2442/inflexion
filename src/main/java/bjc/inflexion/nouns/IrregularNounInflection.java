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

/**
 * Implementation of {@link NounInflection} for irregular nouns.
 *
 * @author EVE
 */
public class IrregularNounInflection implements NounInflection {
	/* Format string for toString. */
	private static final String TOSTRING_FMT
			= "IrregularNounInflection [singular=%s, modernPlural=%s,"
					+ " classicalPlural=%s, preferClassical=%s]";

	/* The singular form. */
	private final String singular;

	/* The modern plural form. */
	private final String modernPlural;
	/* The classical plural form. */
	private final String classicalPlural;

	/* Whether to prefer the classical form or not. */
	private final boolean preferClassical;

	/**
	 * Create a new irregular noun inflection.
	 *
	 * @param singlar
	 *                       The singular form of the noun.
	 *
	 * @param modrnPlural
	 *                       The modern plural of the noun.
	 *
	 * @param classiclPlural
	 *                       The classical plural of the noun.
	 *
	 * @param prefrClassical
	 *                       Whether the classical form should be preferred if it is
	 *                       available.
	 */
	public IrregularNounInflection(final String singlar, final String modrnPlural,
			final String classiclPlural, final boolean prefrClassical) {
		if (singlar == null)
			throw new NullPointerException("Singular form must not be null");

		if (modrnPlural == null && classiclPlural == null)
			throw new NullPointerException(
					"One of modern/classical plural forms must not be null");

		singular = singlar;
		modernPlural = modrnPlural;
		classicalPlural = classiclPlural;
		preferClassical = prefrClassical;
	}

	@Override
	public boolean matches(final String noun) {
		if (noun.equalsIgnoreCase(singular)) {
			return true;
		} else if (noun.equalsIgnoreCase(modernPlural)) {
			return true;
		} else if (noun.equalsIgnoreCase(classicalPlural)) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean isSingular(final String noun) {
		if (noun.equalsIgnoreCase(singular)) {
			return true;
		} else if (matchesPlural(noun)) {
			return false;
		} else {
			final String msg = String.format(
					"Noun '%s' doesn't belong to this inflection '%s'", noun, this);

			throw new InflectionException(msg);
		}
	}

	@Override
	public boolean isPlural(final String noun) {
		if (noun.equalsIgnoreCase(singular)) {
			return false;
		} else if (matchesPlural(noun)) {
			return true;
		} else {
			final String msg = String.format(
					"Noun '%s' doesn't belong to this inflection '%s'", noun, this);

			throw new InflectionException(msg);
		}
	}

	@Override
	public String singularize(final String plural) {
		if (plural.equalsIgnoreCase(singular)) {
			return singular;
		} else if (matchesPlural(plural)) {
			return singular;
		} else {
			final String msg = String.format(
					"Noun '%s' doesn't belong to this inflection '%s'", plural, this);

			throw new InflectionException(msg);
		}
	}

	@Override
	public String pluralize(final String singlar) {
		if (singlar.equalsIgnoreCase(singlar)) {
			return getPlural();
		} else if (matchesPlural(singlar)) {
			return getPlural();
		} else {
			final String msg = String.format(
					"Noun '%s' doesn't belong to this inflection '%s'", singlar, this);

			throw new InflectionException(msg);
		}
	}

	/* Get the plural form. */
	private String getPlural() {
		if (preferClassical) {
			if (classicalPlural == null)
				return modernPlural;

			return classicalPlural;
		} else if (modernPlural == null) {
			return classicalPlural;
		} else {
			return modernPlural;
		}
	}

	/* Check if something matches the plural forms. */
	private boolean matchesPlural(final String noun) {
		return noun.equalsIgnoreCase(modernPlural)
				|| noun.equalsIgnoreCase(classicalPlural);
	}

	@Override
	public String toString() {
		return String.format(TOSTRING_FMT, singular, modernPlural, classicalPlural,
				preferClassical);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;

		result = prime * result
				+ (classicalPlural == null ? 0 : classicalPlural.hashCode());
		result = prime * result + (modernPlural == null ? 0 : modernPlural.hashCode());
		result = prime * result + (singular == null ? 0 : singular.hashCode());

		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;

		if (obj == null)
			return false;

		if (!(obj instanceof IrregularNounInflection))
			return false;

		final IrregularNounInflection other = (IrregularNounInflection) obj;

		if (singular == null) {
			if (other.singular != null)
				return false;
		} else if (!singular.equals(other.singular))
			return false;

		if (classicalPlural == null) {
			if (other.classicalPlural != null)
				return false;
		} else if (!classicalPlural.equals(other.classicalPlural))
			return false;

		if (modernPlural == null) {
			if (other.modernPlural != null)
				return false;
		} else if (!modernPlural.equals(other.modernPlural))
			return false;

		return true;
	}

	@Override
	public String pluralizeModern(final String singlar) {
		if (modernPlural == null)
			return pluralizeClassical(singlar);

		return modernPlural;
	}

	@Override
	public String pluralizeClassical(final String singlar) {
		if (classicalPlural == null)
			return pluralizeModern(singlar);

		return classicalPlural;
	}
}
