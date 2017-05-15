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
- [Customizations](#customizations)

# Goals

- Read and convert CSV files to Scala case classes
- Write Scala case classes to CSV

# Getting started

Include dependency:

```scala
"com.github.piotr-kalanski" % "csv2class_2.11" % "0.2.0"
```

or

```xml
<dependency>
    <groupId>com.github.piotr-kalanski</groupId>
    <artifactId>csv2class_2.11</artifactId>
    <version>0.2.0</version>
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
