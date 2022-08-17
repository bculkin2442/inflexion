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

/**
 * The type of the directive in the inflection string.
 *
 * @author bjculkin
 *
 */
public enum DirectiveType {
	/**
	 * A literal string. Not inflected in any way.
	 */
	LITERAL,
	/**
	 * A variable reference. Not inflected in any way.
	 */
	VARIABLE,
	/**
	 * Sets the current number.
	 */
	NUMERIC,
	/**
	 * Prints an inflected noun.
	 */
	NOUN,
	/**
	 * Represents a sequence of directives.
	 */
	SEQ
}