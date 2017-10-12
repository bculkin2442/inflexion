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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Simple implementation of {@link InflectionAffix}
 *
 * @author EVE
 */
public class SimpleInflectionAffix implements InflectionAffix {
	/* Format string for toString. */
	private static final String TOSTRING_FMT =
	        "SimpleInflectionAffix [affixTemplate=%s, affixMatcher=%s]";

	/* Affix template. */
	private final String affixTmplate;
	/* Affix matching. */
	private final Pattern affixMtcher;

	/**
	 * Create a new inflection affix.
	 *
	 * @param affixTemplate
	 * 	The template for applying the affix, Should be a printf-style
	 * 	format string with a single string blank.
	 *
	 * @param affixMatcher
	 * 	The regular expression that matches the affix on strings. The
	 * 	'stem' or word should be placed in a named capturing group named
	 * 	'stem'.
	 */
	public SimpleInflectionAffix(final String affixTemplate, final Pattern affixMatcher) {
		affixTmplate = affixTemplate;
		affixMtcher = affixMatcher;
	}

	@Override
	public boolean hasAffix(final String word) {
		return affixMtcher.matcher(word).matches();
	}

	@Override
	public String deaffix(final String word) {
		final Matcher matcher = affixMtcher.matcher(word);
		matcher.matches();

		return matcher.group("stem");
	}

	@Override
	public String affix(final String word) {
		return String.format(affixTmplate, word);
	}

	@Override
	public String toString() {
		return String.format(TOSTRING_FMT, affixTmplate, affixMtcher);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;

		result = prime * result + (affixMtcher == null ? 0 : affixMtcher.hashCode());
		result = prime * result + (affixTmplate == null ? 0 : affixTmplate.hashCode());

		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) return true;

		if (obj == null) return false;

		if (!(obj instanceof SimpleInflectionAffix)) return false;

		final SimpleInflectionAffix other = (SimpleInflectionAffix) obj;

		if (affixTmplate == null) {
			if (other.affixTmplate != null) return false;
		} else if (!affixTmplate.equals(other.affixTmplate)) return false;

		if (affixMtcher == null) {
			if (other.affixMtcher != null) return false;
		} else if (!affixMtcher.equals(other.affixMtcher)) return false;

		return true;
	}
}
