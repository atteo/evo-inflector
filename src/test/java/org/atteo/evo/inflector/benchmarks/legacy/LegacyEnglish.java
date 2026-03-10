package org.atteo.evo.inflector.benchmarks.legacy;

import static java.lang.Character.toLowerCase;
import static java.lang.Character.toUpperCase;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

abstract class LegacyTwoFormInflector {
    private final List<LegacyRule> rules = new ArrayList<>();

    protected String getPlural(String word) {
        for (LegacyRule rule : rules) {
            var result = rule.getPlural(word);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    protected void uncountable(String[] list) {
        rules.add(new LegacyCategoryRule(list, "", ""));
    }

    protected void irregular(String singular, String plural) {
        if (singular.charAt(0) == plural.charAt(0)) {
            rules.add(new LegacyRegExpRule(
                    Pattern.compile("(?i)(" + singular.charAt(0) + ")" + singular.substring(1) + "$"),
                    "$1" + plural.substring(1)));
        } else {
            rules.add(new LegacyRegExpRule(
                    Pattern.compile(toUpperCase(singular.charAt(0)) + "(?i)" + singular.substring(1) + "$"),
                    toUpperCase(plural.charAt(0)) + plural.substring(1)));
            rules.add(new LegacyRegExpRule(
                    Pattern.compile(toLowerCase(singular.charAt(0)) + "(?i)" + singular.substring(1) + "$"),
                    toLowerCase(plural.charAt(0)) + plural.substring(1)));
        }
    }

    protected void irregular(String[][] list) {
        for (String[] pair : list) {
            irregular(pair[0], pair[1]);
        }
    }

    protected void rule(String singular, String plural) {
        rules.add(new LegacyRegExpRule(Pattern.compile(singular, Pattern.CASE_INSENSITIVE), plural));
    }

    protected void rule(String[][] list) {
        for (String[] pair : list) {
            rules.add(new LegacyRegExpRule(Pattern.compile(pair[0], Pattern.CASE_INSENSITIVE), pair[1]));
        }
    }

    protected void categoryRule(String[] list, String singular, String plural) {
        rules.add(new LegacyCategoryRule(list, singular, plural));
    }
}

interface LegacyRule {
    String getPlural(String singular);
}

final class LegacyCategoryRule implements LegacyRule {
    private final String[] list;
    private final String singular;
    private final String plural;

    LegacyCategoryRule(String[] list, String singular, String plural) {
        this.list = list;
        this.singular = singular;
        this.plural = plural;
    }

    @Override
    public String getPlural(String word) {
        var lowerWord = word.toLowerCase();
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

final class LegacyRegExpRule implements LegacyRule {
    private final Pattern singular;
    private final String plural;

    LegacyRegExpRule(Pattern singular, String plural) {
        this.singular = singular;
        this.plural = plural;
    }

    @Override
    public String getPlural(String word) {
        var buffer = new StringBuffer();
        var matcher = singular.matcher(word);
        if (matcher.find()) {
            matcher.appendReplacement(buffer, plural);
            matcher.appendTail(buffer);
            return buffer.toString();
        }
        return null;
    }
}

public final class LegacyEnglish extends LegacyTwoFormInflector {
    public enum Mode {
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

    private static final String[] CATEGORY_O_I = {
        "solo", "soprano", "basso", "alto", "contralto", "tempo", "piano", "virtuoso",
    };

    private static final String[] CATEGORY_EN_INA = {"stamen", "foramen", "lumen"};

    private static final String[] CATEGORY_A_ATA = {
        "anathema", "enema", "oedema", "bema", "enigma", "sarcoma",
        "carcinoma", "gumma", "schema", "charisma", "lemma", "soma",
        "diploma", "lymphoma", "stigma", "dogma", "magma", "stoma",
        "drama", "melisma", "trauma", "edema", "miasma"
    };

    private static final String[] CATEGORY_IS_IDES = {"iris", "clitoris"};

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

    public LegacyEnglish() {
        this(Mode.ENGLISH_ANGLICIZED);
    }

    public LegacyEnglish(Mode mode) {
        uncountable(new String[] {
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

        irregular(new String[][] {
            {"child", "children"},
            {"ephemeris", "ephemerides"},
            {"mongoose", "mongoose"},
            {"mythos", "mythoi"},
            {"soliloquy", "soliloquies"},
            {"trilby", "trilbys"},
            {"genus", "genera"},
            {"quiz", "quizzes"},
        });

        if (mode == Mode.ENGLISH_ANGLICIZED) {
            irregular(new String[][] {
                {"beef", "beefs"},
                {"brother", "brothers"},
                {"cow", "cows"},
                {"genie", "genies"},
                {"money", "moneys"},
                {"octopus", "octopuses"},
                {"opus", "opuses"},
            });
        } else if (mode == Mode.ENGLISH_CLASSICAL) {
            irregular(new String[][] {
                {"beef", "beeves"},
                {"brother", "brethren"},
                {"cow", "kine"},
                {"genie", "genii"},
                {"money", "monies"},
                {"octopus", "octopodes"},
                {"opus", "opera"},
            });
        }

        categoryRule(CATEGORY_MAN_MANS, "", "s");

        rule(new String[][] {
            {"(m)an$", "$1en"},
            {"([lm])ouse$", "$1ice"},
            {"(t)ooth$", "$1eeth"},
            {"(g)oose$", "$1eese"},
            {"(f)oot$", "$1eet"},
            {"(z)oon$", "$1oa"},
            {"([csx])is$", "$1es"},
        });

        categoryRule(CATEGORY_EX_ICES, "ex", "ices");
        categoryRule(CATEGORY_IX_ICES, "ix", "ices");
        categoryRule(CATEGORY_UM_A, "um", "a");
        categoryRule(CATEGORY_ON_A, "on", "a");
        categoryRule(CATEGORY_A_AE, "a", "ae");

        if (mode == Mode.ENGLISH_CLASSICAL) {
            rule(new String[][] {
                {"trix$", "trices"},
                {"eau$", "eaux"},
                {"ieu$", "ieux"},
                {"(..[iay])nx$", "$1nges"},
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
        rule(new String[][] {{"([cs])h$", "$1hes"}, {"ss$", "sses"}});
        rule(new String[][] {
            {"([aeo]l)f$", "$1ves"},
            {"([^d]ea)f$", "$1ves"},
            {"(ar)f$", "$1ves"},
            {"([nlw]i)fe$", "$1ves"}
        });
        rule(new String[][] {
            {"([aeiou]y)$", "$1s"}, {"y$", "ies"},
        });
        categoryRule(CATEGORY_O_I, "o", "os");
        categoryRule(CATEGORY_O_OS, "o", "os");
        rule("([aeiou]o)$", "$1s");
        rule("(o)$", "$1es");
        rule("(ul)um$", "$1a");
        categoryRule(CATEGORY_A_ATA, "", "es");
        rule("(s)$", "$1es");
        rule("^$", "");
        rule("$", "s");
    }

    public String plural(String word) {
        return getPlural(word);
    }
}
