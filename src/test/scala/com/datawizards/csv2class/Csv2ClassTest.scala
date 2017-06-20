package com.datawizards.csv2class

import java.util.{Calendar, Date}

import com.datawizards.model._
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class Csv2ClassTest extends FunSuite {

  test("Default CSV file") {
    val result = parseCSV[Foo]("src/test/resources/foo.csv")

    assertResult(Iterable(
      Foo("first",10),
      Foo("second",11)
    )) {
      result._1
    }

    assertResult(true) {
      result._2.isEmpty
    }
  }

  test("Changed columns order") {
    val result = parseCSV[Foo]("src/test/resources/foo_changed_columns_order.csv")

    assertResult(Iterable(
      Foo("first",10),
      Foo("second",11)
    )) {
      result._1
    }

    assertResult(true) {
      result._2.isEmpty
    }
  }

  test("All types") {
    val result = parseCSV[ClassWithAllTypes]("src/test/resources/all_types.csv")

    assertResult(Iterable(
      ClassWithAllTypes("s1",1,2L,3.0,4.0f,5,bool=true,'a',6, BigInt(9999)),
      ClassWithAllTypes("s2",21,22L,23.0,24.0f,25,bool=false,'b',26, BigInt(9999))
    )) {
      result._1
    }

    assertResult(true) {
      result._2.isEmpty
    }
  }

  test("Dates") {
    val result = parseCSV[ClassWithDate]("src/test/resources/dates.csv")

    assert(Math.abs(result._1.head.date.getTime - date(2000,1,2).getTime) < 1000)
    assert(Math.abs(result._1.tail.head.date.getTime - date(2000,2,3).getTime) < 1000)

    assertResult(true) {
      result._2.isEmpty
    }
  }

  test("CSV file with one not correct line") {
    val result = parseCSV[Foo]("src/test/resources/foo_not_correct.csv")

    assertResult(Iterable(
      Foo("first",10),
      Foo("second",11)
    )) {
      result._1
    }

    assertResult(1) {
      result._2.size
    }
  }

  test("CSV file with ; delimiter") {
    val result = parseCSV[Foo]("src/test/resources/foo_delimiter.csv", ';')

    assertResult(Iterable(
      Foo("first",10),
      Foo("second",11)
    )) {
      result._1
    }

    assertResult(true) {
      result._2.isEmpty
    }
  }

  test("CSV file without header - provide column names") {
    val result = readCSV[Foo](
      path = "src/test/resources/foo_without_header.csv",
      header = false,
      columns = Seq("s","i")
    )

    assertResult(Iterable(
      Foo("first",10),
      Foo("second",11)
    )) {
      result._1
    }

    assertResult(true) {
      result._2.isEmpty
    }
  }

  test("CSV file without header - column names from class") {
    val result = parseCSV[Foo](
      path = "src/test/resources/foo_without_header.csv",
      header = false
    )

    assertResult(Iterable(
      Foo("first",10),
      Foo("second",11)
    )) {
      result._1
    }

    assertResult(true) {
      result._2.isEmpty
    }
  }

  test("CSV file with quotes") {
    val result = parseCSV[Foo](
      path = "src/test/resources/foo_with_quotes.csv"
    )

    assertResult(Iterable(
      Foo("first, first",10),
      Foo("second, second",11)
    )) {
      result._1
    }

    assertResult(true) {
      result._2.isEmpty
    }
  }

  test("CSV file without header and custom separator") {
    val result = parseCSV[Foo](
      path = "src/test/resources/foo_without_header_and_custom_format.csv",
      delimiter = ';',
      header = false,
      columns = Seq("s","i")
    )

    assertResult(Iterable(
      Foo("p1", 10),
      Foo("p2", 20),
      Foo("p3", 30),
      Foo("p;4", 40)
    )) {
      result._1
    }

    assertResult(true) {
      result._2.isEmpty
    }
  }

  test("Option type read") {
    val result = parseCSV[PersonV3](
      path = "src/test/resources/people.csv",
      delimiter = ';'
    )

    assertResult(Iterable(
      PersonV3("p1", 10, Some("Developer"), Some(1000L)),
      PersonV3("p2", 20, None, Some(2000L)),
      PersonV3("p3", 30, None, None)
    )) {
      result._1
    }

    assertResult(1) {
      result._2.size
    }
  }

  test("Read only columns that exists in class") {
    val result = parseCSV[Person](
      path = "src/test/resources/people_v2.csv",
      delimiter = ';'
    )

    assertResult(Iterable(
      Person("p1", 10),
      Person("p2", 20),
      Person("p3", 30)
    )) {
      result._1
    }

    assertResult(1) {
      result._2.size
    }
  }

  test("Read only columns that exists in class - default value for Option type") {
    val result = parseCSV[PersonV3](
      path = "src/test/resources/people_v2.csv",
      delimiter = ';'
    )

    assertResult(Iterable(
      PersonV3("p1", 10, Some("Developer"), None),
      PersonV3("p2", 20, None, None),
      PersonV3("p3", 30, None, None)
    )) {
      result._1
    }

    assertResult(1) {
      result._2.size
    }
  }

  private def date(year: Int, month: Int, day: Int): Date = {
    val cal = Calendar.getInstance()
    cal.set(year, month-1, day, 0, 0, 0)
    cal.getTime
  }

}
