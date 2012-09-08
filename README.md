About
=====

Evo Inflector implements English pluralization algorithm based on Damian Conway's paper ["An Algorithmic Approach to English Pluralization"](http://www.csse.monash.edu.au/~damian/papers/HTML/Plurals.html).

The tests performed (December 2011) based on data from [Wiktionary](http://dumps.wikimedia.org/enwiktionary/latest/) show perfect results for 1000 basic words and 70% of corrects answers for the entire Wiktionary set of more than 100000 words.

Words checked: 108774 (979 basic words)
Correct: 69.92112% (100.0% basic words)
(If you are curious this test is part of the [unit tests](http://code.google.com/p/evo-framework/source/browse/inflector/src/test/java/org/atteo/evo/inflector/EnglishInflectorTest.java).)

Usage
=====

```java
System.out.println(English.plural("word")); // == "words"
```

Download
========

You can download the library from [here](http://search.maven.org/remotecontent?filepath=org/atteo/evo-inflector/0.6/evo-inflector-0.6.jar) or use the following Maven dependency:

```xml
<dependency>
    <groupid>atteo.org</groupid>
    <artifactid>evo-inflector</artifactid>
    <version>0.6</version>
</dependency>
```



