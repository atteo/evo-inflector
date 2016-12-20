[![Build Status](https://travis-ci.org/atteo/evo-inflector.svg)](https://travis-ci.org/atteo/evo-inflector)
[![Coverage Status](https://img.shields.io/coveralls/atteo/evo-inflector.svg)](https://coveralls.io/r/atteo/evo-inflector)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.atteo/evo-inflector/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.atteo/evo-inflector)

About
=====

Evo Inflector implements English pluralization algorithm based on Damian Conway's paper ["An Algorithmic Approach to English Pluralization"](http://www.csse.monash.edu.au/~damian/papers/HTML/Plurals.html).

The tests performed (May 2014) based on data from [Wiktionary](http://dumps.wikimedia.org/enwiktionary/latest/) show that:
- for entire set of 163518 words from Wiktionary, Evo Inflector returns correct answer for 68.4% of them,
- for 979 words marked as basic words almost all answers are correct, the sole exception being the word ['worse'](https://en.wiktionary.org/wiki/worse) which when used as a noun does not have a plural form,
- for 24.9% of all words Evo Inflector returns some form, but the word is marked as uncountable in Wiktionary,
- for 4.1% of all words Wiktionary does not specify the plural form for given word so whatever Evo Inflector returns will always be wrong,
- for 2.6% Evo Inflector returns an answer which is different than the one provided in Wiktionary.

(If you are curious this test is part of the [unit tests](https://github.com/atteo/evo-inflector/blob/master/src/test/java/org/atteo/evo/inflector/EnglishInflectorTest.java).)

Changes
=======

1.2.2
- fix pluralization of todo

1.2
- compile with Java 1.6 for better compatibility
- -s -> -ses, for instance pancreas -> pancrases
- -ulum -> -ula, for instance baculum -> bacula
- some minor optimizations
- better testing with Wiktionary dump

1.1
- fix for -us ending words, like virus

1.0.1
- add inflection with count

1.0 Initial revision

Usage
=====

```java
System.out.println(English.plural("word")); // == "words"

System.out.println(English.plural("word", 1)); // == "word"
System.out.println(English.plural("word", 2)); // == "words"
```

License
=======

Evo Inflector is available under [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0).

Download
========

You can download the library from [here](http://search.maven.org/remotecontent?filepath=org/atteo/evo-inflector/1.2.2/evo-inflector-1.2.2.jar) or use the following Maven dependency:

```xml
<dependency>
    <groupId>org.atteo</groupId>
    <artifactId>evo-inflector</artifactId>
    <version>1.2.2</version>
</dependency>
```



