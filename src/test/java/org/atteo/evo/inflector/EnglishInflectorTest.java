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

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import java.io.BufferedWriter;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.atteo.evo.inflector.benchmarks.legacy.LegacyEnglish;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class EnglishInflectorTest {
    private static final Path INCORRECT_COUNTABLE_REPORT = Path.of("target/reports/incorrect-countable.md");
    private static final Path LEGACY_PARITY_REPORT = Path.of("target/reports/legacy-parity.md");

    private final English inflector = new English();

    @Test
    void wiktionaryTest() throws Exception {

        var all = new AtomicInteger();
        var countable = new AtomicInteger();
        var correctCountable = new AtomicInteger();
        var uncountable = new AtomicInteger();
        var correctUncountable = new AtomicInteger();
        var pluralNotAttested = new AtomicInteger();
        var pluralUnknown = new AtomicInteger();

        Files.createDirectories(INCORRECT_COUNTABLE_REPORT.getParent());
        try (BufferedWriter incorrectCountable = Files.newBufferedWriter(INCORRECT_COUNTABLE_REPORT, UTF_8)) {
            incorrectCountable.append("|Singular|Evo-Inflector plural|Wiktionary plurals|\n");
            incorrectCountable.append("|--------|--------------------|------------------|\n");

            new WiktionaryCorpus().forEach(wikiNouns -> {
                all.getAndIncrement();

                var calculatedPlural = inflector.getPlural(wikiNouns.get(0).singular());

                Optional<WikiNoun> correctNoun = wikiNouns.stream()
                        .filter(noun -> noun.plurals().contains(calculatedPlural))
                        .findFirst();

                var correct = correctNoun.isPresent();

                var wikiNoun = correctNoun.orElse(wikiNouns.get(0));

                if (wikiNoun.isUncountable()) {
                    uncountable.getAndIncrement();
                    if (correct) {
                        correctUncountable.getAndIncrement();
                    }
                    return;
                }
                if (wikiNoun.isPluralNotAttested()) {
                    pluralNotAttested.getAndIncrement();
                    return;
                }

                if (wikiNoun.isPluralUnknown()) {
                    pluralUnknown.getAndIncrement();
                    return;
                }

                countable.getAndIncrement();

                if (correct) {
                    correctCountable.getAndIncrement();
                    return;
                }

                try {
                    var wiktionaryPlurals = wikiNouns.stream()
                            .flatMap(noun -> noun.plurals().stream())
                            .collect(Collectors.joining(","));
                    String uriEncodedSingular = URLEncoder.encode(wikiNoun.singular(), UTF_8);

                    incorrectCountable.append("|" + wikiNoun.singular() + " | " + calculatedPlural + " | ["
                            + wiktionaryPlurals + "](https://en.wiktionary.org/wiki/" + uriEncodedSingular + ") |\n");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        printSummary(countable, correctCountable, uncountable, correctUncountable, pluralNotAttested, pluralUnknown);
    }

    private void printSummary(
            AtomicInteger countable,
            AtomicInteger correctCountable,
            AtomicInteger uncountable,
            AtomicInteger correctUncountable,
            AtomicInteger pluralNotAttested,
            AtomicInteger pluralUnknown) {
        var all = countable.get() + uncountable.get() + pluralNotAttested.get() + pluralUnknown.get();

        System.out.println("");
        System.out.println("There are (" + LocalDate.now().toString() + ") " + all
                + " single word nouns in the English Wiktionary of which:");
        System.out.println("- " + percent(countable.get(), all) + " are countable nouns,");
        System.out.println("- " + percent(uncountable.get(), all) + " are uncountable nouns,");
        System.out.println("- for " + percent(pluralUnknown.get(), all) + " nouns plural is unknown,");
        System.out.println("- for " + percent(pluralNotAttested.get(), all) + " nouns plural is not attested.");
        System.out.println("");
        System.out.println("Evo Inflector returns correct answer for: ");
        System.out.println("- " + percent(correctCountable.get(), countable.get())
                + " of all countable nouns, see [this report](target/reports/incorrect-countable.md),");
        System.out.println(
                "- but only for " + percent(correctUncountable.get(), uncountable.get()) + " of uncountable nouns.");
        System.out.println("In overall it returns correct answer for "
                + percent(correctCountable.get() + correctUncountable.get(), all) + " of all nouns");
        System.out.println("");
    }

    private String percent(int count, int all) {
        var percent = count * 100 / (float) all;
        return percent + "% (" + count + ")";
    }

    @Test
    void exampleWordList() {
        check(new String[][] {
            {"alga", "algae"},
            {"nova", "novas"},
            {"dogma", "dogmas"},
            {"Woman", "Women"},
            {"church", "churches"},
            {"quick_chateau", "quick_chateaus"},
            {"codex", "codices"},
            {"index", "indexes"},
            {"basis", "bases"},
            {"iris", "irises"},
            {"phalanx", "phalanxes"},
            {"tempo", "tempos"},
            {"foot", "feet"},
            {"series", "series"},
            {"wish", "wishes"},
            {"Bacterium", "Bacteria"},
            {"medium", "mediums"},
            {"Genus", "Genera"},
            {"stimulus", "stimuli"},
            {"opus", "opuses"},
            {"status", "statuses"},
            {"Box", "Boxes"},
            {"ferry", "ferries"},
            {"protozoon", "protozoa"},
            {"cherub", "cherubs"},
            {"human", "humans"},
            {"sugar", "sugar"},
            {"virus", "viruses"},
            {"gastrostomy", "gastrostomies"},
            {"baculum", "bacula"},
            {"pancreas", "pancreases"},
            {"todo", "todos"},
            {"person", "persons"},
            {"baculumulum", "baculumula"}, // https://github.com/atteo/evo-inflector/pull/18
            {"", ""},
        });
    }

    @Test
    void shouldPreserveCapitalLetters() {
        check(new String[][] {
            {"Milieu", "Milieus"},
            {"NightWolf", "NightWolves"},
            {"WorldAtlas", "WorldAtlases"},
            {"SMS", "SMSes"},
            {"bacuLum", "bacuLa"},
            {"alO", "alOes"},
            {"luO", "luOs"},
            {"boY", "boYs"},
            {"Foot", "Feet"},
            {"Goose", "Geese"},
            {"Man", "Men"},
            {"Tooth", "Teeth"},
        });
    }

    @Test
    void withCount() {
        assertThat(inflector.getPlural("cat", 1)).isEqualTo("cat");
        assertThat(inflector.getPlural("cat", 2)).isEqualTo("cats");

        assertThat(inflector.getPlural("demoness", 1)).isEqualTo("demoness");
        assertThat(inflector.getPlural("demoness", 2)).isEqualTo("demonesses");
    }

    @Test
    void staticMethods() {
        assertThat(English.plural("sulfimide")).isEqualTo("sulfimides");
        assertThat(English.plural("semifluid", 2)).isEqualTo("semifluids");
    }

    @Test
    @Disabled("Compiled engine intentionally differs from legacy regex parity because of improved case handling")
    void shouldMatchLegacyImplementationOnWiktionaryCorpus() throws Exception {
        Files.createDirectories(LEGACY_PARITY_REPORT.getParent());

        try (BufferedWriter report = Files.newBufferedWriter(LEGACY_PARITY_REPORT, UTF_8)) {
            report.append("|Mode|Singular|Compiled|Legacy|\n");
            report.append("|----|--------|--------|------|\n");

            var mismatches = new AtomicInteger();
            var checked = new AtomicInteger();

            compareWithLegacy(
                    new English(English.MODE.ENGLISH_ANGLICIZED),
                    new LegacyEnglish(LegacyEnglish.Mode.ENGLISH_ANGLICIZED),
                    "anglicized",
                    checked,
                    mismatches,
                    report);

            compareWithLegacy(
                    new English(English.MODE.ENGLISH_CLASSICAL),
                    new LegacyEnglish(LegacyEnglish.Mode.ENGLISH_CLASSICAL),
                    "classical",
                    checked,
                    mismatches,
                    report);

            assertThat(checked.get()).isPositive();
            if (mismatches.get() > 0) {
                fail("Found %s compiled-vs-legacy mismatches on Wiktionary corpus; see %s"
                        .formatted(mismatches.get(), LEGACY_PARITY_REPORT));
            }
        }
    }

    private void check(String[][] list) {
        for (String[] pair : list) {
            check(pair[0], pair[1]);
        }
    }

    private void check(String singular, String plural) {
        assertThat(inflector.getPlural(singular)).isEqualTo(plural);
    }

    private void compareWithLegacy(
            English compiled,
            LegacyEnglish legacy,
            String mode,
            AtomicInteger checked,
            AtomicInteger mismatches,
            BufferedWriter report)
            throws IOException {
        new WiktionaryCorpus().forEach(wikiNouns -> {
            var singular = wikiNouns.get(0).singular();
            var compiledPlural = compiled.getPlural(singular);
            var legacyPlural = legacy.plural(singular);

            checked.incrementAndGet();
            if (compiledPlural.equals(legacyPlural)) {
                return;
            }

            mismatches.incrementAndGet();
            try {
                report.append('|')
                        .append(mode)
                        .append('|')
                        .append(singular)
                        .append('|')
                        .append(compiledPlural)
                        .append('|')
                        .append(legacyPlural)
                        .append("|\n");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
