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
	/* Format string for toString. */
	private static final String TOSTRING_FMT = "Noun [word=%s, inflection=%s]";
	/* The word itself. */
	private final String word;
	/* Its inflection. */
	private final NounInflection inflection;

	/**
	 * Create a new noun from a word and inflection.
	 *
	 * @param wrd
	 * 	The word for the noun.
	 *
	 * @param inflction
	 * 	The inflection for the word.
	 */
	public Noun(final String wrd, final NounInflection inflction) {
		word = wrd;
		inflection = inflction;
	}

	/**
	 * Get the input noun.
	 *
	 * @return
	 * 	The noun, as input.
	 */
	public String getWord() {
		return word;
	}

	/**
	 * Get the inflection for this noun.
	 *
	 * @return
	 * 	The inflection for this noun.
	 */
	public NounInflection getInflection() {
		return inflection;
	}

	/**
	 * Check if this noun is singular.
	 *
	 * @return
	 * 	Whether or not the noun is singular.
	 */
	public boolean isSingular() {
		return inflection.isSingular(word);
	}

	/**
	 * Check if this noun is plural.
	 *
	 * @return
	 * 	Whether or not this noun is plural.
	 */
	public boolean isPlural() {
		return inflection.isPlural(word);
	}

	/**
	 * Get the singular form of this noun.
	 *
	 * @return
	 * 	The singular form of this noun.
	 */
	public String singular() {
		return inflection.singularize(word);
	}

	/**
	 * Get the plural form of this noun.
	 *
	 * @return
	 * 	The plural form of this noun.
	 */
	public String plural() {
		return inflection.pluralize(word);
	}

	@Override
	public String toString() {
		return String.format(TOSTRING_FMT, word, inflection);
	}

	/**
	 * Get the modern plural form of this noun.
	 *
	 * @return
	 * 	The modern plural form of this noun.
	 */
	public String modernPlural() {
		return inflection.pluralizeModern(word);
	}

	/**
	 * Get the classical plural form of this noun.
	 *
	 * @return
	 * 	The classical plural form of this noun.
	 */
	public String classicalPlural() {
		return inflection.pluralizeClassical(word);
	}
}
