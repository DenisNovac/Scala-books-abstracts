# Монады в Cats

Опять рассмотрим тайпкласс, инстансы и синтаксис.

Монады расположены в `cats.Monad`. Они расширяют два других класса: `FlatMap` (предоставляет flatMap) и `Applicative` (предоставляет pure). Applicative расширяет Functor, а в Functor есть `map`. Поэтому мы можем использовать for-comprehension.

```scala
import cats.Monad
import cats.instances.option._
import cats.instances.list._

val opt1 = Monad[Option].pure(3)  // opt1: Option[Int] = Some(3)
val opt2 = Monad[Option].flatMap(opt1)(a => Some(a + 2))
val opt3 = Monad[Option].map(opt2)(100 * _)

val list1 = Monad[List].pure(3)  // list1: List[Int] = List(3)
val list2 = Monad[List].flatMap(List(1, 2, 3))(a => List(a, a * 10))  // list2: List[Int] = List(1, 10, 2, 20, 3, 30)
val list3 = Monad[List].map(list2)(a => a + 123)  // list3: List[Int] = List(124, 133, 125, 143, 126, 153)
```

Монада умеет всё, что умеет функтор и вообще много чего умеет:

https://typelevel.org/cats/api/cats/Monad.html


## Инстансы

Инстансы лежат в `cats.instances` как обычно.

Среди прочего там есть монада для `Future`. У неё есть особенность - требовательность к контексту. Методы `pure` и `flatMap` сами по себе не могут принимать `implicit ExecutionContext ec`, т.к. это не часть сигнатуры. Поэтому монада для Future при создании требует этот контекст в скоупе:

```scala
import scala.concurrent.ExecutionContext.Implicits.global
import cats.instances.future._

val f = Monad[Future]
```

## Синтаксис

Синтаксис монад может быть получен из пакетов:

- `cats.syntax.flatMap` - flatMap;
- `cats.syntax.functor` - map;
- `cats.syntax.applicative` - pure.

Или:

- `cats.implicits` для всего.

### Примеры синтаксиса

Applicative:

```scala
import cats.syntax.applicative._

1.pure[Option]
```

Тяжело продемонстрировать flatMap и map на монадах Scala (там они уже есть), поэтому напишем обобщённую функцию для любых монад:


```scala

import cats.syntax.functor._
import cats.syntax.flatMap._

def sumSquare[F[_]: Monad](a: F[Int], b: F[Int]): F[Int] =
  a.flatMap(x => b.map(y => x*x + y*y))


sumSquare(Option(3), Option(4))  // Some(25)
sumSquare(List(1,2,3), List(4))  // List(17, 20, 25)
```

Этот код можно переписать на for-comprehensions, ведь a и b - монады по условию:

```scala
def coolerSumSquare[F[_]: Monad](a: F[Int], b: F[Int]): F[Int] = for {
  x <- a
  y <- b
} yield x*x + y*y
```

