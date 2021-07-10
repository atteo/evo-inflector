package org.atteo.evo.inflector;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.Consumer;

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.opentest4j.TestAbortedException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlFactory;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

public class WikiParser {
    public void parse(Consumer<Page> consumer) throws IOException {
        InputStream compressedStream = EnglishInflectorTest.class.getResourceAsStream(
            "/enwiktionary-latest-pages-articles.xml.bz2");
        if (compressedStream == null) {
            System.err.println("\nFull test requires wiktionary dump which was not found\n" +
                "To run rull test do the following:\n" +
                "cd src/test/resources\n" +
                "wget http://download.wikimedia.org/enwiktionary/latest/" +
                "enwiktionary-latest-pages-articles.xml.bz2\n");
            throw new TestAbortedException("Wiktionary data is missing");
        }
        BZip2CompressorInputStream stream = new BZip2CompressorInputStream(compressedStream);

        XmlFactory xmlFactory = new XmlFactory();
        JsonParser parser = xmlFactory.createParser(stream);
        XmlMapper xmlMapper = new XmlMapper(xmlFactory);
        xmlMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        parser.setCodec(xmlMapper);

        if (parser.nextToken() != JsonToken.START_OBJECT) {
            throw new RuntimeException("START_OBJECT is required at the beginning");
        }

        while (true) {
            JsonToken token = parser.nextToken();
            if (token == null) {
                break;
            }

            if (token == JsonToken.START_OBJECT) {
                if ("siteinfo".equals(parser.currentName())) {
                    parser.skipChildren();
                } else if ("page".equals(parser.currentName())) {

                    Page page = parser.readValueAs(Page.class);

                    consumer.accept(page);
                }
            }
        }
        parser.close();
    }
}
