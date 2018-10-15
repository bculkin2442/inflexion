/**
 * (C) Copyright 2018 Benjamin Culkin.
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

class InflectPair {
	public String exp;
	public Object[] pars;
	
	public InflectPair(String str, Object... pars) {
		this.exp = str;
		this.pars = pars;
	}
	
	public static InflectPair pair(String str, Object... pars) {
		return new InflectPair(str, pars);
	}
}