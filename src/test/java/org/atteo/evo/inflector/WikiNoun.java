package org.atteo.evo.inflector;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WikiNoun {
    private static final Pattern enNounPattern = Pattern.compile("\\{\\{en-noun((\\|[\\-\\+~!?a-z\\[\\] =]+)*)\\}\\}");
    private final String singular;
    private final List<String> plurals = new ArrayList<>();
    private final String ennoun;
    private NounType nounType;

    public WikiNoun(String word, String ennoun) {
        this.singular = word;
        this.ennoun = ennoun;
        calculatePlurals(singular, ennoun);
    }

    private void calculatePlurals(String singular, String ennoun) {
        String[] split = ennoun.split("\\|");

        for (int i = 1; i < split.length; i++) {
            if (split[i].startsWith("head=")) {
                continue;
            }

            Noun noun = interpretInflection(singular, split[i]);

            if (nounType == null) {
                // first entry is the most common
                nounType = noun.type;
            }
            plurals.add(noun.plural);
        }

        if (plurals.isEmpty()) {
            plurals.add(defaultPlural(singular));
        }
    }

    static class Noun {
        NounType type;
        String plural;

        private Noun(NounType type, String plural) {
            this.type = type;
            this.plural = plural;
        }

        public static Noun countable(String plural) {
            return new Noun(NounType.COUNTABLE, plural);
        }

        public static Noun uncountable(String plural) {
            return new Noun(NounType.UNCOUNTABLE, plural);
        }

        public static Noun pluralNotAttested() {
            return new Noun(NounType.PLURAL_NOT_ATTESTED, "");
        }

        public static Noun unknownPlural() {
            return new Noun(NounType.UNKNOWN_PLURAL, "");
        }
    }

    private Noun interpretInflection(String singular, String inflection) {
        if ("-".equals(inflection)) {
            return Noun.uncountable(singular);
        }

        if ("~".equals(inflection)) {
            return Noun.countable(defaultPlural(singular));
        }

        if ("+".equals(inflection)) {
            return Noun.countable(defaultPlural(singular));
        }

        if ("!".equals(inflection)) {
            return Noun.pluralNotAttested();
        }

        // unknown or uncertain plural
        if ("?".equals(inflection)) {
            return Noun.unknownPlural();
        }

        if ("s".equals(inflection)) {
            return Noun.countable(singular + "s");
        }
        if ("es".equals(inflection)) {
            return Noun.countable(singular + "es");
        }

        return Noun.countable(inflection);
    }

    public String singular() {
        return singular;
    }

    public List<String> plurals() {
        return plurals;
    }

    public boolean isCountable() {
        return nounType == NounType.COUNTABLE;
    }

    public boolean isUncountable() {
        return nounType == NounType.UNCOUNTABLE;
    }

    public boolean isPluralUnknown() {
        return nounType == NounType.UNKNOWN_PLURAL;
    }

    public boolean isPluralNotAttested() {
        return nounType == NounType.PLURAL_NOT_ATTESTED;
    }

    public String ennoun() {
        return ennoun;
    }

    private static String defaultPlural(String singular) {
        if (singular.matches(".*(s|x|z|sh|ch)$")) {
            return singular + "es";
        } else {
            String plural = new RegExpRule("([aeiou])y$", "$1ys").getPlural(singular);
            if (plural != null) {
                return plural;
            }
            plural = new RegExpRule("y$", "ies").getPlural(singular);

            if (plural != null) {
                return plural;
            }
            return new RegExpRule("$", "s").getPlural(singular);
        }
    }

    public static List<WikiNoun> find(Page page) {
        Matcher matcher = enNounPattern.matcher(page.getRevision().getText());
        List<WikiNoun> nouns = new ArrayList<>();

        while (matcher.find()) {
            nouns.add(new WikiNoun(page.getTitle(), matcher.group(1)));
        }

        return nouns;
    }
}
