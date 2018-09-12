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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.tools.bzip2.CBZip2InputStream;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeNotNull;

public class EnglishInflectorTest {
	private final English inflector = new English();

	@Test
	public void wiktionaryList() throws IOException {
		InputStream compressedStream = EnglishInflectorTest.class.getResourceAsStream(
			"/enwiktionary-latest-pages-articles.xml.bz2");
		if (compressedStream == null) {
			System.err.println("\nFull test requires wiktionary dump which was not found\n" +
					"To run rull test do the following:\n" +
					"cd src/test/resources\n" +
					"wget http://download.wikimedia.org/enwiktionary/latest/" +
					"enwiktionary-latest-pages-articles.xml.bz2\n");
			assumeNotNull(compressedStream);
			return;
		}
		// Read 2 bytes due to bug in BZip library
		compressedStream.read();
		compressedStream.read();
		InputStream stream = new CBZip2InputStream(compressedStream);
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

		// Pattern find word name
		Pattern titlePattern = Pattern.compile("<title>([^<]+)</title>");
		// Pattern to find beginning of wiki text
		Pattern textPattern = Pattern.compile("<text");
		// Pattern to find rank definition
		Pattern rankPattern = Pattern.compile("\\{\\{rank");
		// Pattern to find noun definition
		Pattern enNounPattern = Pattern.compile("\\{\\{en-noun([a-z0-9\\|\\-\\[\\]\\?\\!=]*)\\}\\}");

		Pattern wordPattern = Pattern.compile("([a-zA-Z\\-]+)");

		String line;
		String word = "";
		int text = 0;
		int count = 0;
		int basicCount = 0;
		int wrong = 0;
		int basicWrong = 0;
		int wrongNoPlural = 0;
		int wrongUncountable = 0;
		boolean basicWord = false;
		while ((line = reader.readLine()) != null) {
			Matcher titleMatcher = titlePattern.matcher(line);
			if (titleMatcher.find()) {
				word = titleMatcher.group(1);
				if (word.startsWith("Wiktionary:")) {
					continue;
				}
				basicWord = false;
				text = 0;
				continue;
			}
			Matcher textMatcher = textPattern.matcher(line);
			if (textMatcher.find()) {
				text++;
				continue;
			}
			Matcher rankMatcher = rankPattern.matcher(line);
			if (rankMatcher.find()) {
				basicWord = true;
				basicCount++;
			}
			if (text != 1) {
				continue;
			}
			Matcher enNounMatcher = enNounPattern.matcher(line);
			if (enNounMatcher.find()) {
				// only first
				/*
				if (text != 1) {
					continue;
				}
				*/
				text++;
				count++;
				if (count % 5000 == 0) {
					System.out.println(count);
				}
				String[] rules = enNounMatcher.group(1).split("\\|");
				List<String> plurals = new ArrayList<String>();

				boolean uncountable = false;
				boolean noPlural = false;
				for (String rule : rules) {
					if (rule.isEmpty()) {
						continue;
					}
					if ("-".equals(rule)) {
						plurals.add(word);
						uncountable = true;
					} else if ("s".equals(rule)) {
						plurals.add(word + "s");
					} else if ("es".equals(rule)) {
						plurals.add(word + "es");
					} else if ("!".equals(rule)) {
						plurals.add("plural not attested");
						uncountable = true;
					} else if ("?".equals(rule)) {
						plurals.add("unknown");
						noPlural = true;
					} else {
						Matcher matcher = wordPattern.matcher(rule);
						if (matcher.matches()) {
							plurals.add(rule);
						}
					}
				}
				if (plurals.isEmpty()) {
					plurals.add(word + "s");
				}

				String calculatedPlural = inflector.getPlural(word);
				boolean ok = false;
				for (String plural : plurals) {
					if (plural.equals(calculatedPlural)) {
						ok = true;
						break;
					}
				}

				if (!ok) {
					wrong++;
					if (uncountable) {
						wrongUncountable++;
					} else if (noPlural) {
						wrongNoPlural++;
					}
					if (basicWord) {
						System.out.println("basic word: " + word + " got: " + calculatedPlural + ", but expected "
								+ enNounMatcher.group(1));
						basicWrong++;
					} else {
						System.out.println(word + " got: " + calculatedPlural + ", but expected "
								+ enNounMatcher.group(1));
					}
				}
			}
		}
		reader.close();
		compressedStream.close();

		float correct = (count - wrong)*100/(float)count;
		float basicCorrect = (basicCount - basicWrong) * 100 / (float)basicCount;
		float wrongUncountablePercent = wrongUncountable * 100 / (float)count;
		float wrongNoPluralPercent = wrongNoPlural * 100 / (float)count;
		int justPlainWrong = wrong - wrongUncountable - wrongNoPlural;
		float justPlainWrongPercent = justPlainWrong * 100 / (float)count;
		System.out.println("Words checked: " + count + " (" + basicCount + " basic words)");
		System.out.println("Correct: " + correct + "% (" + basicCorrect + "% basic words)");
		System.out.println("Errors: ");
		System.out.println("    Uncountable: " + wrongUncountable + " (" + wrongUncountablePercent + "%)");
		System.out.println("    No plural form specified: " + wrongNoPlural + " (" + wrongNoPluralPercent + "%)");
		System.out.println("    Incorrect answer: " + justPlainWrong + " (" + justPlainWrongPercent + "%)");
		assertTrue(correct > 68);
		assertTrue(basicWrong == 1);
	}

	@Test
	public void exampleWordList() {
		check(new String[][] {
			{ "alga", "algae" },
			{ "nova", "novas" },
			{ "dogma", "dogmas" },
			{ "Woman", "Women" },
			{ "church", "churches" },
			{ "quick_chateau", "quick_chateaus" },
			{ "codex", "codices" },
			{ "index", "indexes" },
			{ "NightWolf", "NightWolves" },
			{ "Milieu", "Milieus" },
			{ "basis", "bases" },
			{ "iris", "irises" },
			{ "phalanx", "phalanxes" },
			{ "tempo", "tempos" },
			{ "foot", "feet" },
			{ "series", "series" },
			{ "WorldAtlas", "WorldAtlases" },
			{ "wish", "wishes" },
			{ "Bacterium", "Bacteria" },
			{ "medium", "mediums" },
			{ "Genus", "Genera" },
			{ "stimulus", "stimuli" },
			{ "opus", "opuses" },
			{ "status", "statuses" },
			{ "Box", "Boxes" },
			{ "ferry", "ferries" },
			{ "protozoon", "protozoa" },
			{ "cherub", "cherubs" },
			{ "human", "humans" },
			{ "sugar", "sugar" },
			{ "virus", "viruses" },
			{ "gastrostomy", "gastrostomies" },
			{ "baculum", "bacula" },
			{ "pancreas", "pancreases" },
			{ "todo", "todos"},
			{ "person", "persons"},
			{ "baculumulum", "baculumula" }, // https://github.com/atteo/evo-inflector/pull/18
		});
	}

	@Test
	public void withCount() {
		assertEquals("cat", inflector.getPlural("cat", 1));
		assertEquals("cats", inflector.getPlural("cat", 2));

		assertEquals("demoness", inflector.getPlural("demoness", 1));
		assertEquals("demonesses", inflector.getPlural("demoness", 2));
	}

	@Test
	public void staticMethods() {
		assertEquals("sulfimides", English.plural("sulfimide"));
		assertEquals("semifluids", English.plural("semifluid", 2));
	}

	private void check(String[][] list) {
		for (String[] pair : list) {
			check(pair[0], pair[1]);
		}
	}

	private void check(String singular, String plural) {
		assertEquals(plural, inflector.getPlural(singular));
	}
}
