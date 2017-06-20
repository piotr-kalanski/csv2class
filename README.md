# csv2class
Generic CSV reader/writer with conversion to Scala case class without boilerplate

[![Build Status](https://api.travis-ci.org/piotr-kalanski/csv2class.png?branch=development)](https://api.travis-ci.org/piotr-kalanski/csv2class.png?branch=development)
[![codecov.io](http://codecov.io/github/piotr-kalanski/csv2class/coverage.svg?branch=development)](http://codecov.io/github/piotr-kalanski/csv2class/coverage.svg?branch=development)
[<img src="https://img.shields.io/maven-central/v/com.github.piotr-kalanski/csv2class_2.11.svg?label=latest%20release"/>](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22csv2class_2.11%22)
[![License](http://img.shields.io/:license-Apache%202-red.svg)](http://www.apache.org/licenses/LICENSE-2.0.txt)

# Table of contents

- [Goals](#goals)
- [Getting started](#getting-started)
- [Examples](#examples)
- [Versioning support](#versioning-support)
- [Customizations](#customizations)

# Goals

- Read and convert CSV files to Scala case classes
- Write Scala case classes to CSV

# Getting started

Include dependency:

```scala
"com.github.piotr-kalanski" % "csv2class_2.11" % "0.2.3"
```

or

```xml
<dependency>
    <groupId>com.github.piotr-kalanski</groupId>
    <artifactId>csv2class_2.11</artifactId>
    <version>0.2.3</version>
</dependency>
```

For reading from CSV import:
```scala
import com.datawizards.csv2class._
```

For writing to CSV import:
```scala
import com.datawizards.class2csv._
```

# Examples

## Reading from CSV

### Basic example:

CSV file:
```
s,i
first,10
second,11
```

Parsing command:
```scala
case class Foo(s: String, i: Int)
parseCSV[Foo]("foo.csv")
```

result:
```scala
Foo("first",10),
Foo("second",11)
```

### Different order of columns

CSV file:
```csv
i,s
10,first
11,second
```

Parsing command:
```scala
parseCSV[Foo]("foo.csv")
```

result:
```scala
Foo("first",10),
Foo("second",11)
```

### Returning not parsed rows

CSV file:
```
s,i
first,10
second,11
third,third
```

```scala
parseCSV[Foo]("foo.csv")
```

result:
```scala
Foo(first,10)
Foo(second,11)
java.lang.NumberFormatException: For input string: "third"
```

## Writing to CSV

```scala
case class Foo(s: String, i: Int)

val data = Seq(
  Foo("first",10),
  Foo("second",11)
)

writeCSV(data, file)
```

# Versioning support

First write below data to CSV:

```scala
case class PersonV2(name: String, age: Int, title: Option[String])
val peopleV2 = Seq(
    PersonV2("p1", 10, Some("Developer")),
    PersonV2("p2", 20, None),
    PersonV2("p3", 30, None)
)
writeCSV(peopleV2, file)
```

Read CSV file with previous version of compatible Person model:
```scala
case class Person(name: String, age: Int)
parseCSV[Person](file)
```

result:
```scala
Seq(
  Person("p1", 10),
  Person("p2", 20),
  Person("p3", 30)
)
```

Read CSV file using newer compatible Person model version - new columns should be `Option` type.

```scala
case class PersonV3(name: String, age: Int, title: Option[String], salary: Option[Long])
parseCSV[PersonV3](file)
```

result:
```scala
Seq(
  PersonV3("p1", 10, Some("Developer"), None),
  PersonV3("p2", 20, None, None),
  PersonV3("p3", 30, None, None)
)
```


# Customizations

## Change delimiter

```scala
parseCSV[Foo]("file.csv", delimiter = ';')
writeCSV(data, file, delimiter = ';')
```

## CSV without header

```scala
parseCSV[Foo]("file.csv", header = false, columns = Seq("s","i"))
writeCSV(data, "file.csv", header = false)
```
