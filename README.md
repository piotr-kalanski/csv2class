# csv2class
CSV reader with conversion to Scala case class

[![Build Status](https://api.travis-ci.org/piotr-kalanski/csv2class.png?branch=development)](https://api.travis-ci.org/piotr-kalanski/csv2class.png?branch=development)
[![codecov.io](http://codecov.io/github/piotr-kalanski/csv2class/coverage.svg?branch=development)](http://codecov.io/github/piotr-kalanski/csv2class/coverage.svg?branch=development)
[<img src="https://img.shields.io/maven-central/v/com.github.piotr-kalanski/csv2class_2.11.svg?label=latest%20release"/>](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22csv2class_2.11%22)
[![License](http://img.shields.io/:license-Apache%202-red.svg)](http://www.apache.org/licenses/LICENSE-2.0.txt)

# Table of contents

- [Goals](#goals)
- [Getting started](#getting-started)
- [Examples](#examples)

# Goals

- Read and convert CSV files to Scala case classes

# Getting started

Include dependency:

```scala
"com.github.piotr-kalanski" % "csv2class_2.11" % "0.1.0"
```

or

```xml
<dependency>
    <groupId>com.github.piotr-kalanski</groupId>
    <artifactId>csv2class_2.11</artifactId>
    <version>0.1.0</version>
</dependency>
```

# Examples

## Basic example:

CSV file:
```csv
s,i
first,10
second,11
```

Parsing command:
```scala
case class Foo(s: String, i: Int)
val result = ParseCSV[Foo]("foo.csv")
println(result)
```

```scala
Foo("first",10),
Foo("second",11)
```

## Different order of columns

CSV file:
```csv
i,s
10,first
11,second
```

Parsing command:
```scala
val result = ParseCSV[Foo]("foo.csv")
println(result)
```

```scala
Foo("first",10),
Foo("second",11)
```

# Customizations

## Change delimiter

```scala
ParseCSV[Foo]("file.csv", ';')
```
