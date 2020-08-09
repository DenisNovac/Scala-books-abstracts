import cats.instances.option._
import cats.syntax.apply._

(Option(123), Option("abc")).tupled  // res0: Option[(Int, String)] = Some((123,abc))

(Option(123), Option("abc"), None).tupled  // res1: Option[(Int, String, Nothing)] = None

/** Дополнительный синтаксис - mapN */
/** Требует имлисит Functor */
/** Использует Semigroupal для распаковки Option и Functor для применения значений к функции */
case class Cat(name: String, born: Int)

(
  Option("Garfield"),
  Option(1978)
).mapN(Cat.apply)
// res2: Option[Cat] = Some(Cat(Garfield,1978))

import cats.instances.list._
import cats.instances.string._
import cats.instances.int._
import cats.instances.invariant._
import cats.Monoid

case class Catt(name: String, born: Int, food: List[String])

val tupleToCat: (String, Int, List[String]) => Catt = Catt.apply _

val catToTuple: Catt => (String, Int, List[String]) =
  cat => (cat.name, cat.born, cat.food)

implicit val catMonoid: Monoid[Catt] = (
  Monoid[String],
  Monoid[Int],
  Monoid[List[String]]
).imapN(tupleToCat)(catToTuple)

import cats.syntax.semigroup._

val garf = Catt("Garf", 1978, List("Lasagne"))
val het = Catt("Heathcliff", 1988, List("Junk"))

garf |+| het  // res3: Catt = Catt(GarfHeathcliff,3966,List(Lasagne, Junk))
