package org.atteo.evo.inflector;

class CategoryRule implements Rule {
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
