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

/**
 * Exception thrown when something goes wrong with inflection.
 *
 * @author EVE
 *
 */
public class InflectionException extends RuntimeException {
	private static final long serialVersionUID = 5680541587449153748L;

	/**
	 * Create a new inflection exception with the given message and cause.
	 *
	 * @param message
	 *                The message of the exception.
	 *
	 * @param cause
	 *                The cause of the exception.
	 */
	public InflectionException(final String message, final Throwable cause) {
		super(message, cause);
	}

	/**
	 * Create a new inflection exception with the given message.
	 *
	 * @param message
	 *                The message of the exception.
	 */
	public InflectionException(final String message) {
		super(message);
	}
}
