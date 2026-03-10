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
public class English {
    public enum MODE {
        ENGLISH_ANGLICIZED,
        ENGLISH_CLASSICAL
    }

    private static final String[] CATEGORY_EX_ICES = {
        "codex", "murex", "silex",
    };

    private static final String[] CATEGORY_IX_ICES = {
        "radix", "helix",
    };

    private static final String[] CATEGORY_UM_A = {
        "bacterium", "agendum", "desideratum", "erratum", "stratum", "datum", "ovum", "extremum", "candelabrum",
    };

    // Always us -> i
    private static final String[] CATEGORY_US_I = {
        "alumnus", "alveolus", "bacillus", "bronchus", "locus", "nucleus", "stimulus", "meniscus", "thesaurus",
    };

    private static final String[] CATEGORY_ON_A = {
        "criterion",
        "perihelion",
        "aphelion",
        "phenomenon",
        "prolegomenon",
        "noumenon",
        "organon",
        "asyndeton",
        "hyperbaton",
    };

    private static final String[] CATEGORY_A_AE = {"alumna", "alga", "vertebra", "persona"};

    // Always o -> os
    private static final String[] CATEGORY_O_OS = {
        "albino",
        "archipelago",
        "armadillo",
        "commando",
        "crescendo",
        "fiasco",
        "ditto",
        "dynamo",
        "embryo",
        "ghetto",
        "guano",
        "inferno",
        "jumbo",
        "lumbago",
        "magneto",
        "manifesto",
        "medico",
        "octavo",
        "photo",
        "pro",
        "quarto",
        "canto",
        "lingo",
        "generalissimo",
        "stylo",
        "rhino",
        "casino",
        "auto",
        "macro",
        "zero",
        "todo"
    };

    // Classical o -> i  (normally -> os)
    private static final String[] CATEGORY_O_I = {
        "solo", "soprano", "basso", "alto", "contralto", "tempo", "piano", "virtuoso",
    };

    private static final String[] CATEGORY_EN_INA = {"stamen", "foramen", "lumen"};

    // -a to -as (anglicized) or -ata (classical)
    private static final String[] CATEGORY_A_ATA = {
        "anathema", "enema", "oedema", "bema", "enigma", "sarcoma",
        "carcinoma", "gumma", "schema", "charisma", "lemma", "soma",
        "diploma", "lymphoma", "stigma", "dogma", "magma", "stoma",
        "drama", "melisma", "trauma", "edema", "miasma"
    };

    private static final String[] CATEGORY_IS_IDES = {"iris", "clitoris"};

    // -us to -uses (anglicized) or -us (classical)
    private static final String[] CATEGORY_US_US = {
        "apparatus", "impetus", "prospectus", "cantus", "nexus", "sinus", "coitus", "plexus", "status", "hiatus"
    };

    private static final String[] CATEGORY_NONE_I = {"afreet", "afrit", "efreet"};

    private static final String[] CATEGORY_NONE_IM = {"cherub", "goy", "seraph"};

    private static final String[] CATEGORY_EX_EXES = {
        "apex", "latex", "vertex", "cortex", "pontifex", "vortex", "index", "simplex"
    };

    private static final String[] CATEGORY_IX_IXES = {"appendix"};

    private static final String[] CATEGORY_S_ES = {
        "acropolis",
        "chaos",
        "lens",
        "aegis",
        "cosmos",
        "mantis",
        "alias",
        "dais",
        "marquis",
        "asbestos",
        "digitalis",
        "metropolis",
        "atlas",
        "epidermis",
        "pathos",
        "bathos",
        "ethos",
        "pelvis",
        "bias",
        "gas",
        "polis",
        "caddis",
        "glottis",
        "rhinoceros",
        "cannabis",
        "glottis",
        "sassafras",
        "canvas",
        "ibis",
        "trellis"
    };

    private static final String[] CATEGORY_MAN_MANS = {
        "human",
        "Alabaman",
        "Bahaman",
        "Burman",
        "German",
        "Hiroshiman",
        "Liman",
        "Nakayaman",
        "Oklahoman",
        "Panaman",
        "Selman",
        "Sonaman",
        "Tacoman",
        "Yakiman",
        "Yokohaman",
        "Yuman"
    };

    private static final CompiledInflector ANGLICIZED_ENGINE = buildEngine(MODE.ENGLISH_ANGLICIZED);
    private static final CompiledInflector CLASSICAL_ENGINE = buildEngine(MODE.ENGLISH_CLASSICAL);

    private static volatile English inflector = new English();

    private final CompiledInflector engine;

    public English() {
        this(MODE.ENGLISH_ANGLICIZED);
    }

    public English(MODE mode) {
        engine = mode == MODE.ENGLISH_CLASSICAL ? CLASSICAL_ENGINE : ANGLICIZED_ENGINE;
    }

    /**
     * Returns plural form of the given word.
     *
     * @param word word in singular form
     * @return plural form of the word
     */
    public String getPlural(String word) {
        return engine.pluralize(word);
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
        var newInflector = new English(mode);
        inflector = newInflector;
    }

    private static CompiledInflector buildEngine(MODE mode) {
        CompiledInflector.Builder builder = CompiledInflector.builder();

        builder.addIdentityCategory(new String[] {
            "fish",
            "ois",
            "sheep",
            "deer",
            "pox",
            "itis",
            "bison",
            "flounder",
            "pliers",
            "bream",
            "gallows",
            "proceedings",
            "breeches",
            "graffiti",
            "rabies",
            "britches",
            "headquarters",
            "salmon",
            "carp",
            "herpes",
            "scissors",
            "chassis",
            "high-jinks",
            "sea-bass",
            "clippers",
            "homework",
            "series",
            "cod",
            "innings",
            "shears",
            "contretemps",
            "jackanapes",
            "species",
            "corps",
            "mackerel",
            "swine",
            "debris",
            "measles",
            "trout",
            "diabetes",
            "mews",
            "tuna",
            "djinn",
            "mumps",
            "whiting",
            "eland",
            "news",
            "wildebeest",
            "elk",
            "pincers",
            "sugar"
        });

        addIrregular(builder, "child", "children");
        addIrregular(builder, "ephemeris", "ephemerides");
        addIrregular(builder, "mongoose", "mongoose");
        addIrregular(builder, "mythos", "mythoi");
        addIrregular(builder, "soliloquy", "soliloquies");
        addIrregular(builder, "trilby", "trilbys");
        addIrregular(builder, "genus", "genera");
        addIrregular(builder, "quiz", "quizzes");

        if (mode == MODE.ENGLISH_ANGLICIZED) {
            addIrregular(builder, "beef", "beefs");
            addIrregular(builder, "brother", "brothers");
            addIrregular(builder, "cow", "cows");
            addIrregular(builder, "genie", "genies");
            addIrregular(builder, "money", "moneys");
            addIrregular(builder, "octopus", "octopuses");
            addIrregular(builder, "opus", "opuses");
        } else {
            addIrregular(builder, "beef", "beeves");
            addIrregular(builder, "brother", "brethren");
            addIrregular(builder, "cow", "kine");
            addIrregular(builder, "genie", "genii");
            addIrregular(builder, "money", "monies");
            addIrregular(builder, "octopus", "octopodes");
            addIrregular(builder, "opus", "opera");
        }

        builder.addCategoryRule(CATEGORY_MAN_MANS, 0, "s");

        builder.addSuffixRule("man", 2, "en");
        builder.addSuffixRule("mouse", 4, "ice");
        builder.addSuffixRule("louse", 4, "ice");
        builder.addSuffixRule("tooth", 4, "eeth");
        builder.addSuffixRule("goose", 4, "eese");
        builder.addSuffixRule("foot", 3, "eet");
        builder.addSuffixRule("zoon", 3, "oa");
        builder.addSuffixRule("is", CompiledInflector.previousCharIn("csx"), 2, "es");

        builder.addCategoryRule(CATEGORY_EX_ICES, 2, "ices");
        builder.addCategoryRule(CATEGORY_IX_ICES, 2, "ices");
        builder.addCategoryRule(CATEGORY_UM_A, 2, "a");
        builder.addCategoryRule(CATEGORY_ON_A, 2, "a");
        builder.addCategoryRule(CATEGORY_A_AE, 1, "ae");

        if (mode == MODE.ENGLISH_CLASSICAL) {
            builder.addSuffixRule("trix", 4, "trices");
            builder.addSuffixRule("eau", 0, "x");
            builder.addSuffixRule("ieu", 0, "x");
            builder.addSuffixRule(
                    "nx",
                    CompiledInflector.and(
                            CompiledInflector.suffixStartAtLeast(3), CompiledInflector.previousCharIn("iay")),
                    2,
                    "nges");
            builder.addCategoryRule(CATEGORY_EN_INA, 2, "ina");
            builder.addCategoryRule(CATEGORY_A_ATA, 1, "ata");
            builder.addCategoryRule(CATEGORY_IS_IDES, 2, "ides");
            builder.addIdentityCategory(CATEGORY_US_US);
            builder.addCategoryRule(CATEGORY_O_I, 1, "i");
            builder.addCategoryRule(CATEGORY_NONE_I, 0, "i");
            builder.addCategoryRule(CATEGORY_NONE_IM, 0, "im");
            builder.addCategoryRule(CATEGORY_EX_EXES, 2, "ices");
            builder.addCategoryRule(CATEGORY_IX_IXES, 2, "ices");
        }

        builder.addCategoryRule(CATEGORY_US_I, 2, "i");

        builder.addSuffixRule("ch", 0, "es");
        builder.addSuffixRule("sh", 0, "es");
        builder.addSuffixRule("z", 0, "es");
        builder.addSuffixRule("x", 0, "es");
        builder.addCategoryRule(CATEGORY_S_ES, 0, "es");
        builder.addCategoryRule(CATEGORY_IS_IDES, 0, "es");
        builder.addCategoryRule(CATEGORY_US_US, 0, "es");
        builder.addSuffixRule("us", 0, "es");
        builder.addCategoryRule(CATEGORY_A_ATA, 0, "s");
        builder.addSuffixRule("ss", 0, "es");

        builder.addSuffixRule("lf", CompiledInflector.previousCharIn("aeo"), 1, "ves");
        builder.addSuffixRule("eaf", CompiledInflector.previousCharNot('d'), 1, "ves");
        builder.addSuffixRule("arf", 1, "ves");
        builder.addSuffixRule("ife", CompiledInflector.previousCharIn("nlw"), 2, "ves");

        builder.addSuffixRule("y", CompiledInflector.previousCharIn("aeiou"), 0, "s");
        builder.addSuffixRule("y", 1, "ies");

        builder.addCategoryRule(CATEGORY_O_I, 1, "os");
        builder.addCategoryRule(CATEGORY_O_OS, 1, "os");
        builder.addSuffixRule("o", CompiledInflector.previousCharIn("aeiou"), 0, "s");
        builder.addSuffixRule("o", 0, "es");

        builder.addSuffixRule("ulum", 2, "a");
        builder.addCategoryRule(CATEGORY_A_ATA, 0, "es");
        builder.addSuffixRule("s", 0, "es");
        builder.addSuffixRule("", (lowerWord, suffixStart) -> lowerWord.isEmpty(), 0, "");
        builder.addSuffixRule("", 0, "s");

        return builder.build();
    }

    private static void addIrregular(CompiledInflector.Builder builder, String singular, String plural) {
        if (singular.charAt(0) == plural.charAt(0)) {
            builder.addPreservedInitialRule(singular, plural);
        } else {
            builder.addWholeWordRule(singular, plural);
        }
    }
}
