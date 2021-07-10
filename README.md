[![Build Status](https://travis-ci.org/atteo/evo-inflector.svg)](https://travis-ci.org/atteo/evo-inflector)
[![Coverage Status](https://img.shields.io/coveralls/atteo/evo-inflector.svg)](https://coveralls.io/r/atteo/evo-inflector)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.atteo/evo-inflector/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.atteo/evo-inflector)

About
=====

Evo Inflector implements English pluralization algorithm based on ["Damian Conway's"](https://en.wikipedia.org/wiki/Damian_Conway) paper ["An Algorithmic Approach to English Pluralization"](http://www.csse.monash.edu.au/~damian/papers/HTML/Plurals.html).

Usage
=====

The usage is pretty simple:

```java
English.plural("word") == "words"
```

Additionaly you can use provide a required count to select singular or plural form automatically:

```java
English.plural("foot", 1)) == "foot"
English.plural("foot", 2)) == "feet"
```


Features
========
The algorithm tries to preserve the capitalization of the original word, for instance:

```java
English.plural("NightWolf") == "NightWolves"
```

Limitations:
============

* The algorithm cannot reliably detect uncountable words. It will pluralize them anyway.
* There are words which have the same singular form and multiple plural forms, ex:
die (plural dies) - The cubical part of a pedestal; a plinth.
die (plural dice) - An isohedral polyhedron, usually a cube

Tests
=====

As part of the unit tests the results of the algorithm are compared with data from Wiktionary.

There are (2021-07-10) 276574 single word english nouns in the English Wiktionary of which:
- 69.26971% (191582) are countable nouns,
- 27.56839% (76247) are uncountable nouns,
- for 2.8863885% (7983) nouns plural is unknown,
- for 0.27551398% (762) nouns plural is not attested.

Evo Inflector returns correct answer for:
- 96.24286% (184384) of all countable nouns, see [this report](reports/incorrect-countable.md),
- but only for 8.56296% (6529) of uncountable nouns.

In overall it returns correct answer for 69.02782% (190913) of all nouns.

Changes
=======

1.3
- fix ulum -> ula rule
- return empty string for empty string input
- improve preservation of letter capitalization
- make tests up-to-date with change in Wiktionary

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

License
=======

Evo Inflector is available under [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0).

Download
========

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



