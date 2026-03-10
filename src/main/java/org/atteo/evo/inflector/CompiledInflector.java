package org.atteo.evo.inflector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

final class CompiledInflector {
    @FunctionalInterface
    interface WordCondition {
        boolean matches(String lowerWord, int suffixStart);
    }

    sealed interface Transform permits SuffixTransform, WholeWordTransform {
        String apply(String word);
    }

    record SuffixTransform(int removeLength, String append) implements Transform {
        @Override
        public String apply(String word) {
            int prefixLength = word.length() - removeLength;
            StringBuilder builder = new StringBuilder(prefixLength + append.length());
            builder.append(word, 0, prefixLength);
            builder.append(adaptCase(append, word.substring(prefixLength)));
            return builder.toString();
        }

        private static String adaptCase(String value, String pattern) {
            if (value.isEmpty() || pattern.isEmpty()) {
                return value;
            }

            char[] chars = value.toCharArray();
            int max = Math.min(chars.length, pattern.length());
            for (int i = 0; i < max; i++) {
                if (Character.isUpperCase(pattern.charAt(i))) {
                    chars[i] = Character.toUpperCase(chars[i]);
                }
            }
            return new String(chars);
        }
    }

    record WholeWordTransform(String replacement) implements Transform {
        @Override
        public String apply(String word) {
            if (word.isEmpty() || replacement.isEmpty()) {
                return replacement;
            }
            if (Character.isUpperCase(word.charAt(0))) {
                return Character.toUpperCase(replacement.charAt(0)) + replacement.substring(1);
            }
            return replacement;
        }
    }

    private record CompiledRule(int priority, int suffixLength, WordCondition condition, Transform transform) {
        private boolean matches(String lowerWord) {
            return condition.matches(lowerWord, lowerWord.length() - suffixLength);
        }

        private String apply(String word) {
            return transform.apply(word);
        }
    }

    private static final WordCondition ALWAYS = (lowerWord, suffixStart) -> true;
    private static final WordCondition EXACT_WORD = (lowerWord, suffixStart) -> suffixStart == 0;

    private final TrieNode root;

    private CompiledInflector(TrieNode root) {
        this.root = root;
    }

    public String pluralize(String word) {
        String lowerWord = word.toLowerCase(Locale.ROOT);
        CompiledRule best = match(root.rules, lowerWord, null);

        TrieNode node = root;
        for (int i = lowerWord.length() - 1; i >= 0; i--) {
            node = node.children.get(lowerWord.charAt(i));
            if (node == null) {
                break;
            }
            best = match(node.rules, lowerWord, best);
        }

        if (best == null) {
            return word;
        }
        return best.apply(word);
    }

    private static CompiledRule match(List<CompiledRule> rules, String lowerWord, CompiledRule currentBest) {
        CompiledRule best = currentBest;
        for (CompiledRule rule : rules) {
            if (best != null && best.priority() < rule.priority()) {
                continue;
            }
            if (rule.matches(lowerWord)) {
                best = rule;
            }
        }
        return best;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static WordCondition previousCharIn(String chars) {
        return (lowerWord, suffixStart) -> suffixStart > 0 && chars.indexOf(lowerWord.charAt(suffixStart - 1)) >= 0;
    }

    public static WordCondition previousCharNot(char excluded) {
        return (lowerWord, suffixStart) -> suffixStart > 0 && lowerWord.charAt(suffixStart - 1) != excluded;
    }

    public static WordCondition suffixStartAtLeast(int minimum) {
        return (lowerWord, suffixStart) -> suffixStart >= minimum;
    }

    public static WordCondition and(WordCondition... conditions) {
        return (lowerWord, suffixStart) -> {
            for (WordCondition condition : conditions) {
                if (!condition.matches(lowerWord, suffixStart)) {
                    return false;
                }
            }
            return true;
        };
    }

    public static final class Builder {
        private final TrieNode root = new TrieNode();
        private int priority;

        private Builder() {
        }

        public Builder addSuffixRule(String suffix, int removeLength, String append) {
            return addSuffixRule(suffix, ALWAYS, new SuffixTransform(removeLength, append));
        }

        public Builder addSuffixRule(String suffix, WordCondition condition, int removeLength, String append) {
            return addSuffixRule(suffix, condition, new SuffixTransform(removeLength, append));
        }

        public Builder addSuffixRule(String suffix, WordCondition condition, Transform transform) {
            TrieNode node = root;
            for (int i = suffix.length() - 1; i >= 0; i--) {
                node = node.children.computeIfAbsent(suffix.charAt(i), ignored -> new TrieNode());
            }
            node.rules.add(new CompiledRule(priority++, suffix.length(), condition, transform));
            return this;
        }

        public Builder addWholeWordRule(String singular, String plural) {
            return addSuffixRule(singular, EXACT_WORD, new WholeWordTransform(plural));
        }

        public Builder addPreservedInitialRule(String singular, String plural) {
            return addSuffixRule(singular, singular.length() - 1, plural.substring(1));
        }

        public Builder addCategoryRule(String[] suffixes, int removeLength, String append) {
            for (String suffix : suffixes) {
                addSuffixRule(suffix, removeLength, append);
            }
            return this;
        }

        public Builder addIdentityCategory(String[] suffixes) {
            return addCategoryRule(suffixes, 0, "");
        }

        public CompiledInflector build() {
            sortRules(root);
            return new CompiledInflector(root);
        }

        private static void sortRules(TrieNode node) {
            node.rules.sort((left, right) -> Integer.compare(left.priority(), right.priority()));
            for (TrieNode child : node.children.values()) {
                sortRules(child);
            }
        }
    }

    private static final class TrieNode {
        private final Map<Character, TrieNode> children = new HashMap<Character, TrieNode>();
        private final List<CompiledRule> rules = new ArrayList<CompiledRule>();
    }
}
