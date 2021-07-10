/*
 * Copyright 2011 Atteo.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.atteo.evo.inflector;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static java.lang.Character.toLowerCase;
import static java.lang.Character.toUpperCase;

public abstract class TwoFormInflector {

	private final List<Rule> rules = new ArrayList<Rule>();
	
	protected String getPlural(String word) {
		for (Rule rule : rules) {
			String result = rule.getPlural(word);
			if (result != null) {
				return result;
			}
		}
		return null;
	}
	
	protected void uncountable(String[] list) {
		rules.add(new CategoryRule(list, "", ""));
	}
	
	protected void irregular(String singular, String plural) {
		if (singular.charAt(0) == plural.charAt(0)) {
			rules.add(new RegExpRule("(?i)(" + singular.charAt(0) + ")" + singular.substring(1) + "$",
				"$1" + plural.substring(1)));
		} else {
			rules.add(new RegExpRule(toUpperCase(singular.charAt(0)) + "(?i)" + singular.substring(1) + "$",
				toUpperCase(plural.charAt(0))
					+ plural.substring(1)));
			rules.add(new RegExpRule(toLowerCase(singular.charAt(0)) + "(?i)" + singular.substring(1) + "$",
				toLowerCase(plural.charAt(0)) + plural.substring(1)));
		}
	}
	
	protected void irregular(String[][] list) {
		for (String[] pair : list) {
			irregular(pair[0], pair[1]);
		}
	}
	
	protected void rule(String singular, String plural) {
		rules.add(new RegExpRule(Pattern.compile(singular, Pattern.CASE_INSENSITIVE), plural));
	}
	
	protected void rule(String[][] list) {
		for (String[] pair : list) {
			rules.add(new RegExpRule(Pattern.compile(pair[0], Pattern.CASE_INSENSITIVE), pair[1]));
		}
	}

	protected void categoryRule(String[] list, String singular, String plural) {
		rules.add(new CategoryRule(list, singular, plural));
	}
}
