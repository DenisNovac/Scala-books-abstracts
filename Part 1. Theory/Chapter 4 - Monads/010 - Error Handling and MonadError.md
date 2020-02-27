# Обработка ошибок и MonadError

Cats предоставляет дополнительный тайпкласс `MonadError`. Он абстрагирует Either-подобные типы данных, которые используются для обработки ошибок. `MonadError` предоставляет дополнительные операции для выбрасывания и обработки ошибок.

`MonadError` упрощённо выглядит вот так:

```scala
trait MonadError[F[_], E] extends Monad[F] {
  // Lift an error into the `F` context:
  def raiseError[A](e: E): F[A]

  // Handle an error, potentially recovering from it:
  def handleError[A](fa: F[A])(f: E => A): F[A]

  // Test an instance of `F`,
  // failing if the predicate is not satisfied:
  def ensure[A](fa: F[A])(e: E)(f: A => Boolean): F[A]
}
```

Где F - это тип монады, E - тип ошибки в монаде.

На самом деле, `MonadError` расширяет `ApplicativeError`. Но это потом.

## Использование

```scala
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
// res0: ErrorOr[ErrorOr[String]] = Right(Right(It's ok))
```

Ещё есть метод ensure:

```scala
val success = monadError.pure(42)
monadError.ensure(success)("Number too low")(_ > 1000)  // res1: ErrorOr[Int] = Left(Number too low)
```

## Синтаксис для MonadError

Синтаксис для `raiseError` и `handleError` предоставляют пакеты `cats.syntax.applicativeError` и `cats.syntax.monadError`:

```scala
import cats.syntax.applicativeError._
import cats.syntax.applicative._
import cats.syntax.monadError._

val failureAgain = "Badness".raiseError[ErrorOr, Int]
val successAgain = 52.pure[ErrorOr]
success.ensure("Number too Low")(_ > 0)
```

IDEA на этом моменте совсем сломалась и не смогла.

## Инстансы MonadError

Cats предоставляет инстансы MonadError для многих типов данных, включа `Either`, `Future` и `Try`. 

```scala
import scala.util.Try
import cats.instances.try_._

val exn: Throwable =
  new RuntimeException("Hello there")

exn.raiseError[Try, Int]  // res3: scala.util.Try[Int] = Failure(java.lang.RuntimeException: Hello there)
```

