package com.datawizards.class2csv

import java.text.SimpleDateFormat
import java.time.LocalDate

import com.datawizards.model._
import org.junit.runner.RunWith
import org.scalatest.{FunSuite, Matchers}
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class Class2CSVTest extends FunSuite with Matchers {

  test("Default CSV file") {
    val file = "target/foo.csv"
    val data = Seq(
      Foo("first",10),
      Foo("second",11)
    )

    writeCSV(data, file)
    val expected =
      """s,i
        |first,10
        |second,11""".stripMargin.replace("\r", "").replace("\n", "")

    readFileContent(file) should equal(expected)
  }

  test("All primitive types") {
    val file = "target/all_types.csv"
    val data = Seq(
      ClassWithAllTypes("s1",1,2L,3.0,4.0f,5,bool=true,'a',6, BigInt(9999)),
      ClassWithAllTypes("s2",21,22L,23.0,24.0f,25,bool=false,'b',26, BigInt(9999))
    )

    writeCSV(data, file)
    val expected =
      """str,int,long,double,float,short,bool,char,byte,bigint
        |s1,1,2,3.0,4.0,5,true,a,6,9999
        |s2,21,22,23.0,24.0,25,false,b,26,9999""".stripMargin.replace("\r", "").replace("\n", "")

    readFileContent(file) should equal(expected)
  }

  test("Class with array") {
    val file = "target/class_with_array.csv"
    val data = Seq(
      ClassWithArray("1",Seq("1","2")),
      ClassWithArray("2",Seq("1","2","3"))
    )

    writeCSV(data, file)
    val expected =
      """id,vals
        |1,"[""1"",""2""]"
        |2,"[""1"",""2"",""3""]"""".stripMargin.replace("\r", "").replace("\n", "")

    readFileContent(file) should equal(expected)
  }

  test("Class with struct") {
    val file = "target/class_with_struct.csv"
    val data = Seq(
      ClassWithStruct("1",Person("p1", 10)),
      ClassWithStruct("2",Person("p2", 20))
    )

    writeCSV(data, file)
    val expected =
      """id,person
        |1,"{""name"":""p1"",""age"":10}"
        |2,"{""name"":""p2"",""age"":20}"""".stripMargin.replace("\r", "").replace("\n", "")

    readFileContent(file) should equal(expected)
  }

  test("Class with array of struct") {
    val file = "target/class_with_array_of_struct.csv"
    val data = Seq(
      ClassWithArrayOfStruct("1",Seq(Person("p1", 10))),
      ClassWithArrayOfStruct("2",Seq(Person("p1", 10),Person("p2", 20),Person("p3", 30)))
    )

    writeCSV(data, file)
    val expected =
      """id,people
        |1,"[{""name"":""p1"",""age"":10}]"
        |2,"[{""name"":""p1"",""age"":10},{""name"":""p2"",""age"":20},{""name"":""p3"",""age"":30}]"""".stripMargin.replace("\r", "").replace("\n", "")

    readFileContent(file) should equal(expected)
  }

  test("CSV file with ; delimiter") {
    val file = "target/foo_delimiter.csv"
    val data = Seq(
      Foo("first",10),
      Foo("second",11)
    )

    writeCSV(data, file, delimiter = ';')
    val expected =
      """s;i
        |first;10
        |second;11""".stripMargin.replace("\r", "").replace("\n", "")

    readFileContent(file) should equal(expected)
  }

  test("CSV file without header") {
    val file = "target/foo_without_header.csv"
    val data = Seq(
      Foo("first",10),
      Foo("second",11)
    )

    writeCSV(data, file, delimiter = ';', header = false)
    val expected =
      """first;10
        |second;11""".stripMargin.replace("\r", "").replace("\n", "")

    readFileContent(file) should equal(expected)
  }

  test("CSV file with quotes") {
    val file = "target/foo_with_quotes.csv"
    val data = Seq(
      Foo("first, first",10),
      Foo("second, second",11)
    )

    writeCSV(data, file)
    val expected =
      """s,i
        |"first, first",10
        |"second, second",11""".stripMargin.replace("\r", "").replace("\n", "")

    readFileContent(file) should equal(expected)
  }

  test("CSV file with custom header and separator") {
    val file = "target/foo_with_custom_header_and_separator.csv"
    val data = Seq(
      Foo("p1", 10),
      Foo("p2", 20),
      Foo("p3", 30),
      Foo("p;4", 40)
    )

    writeCSV(
      data = data,
      path = file,
      delimiter = ';',
      header = true,
      columns = Seq("s2","i2")
    )
    val expected =
      """s2;i2
        |p1;10
        |p2;20
        |p3;30
        |"p;4";40""".stripMargin.replace("\r", "").replace("\n", "")

    readFileContent(file) should equal(expected)
  }

  test("Write option") {
    val file = "target/personV2.csv"
    val data = Seq(
      PersonV3("p1", 10, Some("Developer"), Some(1000L)),
      PersonV3("p2", 20, None, Some(2000L)),
      PersonV3("p3", 30, None, None)
    )

    writeCSV(
      data = data,
      path = file,
      delimiter = ';'
    )
    val expected =
      """name;age;title;salary
        |p1;10;Developer;1000
        |p2;20;;2000
        |p3;30;;""".stripMargin.replace("\r", "").replace("\n", "")

    readFileContent(file) should equal(expected)
  }

  test("Write java.util.Date") {
    val file = "target/java_util_date.csv"
    val data = Seq(
      ClassWithDate(new SimpleDateFormat("yyyy-MM-dd").parse("2000-01-02")),
      ClassWithDate(new SimpleDateFormat("yyyy-MM-dd").parse("2001-02-03")),
      ClassWithDate(new SimpleDateFormat("yyyy-MM-dd").parse("2002-03-04"))
    )

    writeCSV(
      data = data,
      path = file,
      delimiter = ';'
    )
    val expected =
      """date
        |2000-01-02
        |2001-02-03
        |2002-03-04""".stripMargin.replace("\r", "").replace("\n", "")

    readFileContent(file) should equal(expected)
  }

  test("Write java.sql.Date") {
    val file = "target/java_sql_date.csv"
    val data = Seq(
      ClassWithSqlDate(java.sql.Date.valueOf("2000-01-02")),
      ClassWithSqlDate(java.sql.Date.valueOf("2001-02-03")),
      ClassWithSqlDate(java.sql.Date.valueOf("2002-03-04"))
    )

    writeCSV(
      data = data,
      path = file,
      delimiter = ';'
    )
    val expected =
      """date
        |2000-01-02
        |2001-02-03
        |2002-03-04""".stripMargin.replace("\r", "").replace("\n", "")

    readFileContent(file) should equal(expected)
  }

  private def readFileContent(file: String): String =
    scala.io.Source.fromFile(file).getLines().mkString("").replace("\r", "").replace("\n", "")

}
