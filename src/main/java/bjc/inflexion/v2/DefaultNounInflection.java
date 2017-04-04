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

/**
 * Default noun inflection for english nouns.
 * 
 * @author EVE
 *
 */
public class DefaultNounInflection implements NounInflection {
	@Override
	public boolean matches(String noun) {
		return true;
	}

	@Override
	public boolean isSingular(String noun) {
		return !noun.endsWith("s");
	}

	@Override
	public boolean isPlural(String noun) {
		return noun.endsWith("s");
	}

	@Override
	public String singularize(String plural) {
		if(plural.endsWith("ses")) {
			return plural.substring(0, plural.length() - 3);
		} else if(plural.endsWith("s")) {
			return plural.substring(0, plural.length() - 1);
		} else
			return plural;
	}

	@Override
	public String pluralize(String singular) {
		if(singular.endsWith("s")) {
			return singular + "es";
		}

		return singular + "s";
	}

	@Override
	public String pluralizeModern(String singular) {
		return pluralize(singular);
	}

	@Override
	public String pluralizeClassical(String singular) {
		return pluralize(singular);
	}
}