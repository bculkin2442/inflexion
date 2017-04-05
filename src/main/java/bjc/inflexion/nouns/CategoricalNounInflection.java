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

/**
 * Implementation of {@link NounInflection} for nouns matched by a regular
 * expression.
 * 
 * @author EVE
 *
 */
public class CategoricalNounInflection implements NounInflection {
	private static final String TOSTRING_FMT = "CategoricalNounInflection [singular=%s, modernPlural=%s,"
			+ " classicalPlural=%s]";

	private InflectionAffix singular;

	private InflectionAffix	modernPlural;
	private InflectionAffix	classicalPlural;

	/**
	 * Create a new categorical inflection.
	 * 
	 * @param singlar
	 *                The affix for the singular form.
	 * 
	 * @param modrnPlural
	 *                The affix for the modern plural.
	 * 
	 * @param classiclPlural
	 *                The affix for the classical plural.
	 */
	public CategoricalNounInflection(InflectionAffix singlar, InflectionAffix modrnPlural,
			InflectionAffix classiclPlural) {
		if(singlar == null)
			throw new NullPointerException("Singular form must not be null");
		else if(modrnPlural == null && classiclPlural == null)
			throw new NullPointerException("One of modern/classical plural forms must not be null");

		singular = singlar;
		modernPlural = modrnPlural;
		classicalPlural = classiclPlural;
	}

	@Override
	public boolean matches(String noun) {
		if(singular.hasAffix(noun))
			return true;
		else if(modernPlural != null && modernPlural.hasAffix(noun))
			return true;
		else if(classicalPlural != null && classicalPlural.hasAffix(noun))
			return true;
		else
			return false;
	}

	@Override
	public boolean isSingular(String noun) {
		if(singular.hasAffix(noun))
			return true;
		else if(matchesPlural(noun))
			return false;
		else {
			String msg = String.format("Noun '%s' doesn't belong to this inflection", noun, this);

			throw new InflectionException(msg);
		}
	}

	@Override
	public boolean isPlural(String noun) {
		if(singular.hasAffix(noun))
			return false;
		else if(matchesPlural(noun))
			return true;
		else {
			String msg = String.format("Noun '%s' doesn't belong to this inflection", noun, this);

			throw new InflectionException(msg);
		}
	}

	@Override
	public String singularize(String plural) {
		if(singular.hasAffix(plural))
			return plural;
		else if(modernPlural != null && modernPlural.hasAffix(plural))
			return singular.affix(modernPlural.deaffix(plural));
		else if(classicalPlural != null && classicalPlural.hasAffix(plural))
			return singular.affix(classicalPlural.deaffix(plural));
		else {
			String msg = String.format("Noun '%s' doesn't belong to this inflection", plural, this);

			throw new InflectionException(msg);
		}
	}

	@Override
	public String pluralize(String singlar) {
		if(singular.hasAffix(singlar)) {
			if(modernPlural == null) {
				return classicalPlural.affix(singular.deaffix(singlar));
			} else {
				return modernPlural.affix(singular.deaffix(singlar));
			}
		} else if(matchesPlural(singlar)) {
			return singlar;
		} else {
			String msg = String.format("Noun '%s' doesn't belong to this inflection", singlar, this);

			throw new InflectionException(msg);
		}
	}

	private boolean matchesPlural(String noun) {
		boolean hasModernPlural = modernPlural != null && modernPlural.hasAffix(noun);

		return hasModernPlural || (classicalPlural != null && classicalPlural.hasAffix(noun));
	}

	@Override
	public String toString() {
		return String.format(TOSTRING_FMT, singular, modernPlural, classicalPlural);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;

		result = prime * result + ((classicalPlural == null) ? 0 : classicalPlural.hashCode());
		result = prime * result + ((modernPlural == null) ? 0 : modernPlural.hashCode());

		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if(this == obj) return true;
		if(obj == null) return false;
		if(!(obj instanceof CategoricalNounInflection)) return false;

		CategoricalNounInflection other = (CategoricalNounInflection) obj;

		if(classicalPlural == null) {
			if(other.classicalPlural != null) return false;
		} else if(!classicalPlural.equals(other.classicalPlural)) return false;

		if(modernPlural == null) {
			if(other.modernPlural != null) return false;
		} else if(!modernPlural.equals(other.modernPlural)) return false;

		return true;
	}

	@Override
	public String pluralizeModern(String singlar) {
		if(modernPlural == null) return pluralizeClassical(singlar);

		String actSinglar = singlar;
		if(isPlural(singlar)) {
			actSinglar = singularize(singlar);
		}

		return modernPlural.affix(singular.deaffix(actSinglar));
	}

	@Override
	public String pluralizeClassical(String singlar) {
		if(classicalPlural == null) return pluralizeModern(singlar);

		String actSinglar = singlar;
		if(isPlural(singlar)) {
			actSinglar = singularize(singlar);
		}

		return classicalPlural.affix(singular.deaffix(actSinglar));
	}
}