package org.atteo.evo.inflector;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.opentest4j.TestAbortedException;

final class WiktionaryCorpus {
    private static final String CACHE_HEADER = "# evo-inflector wiktionary noun cache v1";

    private static final Path RAW_DUMP = Path.of("src/test/resources/enwiktionary-latest-pages-articles.xml.bz2");
    private static final Path COMPACT_CACHE = Path.of("src/test/resources/enwiktionary-nouns.tsv.gz");
    private static final Path TEMP_CACHE = Path.of("src/test/resources/enwiktionary-nouns.tsv.gz.tmp");

    void forEach(Consumer<List<WikiNoun>> consumer) throws IOException {
        var cachePath = ensureCompactCache();

        try {
            readCache(cachePath, consumer);
        } catch (IOException e) {
            if (!Files.exists(RAW_DUMP)) {
                throw e;
            }

            Files.deleteIfExists(COMPACT_CACHE);
            Files.deleteIfExists(TEMP_CACHE);
            createCompactCache();
            readCache(COMPACT_CACHE, consumer);
        }
    }

    private Path ensureCompactCache() throws IOException {
        var rawExists = Files.exists(RAW_DUMP);
        var cacheExists = Files.exists(COMPACT_CACHE);

        if (!rawExists && !cacheExists) {
            throw new TestAbortedException("Wiktionary dump and compact cache are missing");
        }

        if (rawExists && shouldRegenerateCache()) {
            createCompactCache();
            return COMPACT_CACHE;
        }

        if (cacheExists) {
            return COMPACT_CACHE;
        }

        throw new TestAbortedException("Wiktionary compact cache is missing");
    }

    private boolean shouldRegenerateCache() throws IOException {
        Files.deleteIfExists(TEMP_CACHE);

        if (!Files.exists(COMPACT_CACHE)) {
            return true;
        }

        if (Files.getLastModifiedTime(RAW_DUMP).compareTo(Files.getLastModifiedTime(COMPACT_CACHE)) > 0) {
            return true;
        }

        try (var reader = newReader(COMPACT_CACHE)) {
            return !CACHE_HEADER.equals(reader.readLine());
        } catch (IOException e) {
            return true;
        }
    }

    private void createCompactCache() throws IOException {
        Files.deleteIfExists(TEMP_CACHE);

        try (var writer = new BufferedWriter(
                new OutputStreamWriter(new GZIPOutputStream(Files.newOutputStream(TEMP_CACHE)), UTF_8))) {
            writer.write(CACHE_HEADER);
            writer.newLine();

            try {
                new WikiParser().parse(RAW_DUMP, page -> writePage(writer, page));
            } catch (UncheckedIOException e) {
                throw e.getCause();
            }
        } catch (IOException e) {
            Files.deleteIfExists(TEMP_CACHE);
            throw e;
        }

        Files.move(TEMP_CACHE, COMPACT_CACHE, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
    }

    private void writePage(BufferedWriter writer, Page page) {
        if (page.getTitle().contains(" ") || page.getTitle().contains(":")) {
            return;
        }

        List<WikiNoun> nouns = WikiNoun.find(page);
        if (nouns.isEmpty()) {
            return;
        }

        try {
            writer.write(page.getTitle());
            for (WikiNoun noun : nouns) {
                writer.write('\t');
                writer.write(noun.ennoun());
            }
            writer.newLine();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private List<WikiNoun> parseLine(String line) {
        var parts = line.split("\\t", -1);
        var nouns = new ArrayList<WikiNoun>(Math.max(1, parts.length - 1));
        for (var i = 1; i < parts.length; i++) {
            nouns.add(new WikiNoun(parts[0], parts[i]));
        }
        return nouns;
    }

    private BufferedReader newReader(Path path) throws IOException {
        try {
            return new BufferedReader(new InputStreamReader(new GZIPInputStream(Files.newInputStream(path)), UTF_8));
        } catch (EOFException e) {
            throw new IOException("Corrupted Wiktionary compact cache: " + path, e);
        }
    }

    private void readCache(Path path, Consumer<List<WikiNoun>> consumer) throws IOException {
        try (var reader = newReader(path)) {
            var header = reader.readLine();
            if (!CACHE_HEADER.equals(header)) {
                throw new IOException("Unsupported Wiktionary compact cache format: " + path);
            }

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isEmpty()) {
                    continue;
                }
                consumer.accept(parseLine(line));
            }
        }
    }
}
