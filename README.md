About
=====

Evo Inflector implements English pluralization algorithm based on Damian Conway's paper ["An Algorithmic Approach to English Pluralization"](http://www.csse.monash.edu.au/~damian/papers/HTML/Plurals.html).

The tests performed (April 2014) based on data from [Wiktionary](http://dumps.wikimedia.org/enwiktionary/latest/) show perfect results for 1000 basic words and 70% of corrects answers for the entire Wiktionary set of more than 100000 words.

Words checked: 142867 (979 basic words)
Correct: 71.24738% (100.0% basic words)
(If you are curious this test is part of the [unit tests](https://github.com/atteo/evo-inflector/blob/master/src/test/java/org/atteo/evo/inflector/EnglishInflectorTest.java).)

Changes
=======

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

You can download the library from [here](http://search.maven.org/remotecontent?filepath=org/atteo/evo-inflector/1.1/evo-inflector-1.1.jar) or use the following Maven dependency:

```xml
<dependency>
    <groupId>org.atteo</groupId>
    <artifactId>evo-inflector</artifactId>
    <version>1.1</version>
</dependency>
```



