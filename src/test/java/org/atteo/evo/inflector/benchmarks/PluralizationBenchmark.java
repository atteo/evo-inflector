package org.atteo.evo.inflector.benchmarks;

import java.util.concurrent.TimeUnit;

import org.atteo.evo.inflector.English;
import org.atteo.evo.inflector.benchmarks.legacy.LegacyEnglish;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@Fork(0)
@Warmup(iterations = 3, time = 500, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 5, time = 500, timeUnit = TimeUnit.MILLISECONDS)
public class PluralizationBenchmark {
    @State(Scope.Benchmark)
    public static class EngineState {
        private final English compiled = new English();
        private final LegacyEnglish legacy = new LegacyEnglish();
    }

    @State(Scope.Thread)
    public static class DatasetState {
        private final String[] words = {
            "cat",
            "dog",
            "foot",
            "goose",
            "tooth",
            "woman",
            "series",
            "virus",
            "gastrostomy",
            "Bacterium",
            "Genus",
            "status",
            "basis",
            "iris",
            "tempo",
            "church",
            "wish",
            "ferry",
            "protozoon",
            "cherub",
            "human",
            "sugar",
            "baculum",
            "pancreas",
            "todo",
            "person",
            "quick_chateau",
            "NightWolf",
            "WorldAtlas",
            "SMS",
            "Milieu",
            "bacuLum",
            "alO",
            "luO",
            "boY",
            "Foot",
            "Goose",
            "Man",
            "Tooth",
            "index",
            "codex",
            "radix",
            "appendix",
            "solo",
            "piano",
            "octopus",
            "opus",
            "money",
            "genie",
            "beef",
            "brother",
            "cow",
            "phalanx",
            "trix",
            "eau",
            "ieu",
            "murex",
            "silex",
            "criterion",
            "phenomenon",
            "alumna",
            "vertebra",
            "persona",
            "afreet",
            "cherub",
            "seraph",
            "canvas",
            "trellis",
            "bias",
            "atlas",
            "rhinoceros",
            "deer",
            "fish",
            "species",
            "homework",
            "mews",
            "baculumulum",
            "demoness",
            "semifluid",
            "sulfimide",
            "quiz",
            "soliloquy",
            "trilby"
        };

        private int index;

        @Setup(Level.Iteration)
        public void reset() {
            index = 0;
        }

        public String next() {
            String word = words[index];
            index++;
            if (index == words.length) {
                index = 0;
            }
            return word;
        }
    }

    @State(Scope.Thread)
    public static class RepeatedState {
        private String lowercaseWord;
        private String mixedCaseWord;

        @Setup(Level.Trial)
        public void setUp() {
            lowercaseWord = "gastrostomy";
            mixedCaseWord = "WorldAtlas";
        }
    }

    @Benchmark
    public String compiledDataset(EngineState state, DatasetState dataset) {
        return state.compiled.getPlural(dataset.next());
    }

    @Benchmark
    public String legacyDataset(EngineState state, DatasetState dataset) {
        return state.legacy.plural(dataset.next());
    }

    @Benchmark
    public String compiledRepeatedLowercase(EngineState state, RepeatedState repeated) {
        return state.compiled.getPlural(repeated.lowercaseWord);
    }

    @Benchmark
    public String legacyRepeatedLowercase(EngineState state, RepeatedState repeated) {
        return state.legacy.plural(repeated.lowercaseWord);
    }

    @Benchmark
    public String compiledRepeatedMixedCase(EngineState state, RepeatedState repeated) {
        return state.compiled.getPlural(repeated.mixedCaseWord);
    }

    @Benchmark
    public String legacyRepeatedMixedCase(EngineState state, RepeatedState repeated) {
        return state.legacy.plural(repeated.mixedCaseWord);
    }
}
