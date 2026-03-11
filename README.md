[![SWUbanner](https://raw.githubusercontent.com/vshymanskyy/StandWithUkraine/main/banner2-direct.svg)](https://github.com/vshymanskyy/StandWithUkraine/blob/main/docs/README.md)

# About

Evo Inflector implements English pluralization algorithm based on ["Damian Conway's"](https://en.wikipedia.org/wiki/Damian_Conway) paper ["An Algorithmic Approach to English Pluralization"](http://www.csse.monash.edu.au/~damian/papers/HTML/Plurals.html).

There are more or less half a million downloads of Evo Inflector from Maven Central each month.
It is used by many high profile projects like [Spring and JetBrains](https://mvnrepository.com/artifact/org.atteo/evo-inflector/usages) and [tons of smaller projects](https://github.com/atteo/evo-inflector/network/dependents).

# Usage

The usage is pretty simple:

```java
English.plural("word") == "words"
```

Additionally you can use provide a required count to select singular or plural form automatically:

```java
English.plural("foot", 1)) == "foot"
English.plural("foot", 2)) == "feet"
```

# Features

The algorithm tries to preserve the capitalization of the original word, for instance:

```java
English.plural("NightWolf") == "NightWolves"
```

# Limitations

* The algorithm cannot reliably detect uncountable words. It will pluralize them anyway.
* There are words which have the same singular form and multiple plural forms, ex:
die (plural dies) - The cubical part of a pedestal; a plinth.
die (plural dice) - An isohedral polyhedron, usually a cube

# Tests

As part of the unit tests the results of the algorithm are compared with data from Wiktionary.
Wiktionary is not a perfect source of data, especially for more obscure words, but it is the best we have.

There are (2026-03-10) 345105 single word nouns in the English Wiktionary of which:
- 68.427% (236145) are countable nouns,
- 28.260384% (97528) are uncountable nouns,
- for 2.9802525% (10285) nouns plural is unknown,
- for 0.33236262% (1147) nouns plural is not attested.

Evo Inflector returns correct answer for:
- 94.61559% (223430) of all countable nouns, see `target/reports/incorrect-countable.md`,
- but only for 8.041793% (7843) of uncountable nouns.

In overall it returns correct answer for 67.01526% (231273) of all nouns.

# Changes

## 2.0

- rewrite internals around a compiled suffix-matching engine instead of repeated regex and rule-list scans
- remove the old regexp-based engine and benchmark-only legacy copy
- require JDK 17 for the main build
- simplify and modernize rule handling while keeping the public `English.plural(...)` API
- add JMH benchmarks for anglicized and classical compiled modes
- improve throughput substantially; the compiled engine delivered about 18x faster mixed-dataset throughput, about 54x faster repeated lowercase lookups, and about 27x faster repeated mixed-case lookups versus the old regexp engine during the migration benchmarks

## 1.3

- fix ulum -> ula rule
- return empty string for empty string input
- improve preservation of letter capitalization
- make tests up-to-date with change in Wiktionary

## 1.2.2

- fix pluralization of todo

## 1.2

- compile with Java 1.6 for better compatibility
- -s -> -ses, for instance pancreas -> pancrases
- -ulum -> -ula, for instance baculum -> bacula
- some minor optimizations
- better testing with Wiktionary dump

## 1.1

- fix for -us ending words, like virus

## 1.0.1

- add inflection with count

## 1.0 Initial revision

# License

Evo Inflector is available under [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0).

# Download

You can download the library from [here](http://search.maven.org/remotecontent?filepath=org/atteo/evo-inflector/1.3/evo-inflector-1.3.jar) or use the following Maven dependency:

```xml
<dependency>
    <groupId>org.atteo</groupId>
    <artifactId>evo-inflector</artifactId>
    <version>1.3</version>
</dependency>
```

or the Gradle dependency:

```groovy
compile group: 'org.atteo', name: 'evo-inflector', version: '1.2.2'
```
