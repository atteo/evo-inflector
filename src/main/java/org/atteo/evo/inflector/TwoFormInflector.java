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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class TwoFormInflector {
	private static class Rule {
		private Pattern singular;
		private String plural;
		private Rule(Pattern singular, String plural) {
			this.singular = singular;
			this.plural = plural;
		}

		public Pattern getSingular() {
			return singular;
		}

		public String getPlural() {
			return plural;
		}
	}
	
	private List<Rule> rules = new ArrayList<Rule>();
	
	protected String getPlural(String word) {
		StringBuffer buffer = new StringBuffer();

		for (Rule rule : rules) {
			Matcher matcher = rule.getSingular().matcher(word);
			if (matcher.find()) {
				matcher.appendReplacement(buffer, rule.getPlural());
				matcher.appendTail(buffer);
				return buffer.toString();
			}
		}
		return null;
	}
	
	protected void uncountable(String word) {
		rules.add(new Rule(Pattern.compile("(?i)(" + word + ")$"), "$1"));
	}
	
	protected void uncountable(String[] list) {
		StringBuilder builder = new StringBuilder();
		builder.append("(?i)(");
		builder.append(list[0]);
		for (String word : list) {
			builder.append("|").append(word);
		}
		builder.append(")$");
		rules.add(new Rule(Pattern.compile(builder.toString()), "$1"));
	}
	
	protected void irregular(String singular, String plural) {
		if (singular.charAt(0) == plural.charAt(0)) {
			rules.add(new Rule(Pattern.compile("(?i)(" + singular.charAt(0) + ")" + singular.substring(1)
					+ "$"), "$1" + plural.substring(1)));
		} else {
			rules.add(new Rule(Pattern.compile(Character.toUpperCase(singular.charAt(0)) + "(?i)"
					+ singular.substring(1) + "$"), Character.toUpperCase(plural.charAt(0))
					+ plural.substring(1)));
			rules.add(new Rule(Pattern.compile(Character.toLowerCase(singular.charAt(0)) + "(?i)"
					+ singular.substring(1) + "$"), Character.toLowerCase(plural.charAt(0))
					+ plural.substring(1)));
		}
	}
	
	protected void irregular(String[][] list) {
		for (String[] pair : list) {
			irregular(pair[0], pair[1]);
		}
	}
	
	protected void rule(String singular, String plural) {
		rules.add(new Rule(Pattern.compile(singular, Pattern.CASE_INSENSITIVE), plural));
	}
	
	protected void rule(String[][] list) {
		for (String[] pair : list) {
			rules.add(new Rule(Pattern.compile(pair[0], Pattern.CASE_INSENSITIVE), pair[1]));
		}
	}
	
	protected void categoryRule(String[] list, String singular, String plural) {
		StringBuilder builder = new StringBuilder();
		
		builder.append("(?=").append(list[0]);
		for (String word : list) {
			builder.append("|").append(word);
		}
		builder.append(")");
		builder.append(singular);
		rules.add(new Rule(Pattern.compile(builder.toString(), Pattern.CASE_INSENSITIVE), plural));
	}
}
