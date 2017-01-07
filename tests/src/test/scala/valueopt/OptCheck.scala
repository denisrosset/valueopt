package valueopt

import scala.util.{Left, Right}

import org.scalatest.FunSuite

//import spire.algebra.Eq

class OptCheck extends FunSuite {

  def customToString[A](opt: Opt[A]): String = opt.toString

  def intToString(opt: Opt[Int]): String = opt.toString

  def strToString(opt: Opt[String]): String = opt.toString

  def parseInt(str: String): Opt[Int] = try {
    Opt(java.lang.Integer.parseInt(str))
  } catch {
    case e: NumberFormatException => Opt.none[Int]
  }

  def isEven(i: Int): Boolean = (i % 2) == 0

  test("OptNone") {
    assert(Opt.none[Int].isEmpty)
    assert(Opt.none[String].isEmpty)
    assert(!Opt.none[Int].nonEmpty)
    assert(!Opt.none[String].nonEmpty)
    assert(!Opt.none[Int].isDefined)
    assert(!Opt.none[String].isDefined)

    assertResult("OptNone") { Opt.none[Int].toString }
    assertResult("OptNone") { Opt.none[String].toString }

    assertResult("OptNone") { customToString(Opt.none[Int]) }
    assertResult("OptNone") { customToString(Opt.none[String]) }
    assertResult("OptNone") { intToString(Opt.none[Int]) }
    assertResult("OptNone") { strToString(Opt.none[String]) }

    intercept[Exception] { Opt.none[Int].get }
    intercept[Exception] { Opt.none[String].get }
  }

  test("Opt.== / !=") {
    assert(Opt(1) != Opt(3))
    assert(Opt.none[Int] == Opt.none[Int])
    assert(Opt.none[Int] != Opt(0))
    assert(Opt(0) != Opt.none[Int])
    assert(Opt(1) != Opt.none[Int])
    assert(Opt(1) == Opt(1))
    assert(Opt("a") == Opt("a"))
    assert(Opt("a") != Opt("b"))
    assert(Opt("a") != Opt.none[String])
    assert(Opt.none[String] != Opt("a"))
  }

  test("Opt.hashCode") {
    assert(Opt(1).hashCode == 1.hashCode)
    Opt.none.hashCode // should not throw
    assert(Opt("str").hashCode == "str".hashCode)
  }

  /* TODO: restore cats-kernel and add scalaz
  test("Opt Equality"){
    import spire.std.boolean._
    val eq = Eq[Opt[Boolean]]
    assert(eq.eqv(Opt(true), Opt(true)))
    assert(eq.eqv(Opt.empty, Opt.empty))
    assert(eq.neqv(Opt.empty, Opt(true)))
    assert(eq.neqv(Opt(true), Opt.empty))
  }
   */

  test("Opt(value)") {
    assert(Opt(1).nonEmpty)
    assert(Opt("abc").nonEmpty)
    assert(Opt(1).isDefined)
    assert(Opt("abc").isDefined)
    assert(!Opt(1).isEmpty)
    assert(!Opt("abc").isEmpty)

    assertResult("1") { Opt(1).toString }
    assertResult("abc") { Opt("abc").toString }

    assertResult("1") { customToString(Opt(1)) }
    assertResult("abc") { customToString(Opt("abc")) }
    assertResult("1") { intToString(Opt(1)) }
    assertResult("abc") { strToString(Opt("abc")) }

    assertResult(1) { Opt(1).get }
    assertResult("abc") { Opt("abc").get }
  }

  test("for comprehension") {
    val a = Opt(33)
    val b = Opt(1999)
    val c = Opt(2)

    val d = a.filter(_ % 2 == 1)
    val e = b.map(_ + 999)

    assertResult(Opt(6029)) {
      for {
        q <- Opt(0)
        x <- d
        y <- e
        z <- c
      } yield q + x + y * z
    }
  }

  test("Name-based extractor") {
    Opt(2) match {
      case OptSome(x) => // success
      case _ => fail()
    }
    Opt.none[Int] match {
      case OptSome(x) => fail()
      case _ => // success
    }
  }

  test("Opt.filter") {
    def isEven(i: Int): Boolean = (i % 2 == 0)
    assert(Opt(1).filter(isEven).isEmpty)
    assertResult(2)(Opt(2).filter(isEven).get)
    assert(Opt.none[Int].filter(_ % 2 == 0).isEmpty)
  }

  test("Opt.filterNot") {
    def isEven(i: Int): Boolean = (i % 2 == 0)
    assertResult(1)(Opt(1).filterNot(isEven).get)
    assert(Opt(2).filterNot(isEven).isEmpty)
    assert(Opt.none[Int].filterNot(_ % 2 == 0).isEmpty)
  }


  test("Opt.map") {
    assertResult(Opt(2))(Opt(1).map(_ * 2))
    assertResult(Opt("2"))(Opt(2).map(_.toString))
    assertResult(Opt(2))(parseInt("2"))
    assertResult(Opt.none[Int])(parseInt("abc"))
  }

  test("Opt.flatMap") {
    assertResult(Opt(2))(Opt("2").flatMap(parseInt))
    assertResult(Opt.none[Int])(Opt("abc").flatMap(parseInt))
    assertResult(Opt.none[Int])(Opt.none[String].flatMap(parseInt))
  }

  test("Opt.flatten") {
    assertResult(Opt(2))(Opt(Opt(2)).flatten)
    assertResult(Opt.none[Int])(Opt(Opt.none[Int]).flatten)
  }

  test("Opt.fold") {
    assertResult(2)(Opt(1).fold(0)(_ * 2))
    assertResult(0)(Opt.none[Int].fold(0)(_ * 2))
    assertResult("abcabc")(Opt("abc").fold("")(_ * 2))
    assertResult("")(Opt.none[String].fold("")(_ * 2))
  }

  test("Opt.getOrElse") {
    assertResult(2)(Opt(2).getOrElse(0))
    assertResult(0)(Opt.none[Int].getOrElse(0))
    assertResult("abc")(Opt("abc").getOrElse(""))
    assertResult("abc")(Opt("abc").getOrElse(sys.error("Should not be executed")))
    assertResult("")(Opt.none[String].getOrElse(""))
  }

  test("Opt.orNull") {
    assertResult(null)(Opt.none[String].orNull)
    assertResult("str")(Opt("str").orNull)
  }

  test("Opt.iterator") {
    assert(!Opt.none[Int].iterator.hasNext)
    assertResult(2)(Opt(2).iterator.next)
  }

  test("Opt.toOption") {
    assertResult(Some(2))(Opt(2).toOption)
    assertResult(None)(Opt.none[Int].toOption)
    assertResult(Some("abc"))(Opt("abc").toOption)
    assertResult(None)(Opt.none[String].toOption)
  }

  test("Opt.toList") {
    assertResult(List(2))(Opt(2).toList)
    assertResult(List("str"))(Opt("str").toList)
    assertResult(Nil)(Opt.none[Int].toList)
    assertResult(Nil)(Opt.none[String].toList)
  }

  test("Opt.collect") {
    val nonEmptyLength: PartialFunction[String, Int] = {
      case str if str.nonEmpty => str.length
    }

    assertResult(Opt.none[Int])(Opt("").collect(nonEmptyLength))
    assertResult(Opt.none[Int])(Opt.none[String].collect(nonEmptyLength))
    assertResult(Opt(4))(Opt("test").collect(nonEmptyLength))
  }

  test("Opt.contains") {
    assert(!Opt.none[String].contains(null))
    assert(Opt("test").contains("test"))
    assert(!Opt("test1").contains("test"))
    assert(!Opt("test").contains(null))
  }

  test("Opt.exists") {
    assert(Opt(2).exists(isEven))
    assert(!Opt(3).exists(isEven))
    assert(!Opt.none[Int].exists(isEven))
  }

  test("Opt.forall") {
    assert(Opt(2).forall(isEven))
    assert(!Opt(3).forall(isEven))
    assert(Opt.none[Int].forall(isEven))
  }

  test("Opt.foreach") {
    var v: Int = 0

    v = -1
    Opt(2).foreach( v = _ )
    assert(v == 2)

    v = -1
    Opt(0).foreach( v = _ )
    assert(v == 0)

    v = -1
    Opt.none[Int].foreach( v = _ )
    assert(v == -1)
  }

  test("Opt.toRight") {
    assertResult(Right(2))(Opt(2).toRight("left"))
    assertResult(Left("left"))(Opt.none[Int].toRight("left"))
  }

  test("Opt.toLeft") {
    assertResult(Left(2))(Opt(2).toLeft("right"))
    assertResult(Right("right"))(Opt.none[Int].toLeft("right"))
  }

}
