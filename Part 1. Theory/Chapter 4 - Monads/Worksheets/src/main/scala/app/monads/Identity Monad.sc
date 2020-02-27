import cats.Monad

import cats.Id
import cats.syntax.functor._
import cats.syntax.flatMap._

def sumSquare[F[_]: Monad](a: F[Int], b: F[Int]): F[Int] =
    for {
      x <- a
      y <- b
    } yield x * x + y * y

val n: Int = sumSquare(1: Id[Int], 2: Id[Int])

val first = sumSquare(1: Id[Int], 2: Id[Int])
val second = sumSquare(43: Id[Int], 23: Id[Int])

for {
    x <- first
    y <- second
  } yield x + y

