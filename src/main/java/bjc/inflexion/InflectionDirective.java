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

import static java.util.Arrays.asList;

import java.util.List;

/**
 * Represents a directive in a inflection string.
 *
 * @author bjculkin
 *
 */
public final class InflectionDirective {
	/**
	 * Emit error message
	 * 
	 * @param dir The directive causing the error
	 * @param curPos The position in the string
	 * @param i The current index
	 * @param msg The detail message
	 * @param props Properties for the detail message.
	 * 
	 * @return A properly formatted error message
	 */
	public static String error(String dir, int curPos, int i, String msg,
			Object... props) {
		return String.format(
				"%s (at position %d in %s directive starting at position %d)",
				String.format(msg, props), curPos + i, dir, curPos);
	}

	/**
	 * The type of the directive.
	 */
	public final DirectiveType type;

	/**
	 * The string value of the directive.
	 *
	 * Currently, set for literals and variable references, as well as nouns.
	 */
	public String litString;

	/**
	 * The integer value of the directive.
	 *
	 * Currently set for numeric values.
	 */
	public int numNumber;

	/**
	 * Is this directives body referencing a variable instead of a literal?
	 */
	public boolean isVRef = false;

	/**
	 * The options for a directive.
	 */
	public Options opts;

	/**
	 * The directives contained in a sequence.
	 */
	public List<InflectionDirective> listDir;

	/**
	 * Create a new inflection directive.
	 *
	 * @param type
	 *             The type of the directive.
	 */
	public InflectionDirective(DirectiveType type) {
		this.type = type;

		switch (type) {
		default:
			throw new IllegalArgumentException(
					"Unhandled or wrong arguments (none) for directive type " + type);
		}
	}

	/**
	 * Create a new inflection directive.
	 *
	 * @param type
	 *               The type of the directive.
	 * @param strang
	 *               The string value for the directive.
	 */
	public InflectionDirective(DirectiveType type, String strang) {
		this.type = type;

		// Set default options.
		switch (type) {
		case NUMERIC:
			this.opts = new NumericOptions();
			break;
		case NOUN:
			this.opts = new NounOptions();
			break;
		default:
			// No options for these types
		}

		switch (type) {
		case LITERAL:
		case VARIABLE:
		case NUMERIC: // Reference to a numeric variable
		case NOUN:
			this.litString = strang;
			break;
		default:
			throw new IllegalArgumentException(
					"Unhandled or wrong arguments (1 string) for directive type "
							+ type);

		}
	}

	/**
	 * Create a new inflection directive.
	 *
	 * @param type
	 *             The type of the directive.
	 * @param num
	 *             The number value for the directive.
	 */
	public InflectionDirective(DirectiveType type, int num) {
		this.type = type;

		switch (type) {
		case NUMERIC:
			this.numNumber = num;
			this.opts = new NumericOptions();
			break;
		default:
			throw new IllegalArgumentException(
					"Unhandled or wrong arguments (1 number) for directive type "
							+ type);

		}
	}

	/**
	 * Create a new inflection directive.
	 *
	 * @param type
	 *                The type of the directive.
	 * @param listDir
	 *                The directive list value for the directive.
	 */
	public InflectionDirective(DirectiveType type,
			List<InflectionDirective> listDir) {
		this.type = type;

		switch (type) {
		case SEQ:
			this.listDir = listDir;
			break;
		default:
			throw new IllegalArgumentException(
					"Unhandled or wrong arguments (1 list of directives) for directive type "
							+ type);

		}
	}

	/**
	 * Create a new literal directive.
	 *
	 * @param strang
	 *               The literal string the directive represents.
	 * @return A literal directive for the given string.
	 */
	public static InflectionDirective literal(String strang) {
		return new InflectionDirective(DirectiveType.LITERAL, strang);
	}

	/**
	 * Create a new variable directive.
	 *
	 * @param strang
	 *               The name of the variable to interpolate into the string.
	 * @return A directive that says to interpolate the given value.
	 */
	public static InflectionDirective variable(String strang) {
		return new InflectionDirective(DirectiveType.VARIABLE, strang);
	}

	/**
	 * Create a new numeric directive.
	 *
	 * @param num
	 *            The value of the directive,
	 * @return A directive that sets the current number to the specific value.
	 */
	public static InflectionDirective numeric(int num) {
		return new InflectionDirective(DirectiveType.NUMERIC, num);
	}

	/**
	 * Create a new numeric directive.
	 *
	 * @param strang
	 *               The name of a variable that holds the value of the directive,
	 * @return A directive that sets the current number to the specific value.
	 */
	public static InflectionDirective numeric(String strang) {
		return new InflectionDirective(DirectiveType.NUMERIC, strang);
	}

	/**
	 * Create a new noun directive.
	 *
	 * @param strang
	 *               The noun, or the name of the variable for the noun.
	 * @return A directive that inflects the specified noun.
	 */
	public static InflectionDirective noun(String strang) {
		return new InflectionDirective(DirectiveType.NOUN, strang);
	}

	/**
	 * Create a sequenced set of directives.
	 *
	 * @param list
	 *             The directives to sequence.
	 * @return A sequence directive.
	 */
	public static InflectionDirective seq(List<InflectionDirective> list) {
		return new InflectionDirective(DirectiveType.SEQ, list);
	}

	/**
	 * Create a sequenced set of directives.
	 *
	 * @param arr
	 *            The directives to sequence.
	 * @return A sequence directive.
	 */
	public static InflectionDirective seq(InflectionDirective... arr) {
		return new InflectionDirective(DirectiveType.SEQ, asList(arr));
	}

	/**
	 * Set the numeric options for this directive.
	 *
	 * @param numOpts
	 *                The numeric options of the directive.
	 * @return The directive.
	 */
	public InflectionDirective options(NumericOptions numOpts) {
		if (type != DirectiveType.NUMERIC)
			throw new IllegalArgumentException(
					"Directive type " + type + " does not take numeric options");
		this.opts = numOpts;

		return this;
	}

	/**
	 * Set the noun options for this directive.
	 *
	 * @param nounOpts
	 *                 The noun options of the directive.
	 * @return The directive.
	 */
	public InflectionDirective options(NounOptions nounOpts) {
		if (type != DirectiveType.NOUN)
			throw new IllegalArgumentException(
					"Directive type " + type + " does not take noun options");
		this.opts = nounOpts;

		return this;
	}
}