package org.atteo.evo.inflector.benchmarks;

import java.util.concurrent.TimeUnit;

import org.atteo.evo.inflector.English;
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
        private final English anglicized = new English();
        private final English classical = new English(English.MODE.ENGLISH_CLASSICAL);
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
            var word = words[index];
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
    public String anglicizedDataset(EngineState state, DatasetState dataset) {
        return state.anglicized.getPlural(dataset.next());
    }

    @Benchmark
    public String classicalDataset(EngineState state, DatasetState dataset) {
        return state.classical.getPlural(dataset.next());
    }

    @Benchmark
    public String anglicizedRepeatedLowercase(EngineState state, RepeatedState repeated) {
        return state.anglicized.getPlural(repeated.lowercaseWord);
    }

    @Benchmark
    public String classicalRepeatedLowercase(EngineState state, RepeatedState repeated) {
        return state.classical.getPlural(repeated.lowercaseWord);
    }

    @Benchmark
    public String anglicizedRepeatedMixedCase(EngineState state, RepeatedState repeated) {
        return state.anglicized.getPlural(repeated.mixedCaseWord);
    }

    @Benchmark
    public String classicalRepeatedMixedCase(EngineState state, RepeatedState repeated) {
        return state.classical.getPlural(repeated.mixedCaseWord);
    }
}
