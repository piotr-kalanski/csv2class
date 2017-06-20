package com.datawizards

import java.util.{Calendar, Date}

import com.datawizards.csv2class._
import com.datawizards.class2csv._
import com.datawizards.model._
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class VersioningTest extends FunSuite {

  test("Versioning") {
    val peopleV2 = Seq(
      PersonV2("p1", 10, Some("Developer")),
      PersonV2("p2", 20, None),
      PersonV2("p3", 30, None)
    )
    val expectedPoepleV3 = Seq(
      PersonV3("p1", 10, Some("Developer"), None),
      PersonV3("p2", 20, None, None),
      PersonV3("p3", 30, None, None)
    )
    val expectedPoepleV1 = Seq(
      Person("p1", 10),
      Person("p2", 20),
      Person("p3", 30)
    )

    val file = "target/people_v2.csv"
    writeCSV(peopleV2, file)

    val resultV1 = parseCSV[Person](file)
    val resultV3 = parseCSV[PersonV3](file)

    assertResult(expectedPoepleV1) { resultV1._1 }
    assertResult(expectedPoepleV3) { resultV3._1 }
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
