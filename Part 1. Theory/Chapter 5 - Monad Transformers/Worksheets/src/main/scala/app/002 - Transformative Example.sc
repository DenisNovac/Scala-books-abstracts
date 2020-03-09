import cats.data.OptionT
import cats.instances.list._
import cats.syntax.applicative._

type ListOption[A] = OptionT[List, A]

val result1: ListOption[Int] = OptionT(List(Option(10)))  // result1: ListOption[Int] = OptionT(List(Some(10)))
val result2: ListOption[Int] = 32.pure[ListOption]  // result2: ListOption[Int] = OptionT(List(Some(32)))


result1.flatMap { x =>
  result2.map { y=>
    x + y
  }
}  // res0: cats.data.OptionT[List,Int] = OptionT(List(Some(42)))








