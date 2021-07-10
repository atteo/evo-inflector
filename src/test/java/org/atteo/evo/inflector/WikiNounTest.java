package org.atteo.evo.inflector;


import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

public class WikiNounTest {
    @ParameterizedTest
    @CsvSource(value = {
        "premix,|~,premixes",
        "scissors,|scissors,scissors",
        "also-ran,|head=[[also]]-[[ran]],also-rans",
    })
    public void shouldParseWikiEnNoun(String singular, String ennoun, String plural) {
        // given
        WikiNoun noun = new WikiNoun(singular, ennoun);

        // then
        assertThat(noun.plurals()).contains(plural);
    }
}
