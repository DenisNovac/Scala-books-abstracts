import cats.Monad
import cats.instances.option._
import cats.instances.list._

import scala.concurrent.{Await, Future}

val opt1 = Monad[Option].pure(3)
val opt2 = Monad[Option].flatMap(opt1)(a => Some(a + 2))
val opt3 = Monad[Option].map(opt2)(100 * _)

val list1 = Monad[List].pure(3)
val list2 = Monad[List].flatMap(List(1, 2, 3))(a => List(a, a * 10))
val list3 = Monad[List].map(list2)(a => a + 123)

val m = Monad[Option]


import scala.concurrent.ExecutionContext.Implicits.global
import cats.instances.future._

val f = Monad[Future]  // Could not find an instance of Monad for scala.concurrent.Future




import cats.syntax.applicative._

1.pure[Option]


import cats.syntax.functor._
import cats.syntax.flatMap._

def sumSquare[F[_]: Monad](a: F[Int], b: F[Int]): F[Int] =
  a.flatMap(x => b.map(y => x*x + y*y))


def coolerSumSquare[F[_]: Monad](a: F[Int], b: F[Int]): F[Int] = for {
  x <- a
  y <- b
} yield x*x + y*y

sumSquare(Option(3), Option(4))  // Some(25)
sumSquare(List(1,2,3), List(4))  // List(17, 20, 25)
coolerSumSquare(List(1,2,3), List(4))  // List(17, 20, 25)














