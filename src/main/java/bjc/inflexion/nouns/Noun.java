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
 * A noun attached to an inflection.
 *
 * @author EVE
 */
public class Noun {
	/* The word itself. */
	private final String word;
	/* Its inflection. */
	private final NounInflection inflection;

	/**
	 * Create a new noun from a word and inflection.
	 *
	 * @param wrd
	 *                  The word for the noun.
	 *
	 * @param inflction
	 *                  The inflection for the word.
	 */
	public Noun(final String wrd, final NounInflection inflction) {
		word = wrd;
		inflection = inflction;
	}

	/**
	 * Get the input noun.
	 *
	 * @return The noun, as input.
	 */
	public String getWord() {
		return word;
	}

	/**
	 * Get the inflection for this noun.
	 *
	 * @return The inflection for this noun.
	 */
	public NounInflection getInflection() {
		return inflection;
	}

	/**
	 * Check if this noun is singular.
	 *
	 * @return Whether or not the noun is singular.
	 */
	public boolean isSingular() {
		return inflection.isSingular(word);
	}

	/**
	 * Check if this noun is plural.
	 *
	 * @return Whether or not this noun is plural.
	 */
	public boolean isPlural() {
		return inflection.isPlural(word);
	}

	/**
	 * Check whether or not this noun is uninflected (does not change in
	 * singular/plural).
	 * 
	 * @return Whether or not the noun is uninflected.
	 */
	public boolean isUninflected() {
		String singlar = singular();

		if (singlar.equals(modernPlural()) || singlar.equals(classicalPlural()))
			return true;

		return false;
	}

	/**
	 * Check if this noun has differing modern/classical plural forms.
	 * 
	 * @return Whether this noun has differing plural forms.
	 */
	public boolean isDifferingPlural() {
		return modernPlural().equals(classicalPlural());
	}

	/**
	 * Get the singular form of this noun.
	 *
	 * @return The singular form of this noun.
	 */
	public String singular() {
		if (isSingular())
			return word;

		return inflection.singularize(word);
	}

	/**
	 * Get the plural form of this noun.
	 *
	 * @return The plural form of this noun.
	 */
	public String plural() {
		if (isPlural())
			return word;

		return inflection.pluralize(word);
	}

	@Override
	public String toString() {
		return String.format("Noun [word=%s, inflection=%s]", word, inflection);
	}

	/**
	 * Get the modern plural form of this noun.
	 *
	 * @return The modern plural form of this noun.
	 */
	public String modernPlural() {
		if (isPlural()) {
			// @NOTE 9/16/18
			//
			// Not sure if we're in modern/classical plural. Think
			// if there's a better way to do this
			return inflection.pluralizeModern(inflection.singularize(word));
		}

		return inflection.pluralizeModern(word);
	}

	/**
	 * Get the classical plural form of this noun.
	 *
	 * @return The classical plural form of this noun.
	 */
	public String classicalPlural() {
		if (isPlural()) {
			// @NOTE 9/16/18
			//
			// Not sure if we're in modern/classical plural. Think
			// if there's a better way to do this
			return inflection.pluralizeModern(inflection.singularize(word));
		}

		return inflection.pluralizeClassical(word);
	}
}
