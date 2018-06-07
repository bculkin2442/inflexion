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
 * An affix attached to a word and used for inflection.
 *
 * @author EVE
 */
public interface InflectionAffix {
	/**
	 * Check if a word has this affix.
	 *
	 * @param word
	 * 	The word to check.
	 *
	 * @return
	 * 	Whether or not the word has the affix.
	 */
	boolean hasAffix(String word);

	/**
	 * Remove the affix from a word.
	 *
	 * @param word
	 * 	The word to remove the affix from.
	 *
	 * @return
	 * 	The word with the affix removed.
	 */
	String deaffix(String word);

	/**
	 * Apply this affix to a word.
	 *
	 * @param word
	 * 	The word to apply the affix to.
	 *
	 * @return
	 * 	The word with the affix applied.
	 */
	String affix(String word);
}
