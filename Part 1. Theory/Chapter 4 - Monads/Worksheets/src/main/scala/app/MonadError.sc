import cats.MonadError
import cats.instances.either._

type ErrorOr[A] = Either[String, A]


val monadError = MonadError[ErrorOr, String]

val success = monadError.pure(42)
val failure = monadError.raiseError("Badness")
val anotherFailure = monadError.raiseError("Oops")

monadError.handleError(failure) {
  case "Badness" =>
    monadError.pure("It's ok")

  case other =>
    monadError.raiseError("It's not ok")
}


monadError.ensure(success)("Number too low")(_ > 1000)

import cats.syntax.applicativeError._
import cats.syntax.applicative._
import cats.syntax.monadError._

val failureAgain = "Badness".raiseError[ErrorOr, Int]
val successAgain = 52.pure[ErrorOr]
success.ensure("Number too Low")(_ > 0)

import scala.util.Try
import cats.instances.try_._

val exn: Throwable =
  new RuntimeException("Hello there")

exn.raiseError[Try, Int]

