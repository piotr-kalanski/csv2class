package com.datawizards

import java.io.PrintWriter
import java.util.Date

import com.datawizards.metadata.ClassMetadata
import com.univocity.parsers.csv.CsvWriter
import com.univocity.parsers.csv.CsvWriterSettings
import org.json4s.jackson.JsonMethods
import org.json4s.{DefaultFormats, Extraction}
import shapeless._

import scala.reflect.ClassTag

package object class2csv {

  trait CsvEncoder[A] {
    def encode(value: A): List[String]
  }

  trait FieldCsvEncoder[A] {
    def encode(value: A): List[String]
  }

  def createEncoder[A](func: A => List[String]): CsvEncoder[A] =
    new CsvEncoder[A] {
      def encode(value: A): List[String] =
        func(value)
    }

  def createFieldEncoder[A](func: A => List[String]): FieldCsvEncoder[A] =
    new FieldCsvEncoder[A] {
      def encode(value: A): List[String] =
        func(value)
    }

  implicit val stringEnc: FieldCsvEncoder[String] =
    createFieldEncoder(str => List(str))

  implicit val intEnc: FieldCsvEncoder[Int] =
    createFieldEncoder(num => List(num.toString))

  implicit val booleanEnc: FieldCsvEncoder[Boolean] =
    createFieldEncoder(bool => List(bool.toString))

  implicit val longEnc: FieldCsvEncoder[Long] =
    createFieldEncoder(num => List(num.toString))

  implicit val doubleEnc: FieldCsvEncoder[Double] =
    createFieldEncoder(num => List(num.toString))

  implicit val floatEnc: FieldCsvEncoder[Float] =
    createFieldEncoder(num => List(num.toString))

  implicit val shortEnc: FieldCsvEncoder[Short] =
    createFieldEncoder(num => List(num.toString))

  implicit val charEnc: FieldCsvEncoder[Char] =
    createFieldEncoder(num => List(num.toString))

  implicit val byteEnc: FieldCsvEncoder[Byte] =
    createFieldEncoder(num => List(num.toString))

  implicit val dateEnc: FieldCsvEncoder[Date] =
    createFieldEncoder(date => List(date.toString))

  implicit val bigIntEnc: FieldCsvEncoder[BigInt] =
    createFieldEncoder(num => List(num.toString))

  implicit def optionEnc[T](implicit innerEncoder: FieldCsvEncoder[T]): FieldCsvEncoder[Option[T]] =
    new FieldCsvEncoder[Option[T]] {
      def encode(value: Option[T]): List[String] =
        if(value.isDefined) innerEncoder.encode(value.get)
        else List("")
    }

  def jsonEncoder[T]: FieldCsvEncoder[T] =
    new FieldCsvEncoder[T] {
      implicit val formats = DefaultFormats
      def encode(value: T): List[String] = {
        val json = Extraction.decompose(value)(formats)
        List(JsonMethods.mapper.writeValueAsString(json))
      }
    }

  implicit def seqEncoder[T]: FieldCsvEncoder[Seq[T]] = jsonEncoder
  implicit def otherFieldEncoder[T]: FieldCsvEncoder[T] = jsonEncoder

  implicit val hnilEncoder: CsvEncoder[HNil] =
    createEncoder(hnil => Nil)

  implicit def hlistEncoder[H, T <: HList](
      implicit
      hEncoder: Lazy[FieldCsvEncoder[H]],
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
