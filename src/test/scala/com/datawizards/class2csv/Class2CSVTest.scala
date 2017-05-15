package com.datawizards.class2csv

import com.datawizards.model.{ClassWithAllTypes, Foo}
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class Class2CSVTest extends FunSuite {

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
        |second,11""".stripMargin

    assertResult(expected) {
      readFileContent(file)
    }
  }

  test("All types") {
    val file = "target/all_types.csv"
    val data = Seq(
      ClassWithAllTypes("s1",1,2L,3.0,4.0f,5,bool=true,'a',6, BigInt(9999)),
      ClassWithAllTypes("s2",21,22L,23.0,24.0f,25,bool=false,'b',26, BigInt(9999))
    )

    writeCSV(data, file)
    val expected =
      """str,int,long,double,float,short,bool,char,byte,bigint
        |s1,1,2,3.0,4.0,5,true,a,6,9999
        |s2,21,22,23.0,24.0,25,false,b,26,9999""".stripMargin

    assertResult(expected) {
      readFileContent(file)
    }
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
        |second;11""".stripMargin

    assertResult(expected) {
      readFileContent(file)
    }
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
        |second;11""".stripMargin

    assertResult(expected) {
      readFileContent(file)
    }
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
        |"second, second",11""".stripMargin

    assertResult(expected) {
      readFileContent(file)
    }
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
      data,
      path = file,
      delimiter = ';',
      columns = Seq("s2","i2")
    )
    val expected =
      """s2;i2
        |p1;10
        |p2;20
        |p3;30
        |"p;4";40""".stripMargin

    assertResult(expected) {
      readFileContent(file)
    }
  }

  private def readFileContent(file: String): String =
    scala.io.Source.fromFile(file).getLines().mkString("\n")

}
