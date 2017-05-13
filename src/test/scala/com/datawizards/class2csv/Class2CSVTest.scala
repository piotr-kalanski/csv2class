package com.datawizards.class2csv

import java.util.{Calendar, Date}

import com.datawizards.model.{ClassWithAllTypes, ClassWithDate, Foo}
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner


@RunWith(classOf[JUnitRunner])
class Class2CSVTest extends FunSuite {

  test("Default CSV file") {
    val file = "tmp/foo.csv"
    val data = Seq(
      Foo("first",10),
      Foo("second",11)
    )

    writeCSV[Foo](data, file)
    val expected =
      """s,i
        |first,10
        |second,11""".stripMargin

    assertResult(expected) {
      readFileContent(file)
    }

  }

  private def readFileContent(file: String): String =
    scala.io.Source.fromFile(file).getLines().mkString("\n")

  private def date(year: Int, month: Int, day: Int): Date = {
    val cal = Calendar.getInstance()
    cal.set(year, month-1, day, 0, 0, 0)
    cal.getTime
  }

}
