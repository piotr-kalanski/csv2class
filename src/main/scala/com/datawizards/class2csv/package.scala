package com.datawizards

import java.io.PrintWriter
import java.util.Date

import com.datawizards.metadata.ClassMetadata
import com.univocity.parsers.csv.CsvWriter
import com.univocity.parsers.csv.CsvWriterSettings
import shapeless._

import scala.reflect.ClassTag

package object class2csv {

  trait CsvEncoder[A] {
    def encode(value: A): List[String]
  }

    def createEncoder[A](func: A => List[String]): CsvEncoder[A] =
      new CsvEncoder[A] {
        def encode(value: A): List[String] =
          func(value)
      }

    implicit val stringEnc: CsvEncoder[String] =
     createEncoder(str => List(str))

    implicit val intEnc: CsvEncoder[Int] =
     createEncoder(num => List(num.toString))

    implicit val booleanEnc: CsvEncoder[Boolean] =
     createEncoder(bool => List(bool.toString))

    implicit val longEnc: CsvEncoder[Long] =
      createEncoder(num => List(num.toString))

    implicit val doubleEnc: CsvEncoder[Double] =
      createEncoder(num => List(num.toString))

    implicit val floatEnc: CsvEncoder[Float] =
      createEncoder(num => List(num.toString))

    implicit val shortEnc: CsvEncoder[Short] =
      createEncoder(num => List(num.toString))

    implicit val charEnc: CsvEncoder[Char] =
      createEncoder(num => List(num.toString))

    implicit val byteEnc: CsvEncoder[Byte] =
      createEncoder(num => List(num.toString))

    implicit val dateEnc: CsvEncoder[Date] =
      createEncoder(date => List(date.toString))

    implicit val bigIntEnc: CsvEncoder[BigInt] =
      createEncoder(num => List(num.toString))

    implicit val hnilEncoder: CsvEncoder[HNil] =
      createEncoder(hnil => Nil)

    implicit def hlistEncoder[H, T <: HList](
        implicit
        hEncoder: Lazy[CsvEncoder[H]],
        tEncoder: CsvEncoder[T]
      ): CsvEncoder[H :: T] = createEncoder {
        case h :: t =>
          hEncoder.value.encode(h) ++ tEncoder.encode(t)
      }

    implicit def genericEncoder[A, R](
       implicit
       gen: Generic.Aux[A, R],
       rEncoder: Lazy[CsvEncoder[R]]
     ): CsvEncoder[A] = createEncoder { value =>
      rEncoder.value.encode(gen.to(value))
    }


  def writeCSV[T](
      data: Traversable[T],
      path: String,
      delimiter: Char = ',',
      header: Boolean = true,
      columns: Seq[String] = Seq.empty,
      escape: Char = '"',
      quote: Char = '"'
    )
    (implicit
       ct:ClassTag[T],
       encoder: CsvEncoder[T]
    ): Unit = {
    val pw = new PrintWriter(path)
    val settings = new CsvWriterSettings
    settings.getFormat.setDelimiter(delimiter)
    settings.getFormat.setQuote(quote)
    settings.getFormat.setQuoteEscape(escape)
    val csvWriter = new CsvWriter(pw, settings)

    if(header) {
      val headerColumns = if(columns.isEmpty)
        ClassMetadata.getClassFields[T]
      else columns

      csvWriter.writeHeaders(headerColumns:_*)
    }

    for(e <- data)
      csvWriter.writeRow(encoder.encode(e).toArray)

    pw.close()
  }

}
