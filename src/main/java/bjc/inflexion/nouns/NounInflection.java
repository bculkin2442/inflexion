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
 * Interface for inflecting nouns.
 *
 * @author EVE
 */
public interface NounInflection {
	/**
	 * Check if a noun matches this inflection.
	 *
	 * @param noun
	 * 	The noun to check on this inflection.
	 *
	 * @return
	 * 	Whether or not the noun belongs to the inflection.
	 */
	public boolean matches(String noun);

	/**
	 * Check if a noun for this inflection is singular or not.
	 *
	 * @param noun
	 * 	The noun to check for singularity.
	 *
	 * @return
	 * 	Whether or not the noun is singular.
	 *
	 * @throws InflectionException
	 * 	If the noun isn't part of this inflection.
	 */
	public boolean isSingular(String noun);

	/**
	 * Check if a noun for this inflection is plural or not.
	 *
	 * @param noun
	 * 	The noun to check for plurality.
	 *
	 * @return
	 * 	Whether or not the noun is plural.
	 *
	 * @throws InflectionException
	 * 	If the noun isn't part of this inflection.
	 */
	public boolean isPlural(String noun);

	/**
	 * Convert a singular noun to a plural noun.
	 *
	 * @param plural
	 * 	The plural noun to inflect to a singular form.
	 *
	 * @return
	 * 	The singular form of the noun.
	 *
	 * @throws InflectionException
	 * 	If the noun isn't part of the inflection.
	 */
	public String singularize(String plural);

	/**
	 * Convert a singular noun to a plural noun.
	 *
	 * @param singular
	 * 	The singular noun to inflect to a plural form.
	 *
	 * @return
	 * 	The plural form of the noun.
	 *
	 * @throws InflectionException
	 * 	If the noun isn't part of the inflection.
	 */
	public String pluralize(String singular);

	/**
	 * Convert a singular noun to a modern plural noun.
	 *
	 * @param singular
	 * 	The singular noun to inflect to a modern plural form.
	 *
	 * @return
	 * 	The modern plural form of the noun.
	 *
	 * @throws InflectionException
	 * 	If the noun isn't part of the inflection.
	 */
	public String pluralizeModern(String singular);

	/**
	 * Convert a singular noun to a classical plural noun.
	 *
	 * @param singular
	 * 	The singular noun to inflect to a classical plural form.
	 *
	 * @return
	 * 	The classical plural form of the noun.
	 *
	 * @throws InflectionException
	 * 	If the noun isn't part of the inflection.
	 */
	public String pluralizeClassical(String singular);
}
