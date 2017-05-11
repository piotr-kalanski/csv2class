package com.datawizards.csv2class

import java.util.{Calendar, Date}

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class Csv2ClassTest extends FunSuite {

  test("Default CSV file") {
    val result = ParseCSV[Foo]("src/test/resources/foo.csv")

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
    val result = ParseCSV[Foo]("src/test/resources/foo_changed_columns_order.csv")

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
    val result = ParseCSV[ClassWithAllTypes]("src/test/resources/all_types.csv")

    assertResult(Iterable(
      ClassWithAllTypes("s1",1,2L,3.0,4.0f,5,true,'a',6),
      ClassWithAllTypes("s2",21,22L,23.0,24.0f,25,false,'b',26)
    )) {
      result._1
    }

    assertResult(true) {
      result._2.isEmpty
    }
  }

  private def date(year: Int, month: Int, day: Int): Date = {
    val cal = Calendar.getInstance()
    cal.set(year, month-1, day, 0, 0, 0)
    cal.getTime
  }

}
