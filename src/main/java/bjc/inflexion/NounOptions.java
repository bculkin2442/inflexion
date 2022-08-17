/**
 * (C) Copyright 2022 Benjamin Culkin.
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
package bjc.inflexion;

import java.util.List;

/**
 * Options for a noun directive.
 *
 * @author bjculkin
 *
 */
public class NounOptions extends Options {
	/**
	 * Use the classical inflection for the noun.
	 */
	public boolean classical;

	/**
	 * Inflect as plural, regardless of current count.
	 */
	public boolean plural;

	/**
	 * Inflect as singular, regardless of current count.
	 */
	public boolean singular;

	/**
	 * Create a new set of noun options from a string.
	 *
	 * @param options
	 *                    The string to create options from.
	 * @param curPos
	 *                    The current position into the string.
	 * @param startFold Whether to start with folding on
	 * @param parseErrors
	 *                    The current list of parsing errors.
	 */
	public NounOptions(String options, int curPos, boolean startFold, List<String> parseErrors) {
		if (options.equals(""))
			return;

		boolean doingCaseFolding = startFold;

		for (int i = 0; i < options.length(); i++) {
			char ci = options.charAt(i);

			if (doingCaseFolding && Character.isLowerCase(ci)) {
				continue;
			} else if (Character.isUpperCase(ci)) {
				doingCaseFolding = true;

				ci = Character.toLowerCase(ci);
			}

			switch (ci) {
			case 'c':
				classical = true;
				break;
			case 'p':
				plural = true;
				break;
			case 's':
				singular = true;
				break;
			default:
				parseErrors.add(error(curPos, i, "Unhandled option %c", ci));
			}
		}
	}

	/**
	 * Create an empty set of noun options.
	 */
	public NounOptions() {
	}

	// Emit error message
	private static String error(int curPos, int i, String msg, Object... props) {
		return InflectionDirective.error("N", curPos, i, msg, props);
	}
}