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
	private interface Rule {
		String getPlural(String singular);
	}

	private static class RegExpRule implements Rule {
		private final Pattern singular;
		private final String plural;

		private RegExpRule(Pattern singular, String plural) {
			this.singular = singular;
			this.plural = plural;
		}

		@Override
		public String getPlural(String word) {
			StringBuffer buffer = new StringBuffer();
			Matcher matcher = singular.matcher(word);
			if (matcher.find()) {
				matcher.appendReplacement(buffer, plural);
				matcher.appendTail(buffer);
				return buffer.toString();
			}
			return null;
		}
	}

	private static class CategoryRule implements Rule {
		private final String[] list;
		private final String singular;
		private final String plural;

		public CategoryRule(String[] list, String singular, String plural) {
			this.list = list;
			this.singular = singular;
			this.plural = plural;
		}

		@Override
		public String getPlural(String word) {
			String lowerWord = word.toLowerCase();
			for (String suffix : list) {
				if (lowerWord.endsWith(suffix)) {
					if (!lowerWord.endsWith(singular)) {
						throw new RuntimeException("Internal error");
					}
					return word.substring(0, word.length() - singular.length()) + plural;
				}
			}
			return null;
		}
	}
	
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
			rules.add(new RegExpRule(Pattern.compile("(?i)(" + singular.charAt(0) + ")" + singular.substring(1)
					+ "$"), "$1" + plural.substring(1)));
		} else {
			rules.add(new RegExpRule(Pattern.compile(Character.toUpperCase(singular.charAt(0)) + "(?i)"
					+ singular.substring(1) + "$"), Character.toUpperCase(plural.charAt(0))
					+ plural.substring(1)));
			rules.add(new RegExpRule(Pattern.compile(Character.toLowerCase(singular.charAt(0)) + "(?i)"
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
