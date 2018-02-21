package consulting.hindsight.parboiled2_condition

import org.parboiled2._
import scala.util.Try

class TestParser(override val input: ParserInput)
extends Parser {

  val wsChar = CharPredicate.from(Character.isWhitespace)

  def ws = rule {
    oneOrMore(wsChar)
  }

  val digit = CharPredicate.Digit

  def intField = rule {
    capture(optional('-') ~ oneOrMore(digit)) ~> {s =>
      val i = Try(s.toInt)
      test(i.isSuccess) ~ push(i.get)
    }
  }

  def boolField = rule {
    atomic(capture("true" | "false")) ~> {s =>
      if(s == "true") true
      else false
    }
  }

  def quotedString = rule {
    '\"' ~ capture(zeroOrMore(CharPredicate.AlphaNum ++ wsChar)) ~ '\"'
  }

  def conditional[U](condition: Boolean, parse: () => Rule1[U]): Rule1[Option[U]] = rule {
    test(condition)  ~ parse() ~> (Some(_)) | push(None)
  }

  def dependentFields = rule {
    boolField ~> (conditional(_, () => rule { ws ~ intField }))
  }

  def testRule = rule {
    dependentFields ~ ws ~ quotedString ~ EOI ~> ((_, _))
  }
}
