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
 * Simple implementation of {@link InflectionAffix}
 * 
 * @author EVE
 *
 */
public class SimpleInflectionAffix implements InflectionAffix {
	private static final String TOSTRING_FMT = "SimpleInflectionAffix [affixTemplate=%s, affixMatcher=%s]";

	private String affixTemplate;

	private Pattern affixMatcher;

	/**
	 * Create a new inflection affix.
	 * 
	 * @param affixTemplate
	 *                The template for applying the affix, Should be a
	 *                printf-style format string with a single string blank.
	 * 
	 * @param affixMatcher
	 *                The regular expression that matches the affix on
	 *                strings. The 'stem' or word should be placed in a
	 *                named capturing group named 'stem'.
	 */
	public SimpleInflectionAffix(String affixTemplate, Pattern affixMatcher) {
		this.affixTemplate = affixTemplate;
		this.affixMatcher = affixMatcher;
	}

	@Override
	public boolean hasAffix(String word) {
		return affixMatcher.matcher(word).matches();
	}

	@Override
	public String deaffix(String word) {
		Matcher matcher = affixMatcher.matcher(word);
		matcher.matches();
		
		return matcher.group("stem");
	}

	@Override
	public String affix(String word) {
		return String.format(affixTemplate, word);
	}

	@Override
	public String toString() {
		return String.format(TOSTRING_FMT, affixTemplate, affixMatcher);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;

		result = prime * result + ((affixMatcher == null) ? 0 : affixMatcher.hashCode());
		result = prime * result + ((affixTemplate == null) ? 0 : affixTemplate.hashCode());

		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if(this == obj) return true;
		if(obj == null) return false;
		if(!(obj instanceof SimpleInflectionAffix)) return false;

		SimpleInflectionAffix other = (SimpleInflectionAffix) obj;

		if(affixTemplate == null) {
			if(other.affixTemplate != null) return false;
		} else if(!affixTemplate.equals(other.affixTemplate)) return false;

		if(affixMatcher == null) {
			if(other.affixMatcher != null) return false;
		} else if(!affixMatcher.equals(other.affixMatcher)) return false;

		return true;
	}
}