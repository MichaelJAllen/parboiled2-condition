package consulting.hindsight.parboiled2_condition

import org.parboiled2.ParseError
import scala.util.{Failure, Success, Try}

object Main
extends App {

  eval(new TestParser("true 52 \"Should be Success with Some 52\""))
  eval(new TestParser("false \"Should be Success with None\""))
  eval(new TestParser("false 25 \"Should be Failure as 25 not a quoted string\""))
  eval(new TestParser("true ha \"Should be Failure as ha not an integer\""))
  eval(new TestParser("true 9874923489234234898234823748374 \"Should be Failure as number not a valid integer\""))

  def eval(parser: TestParser): Unit = parser.testRule.run() match {
    case Success(v) => println(s"Success! Values read are ${v._1}, ${v._2}")
    case Failure(e) => e match {
      case pe: ParseError => println(s"Failure: ${parser.formatError(pe)}")
      case _ => println(s"Failure: ${e.getMessage}")
    }
  }
}
