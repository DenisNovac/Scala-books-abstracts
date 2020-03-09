# Монадные трансформеры в Cats

Каждый трансформер - это обособленный тип данных в `cats.data`.

Что нужно изучить для понимания Cats:

- Доступные классы-трансформеры;
- Как строить стеки монад из трансформеров;
- Как конструировать инстансы монадных стеков;
- Как получать доступ к запакованным монадам в стеке.

## Классы

Классы в пакете `cats.data`:

- OptionT;
- EitherT;
- *ReaderT*;
- WriterT;
- StateT;
- IdT.

Ранее мы сталкивались с `Kleisli` во время работы с Reader в главе 4. Так вот ReaderT это тайпальяс на Kleisli. 

## Построение монадных стеков

Все монадные трансформеры действуют по одинаковым законам. 

- Трансформер - это внутренняя монада в стеке.
- Первый параметр трансформера - внешняя монада.

```scala
import cats.data.OptionT
import cats.instances.list._
import cats.syntax.monoid._

val l = OptionT.pure(1) |+| OptionT.pure(2)  // l: cats.data.OptionT[[+A]List[A],Int] = OptionT(List(Some(1), Some(2)))
```

`OptionT[List, A]` становится листом `List[Option[A]]`. Получается, что монадные стеки строятся изнутри-наружу. 

Многие монады и все трансформеры имеют как минимум два типа-параметра, так что мы часто определяем тайпальясы для промежуточных шагов.

Например: хотим обернуть Option в Either. Option - это внутренний тип, поэтому используем OptionT. Тогда Either должен быть внутренним параметром типа. Но сам Either тоже имеет два параметра, а монады имеют только один. Тут нужен тайпальяс для конверции конструктора типа в нужную форму:

```scala
import cats.data.OptionT
import cats.syntax.applicative._
import cats.instances.all._


// В конструктор типа с одним параметром
type ErrorOr[A] = Either[String, A]

// Финальный монадный стек
type ErrorOrOption[A] = OptionT[ErrorOr, A]


val a = 10.pure[ErrorOrOption]
val b = 32.pure[ErrorOrOption]
val c = a.flatMap(x => b.map(y => x + y))  // c: cats.data.OptionT[ErrorOr,Int] = OptionT(Right(Some(42)))
```

Всё становится ещё более запутанным, когда мы хотим сложить три или более монад.

Например, Future над Either над Option. Теперь EitherT имеет три параметра:

- F[_] - внешняя монада;
- E - error тайп для Either;
- A - результат Either.

Теперь нужен альяс уже для самого EitherT:

```scala
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

futureEitherOr.value  // res0: FutureEither[Option[Int]] = EitherT(Future(Success(Right(Some(42)))))
```

Теперь стек монад содержит три монады и for проходит через три слоя абстракции.

`value` позволяет распаковывать стек:

```scala
futureEitherOr.value  // res0: FutureEither[Option[Int]] = EitherT(Future(Success(Right(Some(42)))))

futureEitherOr.value.value  // res1: scala.concurrent.Future[Either[String,Option[Int]]] = Future(Success(Right(Some(42))))
```

Каждый вызов `value` распаковывает один монадный трансформер. 
