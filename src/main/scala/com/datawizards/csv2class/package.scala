package com.datawizards

import java.util.Date

import com.datawizards.metadata.ClassMetadata
import com.univocity.parsers.csv.{CsvFormat, CsvParser, CsvParserSettings}
import shapeless._

import scala.reflect.ClassTag
import scala.io.Source
import scala.util.{Failure, Success, Try}

package object csv2class {

  trait Read[A] { def reads(s: String): Try[A] }

  object Read {
    def apply[A](implicit readA: Read[A]): Read[A] = readA

    implicit object stringRead extends Read[String] {
      def reads(s: String): Try[String] = Success(s)
    }

    implicit object intRead extends Read[Int] {
      def reads(s: String) = Try(s.toInt)
    }

    implicit object longRead extends Read[Long] {
      def reads(s: String) = Try(s.toLong)
    }

    implicit object doubleRead extends Read[Double] {
      def reads(s: String) = Try(s.toDouble)
    }

    implicit object floatRead extends Read[Float] {
      def reads(s: String) = Try(s.toFloat)
    }

    implicit object shortRead extends Read[Short] {
      def reads(s: String) = Try(s.toShort)
    }

    implicit object booleanRead extends Read[Boolean] {
      def reads(s: String) = Try(s.toBoolean)
    }

    implicit object charRead extends Read[Char] {
      def reads(s: String) = Try(s.head)
    }

    implicit object byteRead extends Read[Byte] {
      def reads(s: String) = Try(s.toByte)
    }

    implicit object dateRead extends Read[Date] {
      private val format = new java.text.SimpleDateFormat("yyyy-MM-dd")
      def reads(s: String) = Try(format.parse(s))
    }
  }

  trait FromRow[L <: HList] { def apply(row: List[String]): Try[L] }

  object FromRow {
    import HList.ListCompat._

    def apply[L <: HList](implicit fromRow: FromRow[L]): FromRow[L] = fromRow

    def fromFunc[L <: HList](f: List[String] => Try[L]) = new FromRow[L] {
      def apply(row: List[String]): Try[L] = f(row)
    }

    implicit val hnilFromRow: FromRow[HNil] = fromFunc {
      case Nil => Success(HNil)
      case _ => Failure(new RuntimeException("No more rows expected"))
    }

    implicit def hconsFromRow[H: Read, T <: HList: FromRow]: FromRow[H :: T] =
      fromFunc {
        case h :: t => for {
          hv <- Read[H].reads(h)
          tv <- FromRow[T].apply(t)
        } yield hv :: tv
        case Nil => Failure(new RuntimeException("Expected more cells"))
      }
  }

  trait RowParser[A] {
    def apply[L <: HList](row: List[String])(implicit
                                             gen: Generic.Aux[A, L],
                                             fromRow: FromRow[L]
    ): Try[A] = fromRow(row).map(gen. from)
  }

  object parseCSV {
    def apply[T]: ParseCSV[T] = new ParseCSV[T] {}
  }

  trait ParseCSV[T] {
    def apply[L <: HList](
        path: String,
        delimiter: Char = ',',
        header: Boolean = true,
        columns: Seq[String] = Seq.empty,
        escape: Char = '"',
        quote: Char = '"'
    )
    (implicit
        ct: ClassTag[T],
        gen: Generic.Aux[T, L],
        fromRow: FromRow[L]
    ): (Iterable[T], Iterable[Throwable]) = {
      val format = new CsvFormat()
      format.setDelimiter(delimiter)
      format.setQuote(quote)
      format.setQuoteEscape(escape)

      def parseHeader(header: String): Seq[String] = {
        val settings = new CsvParserSettings
        settings.setFormat(format)
        val parser = new CsvParser(settings)
        parser.parseLine(header).toSeq
      }

      def calculateFieldsPositions(header: Seq[String], fields: Seq[String]): Map[Int, Int] =
        (
          for {
            f <- fields
            h <- header
            if f == h
          } yield fields.indexOf(h) -> header.indexOf(f)
          ).toMap

      def parseContent(lines: Iterator[String], fieldsMapping: Map[Int, Int], delimiter: Char)
      : (Iterable[T], Iterable[Throwable]) = {
        val rowParserFor = new RowParser[T] {}
        val settings = new CsvParserSettings
        settings.setFormat(format)
        settings.selectIndexes(
          (0 until fieldsMapping.size).map(i => new Integer(fieldsMapping(i))): _*
        )
        val parser = new CsvParser(settings)
        val convertedLines = for {
          line <- lines
          parsedLine = parser.parseLine(line)
        } yield rowParserFor(parsedLine.toList)

        val iterable = convertedLines.toIterable

        val (s,f) = iterable.span {
          case _:Success[T] => true
          case _:Failure[T] => false
        }

        (
          s.map (x => x.get),
          f.map (x => x.failed.get)
        )
      }

      val source = Source.fromFile(path)
      val lines = source.getLines()
      val fields = ClassMetadata.getClassFields[T]
      val headerColumns = if(header) parseHeader(lines.next()) else columns
      val fieldsMapping = calculateFieldsPositions(headerColumns, fields)
      parseContent(lines, fieldsMapping, delimiter)
    }
  }

}
