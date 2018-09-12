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

/**
 * Transforms English words from singular to plural form.
 * <p>
 * Examples:
 * <pre>
 *    English.plural("word") = "words";
 *
 *    English.plural("cat", 1) = "cat";
 *    English.plural("cat", 2) = "cats";
 * </pre>
 * </p>
 * <p>
 * Based on <a href="http://www.csse.monash.edu.au/~damian/papers/HTML/Plurals.html">
 * An Algorithmic Approach to English Pluralization</a> by Damian Conway.
 * </p>
 */
public class English extends TwoFormInflector {
	public static enum MODE {
		ENGLISH_ANGLICIZED, ENGLISH_CLASSICAL
	}

	private static final String[] CATEGORY_EX_ICES = { "codex", "murex",
			"silex", };

	private static final String[] CATEGORY_IX_ICES = { "radix", "helix", };

	private static final String[] CATEGORY_UM_A = { "bacterium",
			"agendum", "desideratum", "erratum", "stratum", "datum", "ovum",
			"extremum", "candelabrum", };

	// Always us -> i
	private static final String[] CATEGORY_US_I = { "alumnus", "alveolus",
			"bacillus", "bronchus", "locus", "nucleus", "stimulus", "meniscus",
			"thesaurus", };

	private static final String[] CATEGORY_ON_A = { "criterion",
			"perihelion", "aphelion", "phenomenon", "prolegomenon", "noumenon",
			"organon", "asyndeton", "hyperbaton", };

	private static final String[] CATEGORY_A_AE = { "alumna", "alga",
			"vertebra", "persona" };

	// Always o -> os
	private static final String[] CATEGORY_O_OS = { "albino",
			"archipelago", "armadillo", "commando", "crescendo", "fiasco",
			"ditto", "dynamo", "embryo", "ghetto", "guano", "inferno", "jumbo",
			"lumbago", "magneto", "manifesto", "medico", "octavo", "photo",
			"pro", "quarto", "canto", "lingo", "generalissimo", "stylo",
			"rhino", "casino", "auto", "macro", "zero", "todo"
	};

	// Classical o -> i  (normally -> os)
	private static final String[] CATEGORY_O_I = {
			"solo", "soprano", "basso", "alto", "contralto", "tempo", "piano",
			"virtuoso", };

	private static final String[] CATEGORY_EN_INA = {
			"stamen", "foramen", "lumen"
	};

	// -a to -as (anglicized) or -ata (classical)
	private static final String[] CATEGORY_A_ATA = {
			"anathema", "enema", "oedema", "bema", "enigma", "sarcoma",
			"carcinoma", "gumma", "schema", "charisma", "lemma", "soma",
			"diploma", "lymphoma", "stigma", "dogma", "magma", "stoma",
			"drama", "melisma", "trauma", "edema", "miasma"
	};

	private static final String[] CATEGORY_IS_IDES = {
			"iris", "clitoris"
	};

	// -us to -uses (anglicized) or -us (classical)
	private static final String[] CATEGORY_US_US = {
			"apparatus", "impetus", "prospectus", "cantus", "nexus", "sinus", "coitus",
			"plexus", "status", "hiatus"
	};

	private static final String[] CATEGORY_NONE_I = {
		"afreet", "afrit", "efreet"
	};

	private static final String[] CATEGORY_NONE_IM = {
		"cherub", "goy", "seraph"
	};

	private static final String[] CATEGORY_EX_EXES = {
		"apex", "latex", "vertex", "cortex", "pontifex", "vortex", "index", "simplex"
	};

	private static final String[] CATEGORY_IX_IXES = {
		"appendix"
	};

	private static final String[] CATEGORY_S_ES = {
		"acropolis", "chaos", "lens", "aegis",
		"cosmos", "mantis", "alias", "dais", "marquis", "asbestos",
		"digitalis", "metropolis", "atlas", "epidermis", "pathos",
		"bathos", "ethos", "pelvis", "bias", "gas", "polis", "caddis",
		"glottis", "rhinoceros", "cannabis", "glottis", "sassafras",
		"canvas", "ibis", "trellis"
	};

	private static final String[] CATEGORY_MAN_MANS = {
		"human", "Alabaman", "Bahaman", "Burman", "German", "Hiroshiman", "Liman", "Nakayaman", "Oklahoman",
		"Panaman", "Selman", "Sonaman", "Tacoman", "Yakiman", "Yokohaman", "Yuman"
	};

	private static English inflector = new English();


	public English() {
		this(MODE.ENGLISH_ANGLICIZED);
	}

	public English(MODE mode) {

		uncountable(new String[] {
			// 2. Handle words that do not inflect in the plural (such as fish, travois, chassis, nationalities ending
			// endings
			"fish", "ois", "sheep", "deer", "pox", "itis",

			// words
			"bison", "flounder", "pliers", "bream",
			"gallows", "proceedings", "breeches", "graffiti", "rabies",
			"britches", "headquarters", "salmon", "carp", "herpes",
			"scissors", "chassis", "high-jinks", "sea-bass", "clippers",
			"homework", "series", "cod", "innings", "shears",
			"contretemps", "jackanapes", "species", "corps", "mackerel",
			"swine", "debris", "measles", "trout", "diabetes", "mews",
			"tuna", "djinn", "mumps", "whiting", "eland", "news",
			"wildebeest", "elk", "pincers", "sugar" });

		// 4. Handle standard irregular plurals (mongooses, oxen, etc.)

		irregular(new String[][] {
				{ "child", "children" }, // classical
				{ "ephemeris", "ephemerides" }, // classical
				{ "mongoose", "mongoose" }, // anglicized
				{ "mythos", "mythoi" }, // classical
				// TODO: handle entire word correctly
				//{ "ox", "oxen" }, // classical
				{ "soliloquy", "soliloquies" }, // anglicized
				{ "trilby", "trilbys" }, // anglicized
				{ "genus", "genera" }, // classical
				{ "quiz", "quizzes" },
		});

		if (mode == MODE.ENGLISH_ANGLICIZED) {
			// Anglicized plural
			irregular(new String[][] {
					{ "beef", "beefs" },
					{ "brother", "brothers" },
					{ "cow", "cows" },
					{ "genie", "genies" },
					{ "money", "moneys" },
					{ "octopus", "octopuses" },
					{ "opus", "opuses" },
				});
		} else if (mode == MODE.ENGLISH_CLASSICAL) {
			// Classical plural
			irregular(new String[][] { { "beef", "beeves"},
					{ "brother", "brethren" },
					{ "cow", "kine" }, { "genie", "genii"},
					{ "money", "monies" },
					{ "octopus", "octopodes" },
					{ "opus", "opera" },
			});
		}

		categoryRule(CATEGORY_MAN_MANS, "", "s");

		// questionable
		/*
		 rule(new String[][] {
				{ "(ness)$", "$1" },
				{ "(ality)$", "$1" }
				{ "(icity)$", "$1" },
				{ "(ivity)$", "$1" },
		});
		 */
		// 5. Handle irregular inflections for common suffixes
		rule(new String[][] {
				{ "man$", "men" },
				{ "([lm])ouse$", "$1ice" },
				{ "tooth$", "teeth" },
				{ "goose$", "geese" },
				{ "foot$", "feet" },
				{ "zoon$", "zoa" },
				{ "([csx])is$", "$1es" },
		});

		// 6. Handle fully assimilated classical inflections
		categoryRule(CATEGORY_EX_ICES, "ex", "ices");
		categoryRule(CATEGORY_IX_ICES, "ix", "ices");
		categoryRule(CATEGORY_UM_A, "um", "a");
		categoryRule(CATEGORY_ON_A, "on", "a");
		categoryRule(CATEGORY_A_AE, "a", "ae");

		// 7. Handle classical variants of modern inflections
		if (mode == MODE.ENGLISH_CLASSICAL) {
			rule(new String[][]{
					{ "trix$", "trices" },
					{ "eau$", "eaux" },
					{ "ieu$", "ieux" },
					{ "(..[iay])nx$", "$1nges" },
			});
			categoryRule(CATEGORY_EN_INA, "en", "ina");
			categoryRule(CATEGORY_A_ATA, "a", "ata");
			categoryRule(CATEGORY_IS_IDES, "is", "ides");
			categoryRule(CATEGORY_US_US, "", "");
			categoryRule(CATEGORY_O_I, "o", "i");
			categoryRule(CATEGORY_NONE_I, "", "i");
			categoryRule(CATEGORY_NONE_IM, "", "im");
			categoryRule(CATEGORY_EX_EXES, "ex", "ices");
			categoryRule(CATEGORY_IX_IXES, "ix", "ices");
		}

		categoryRule(CATEGORY_US_I, "us", "i");

		rule("([cs]h|[zx])$", "$1es");
		categoryRule(CATEGORY_S_ES, "", "es");
		categoryRule(CATEGORY_IS_IDES, "", "es");
		categoryRule(CATEGORY_US_US, "", "es");
		rule("(us)$", "$1es");
		categoryRule(CATEGORY_A_ATA, "", "s");

		// The suffixes -ch, -sh, and -ss all take -es in the plural (churches,
		// classes, etc)...
		rule(new String[][] { { "([cs])h$", "$1hes" }, { "ss$", "sses" } });

		// Certain words ending in -f or -fe take -ves in the plural (lives,
		// wolves, etc)...
		rule(new String[][] {
				{ "([aeo]l)f$", "$1ves" },
				{ "([^d]ea)f$", "$1ves" },
				{ "(ar)f$", "$1ves" },
				{ "([nlw]i)fe$", "$1ves" }
		});

		// Words ending in -y take -ys
		rule(new String[][] { { "([aeiou])y$", "$1ys" }, { "y$", "ies" }, });

		// Some words ending in -o take -os (including does preceded by a vowel)
		categoryRule(CATEGORY_O_I, "o", "os");
		categoryRule(CATEGORY_O_OS, "o", "os");
		rule("([aeiou])o$", "$1os");
		// The rest take -oes
		rule("o$", "oes");

		rule("ulum$", "ula");

		categoryRule(CATEGORY_A_ATA, "", "es");

		rule("s$", "ses");
		// Otherwise, assume that the plural just adds -s
		rule("$", "s");
	}

	/**
	 * Returns plural form of the given word.
	 *
	 * @param word word in singular form
	 * @return plural form of the word
	 */
	@Override
	public String getPlural(String word) {
		return super.getPlural(word);
	}

	/**
	 * Returns singular or plural form of the word based on count.
	 *
	 * @param word word in singular form
	 * @param count word count
	 * @return form of the word correct for given count
	 */
	public String getPlural(String word, int count) {
		if (count == 1) {
			return word;
		}
		return getPlural(word);
	}

	/**
	 * Returns plural form of the given word.
	 * <p>
	 * For instance:
	 * <pre>
	 * {@code
	 * English.plural("cat") == "cats";
	 * }
	 * </pre>
	 * </p>
	 * @param word word in singular form
	 * @return plural form of given word
	 */
	public static String plural(String word) {
		return inflector.getPlural(word);
	}

	/**
	 * Returns singular or plural form of the word based on count.
	 * <p>
	 * For instance:
	 * <pre>
	 * {@code
	 * English.plural("cat", 1) == "cat";
	 * English.plural("cat", 2) == "cats";
	 * }
	 * </pre>
	 * </p>
	 * @param word word in singular form
	 * @param count word count
	 * @return form of the word correct for given count
	 */
	public static String plural(String word, int count) {
		return inflector.getPlural(word, count);
	}

	public static void setMode(MODE mode) {
		English newInflector = new English(mode);
		inflector = newInflector;
	}
}
