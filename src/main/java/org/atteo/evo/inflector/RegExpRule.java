package org.atteo.evo.inflector;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class RegExpRule implements Rule {
    private final Pattern singular;
    private final String plural;

    RegExpRule(Pattern singular, String plural) {
        this.singular = singular;
        this.plural = plural;
    }

    RegExpRule(String singular, String plural) {
        this.singular = Pattern.compile(singular);
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
