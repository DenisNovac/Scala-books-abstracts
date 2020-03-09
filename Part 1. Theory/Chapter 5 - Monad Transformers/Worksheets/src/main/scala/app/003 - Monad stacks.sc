import cats.data.OptionT
import cats.syntax.applicative._
import cats.instances.all._

import scala.concurrent.Future


// В конструктор типа с одним параметром
type ErrorOr[A] = Either[String, A]

// Финальный монадный стек
type ErrorOrOption[A] = OptionT[ErrorOr, A]


val a = 10.pure[ErrorOrOption]
val b = 32.pure[ErrorOrOption]
val c = a.flatMap(x => b.map(y => x + y))


import cats.data.EitherT
import cats.instances.future._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

type FutureEither[A] = EitherT[Future, String, A]
type FutureEitherOption[A] = OptionT[FutureEither, A]

val futureEitherOr: FutureEitherOption[Int] =
  for {
    a <- 10.pure[FutureEitherOption]
    b <- 32.pure[FutureEitherOption]
  } yield a + b

futureEitherOr.value

futureEitherOr.value.value