# Пример трансформации

Cats предоставляет трансформеры для многих монад. Они обозначаются суффиксом `T`: `EitherT`, `OptionT` и т.д. Например, `EitherT` служит для композиции `Either` с другими монадами.

Пример композиции Option с List:

```scala
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
```

Методы flatMap и map скомбинированы из List и Option в одну операцию.

Скомбинированные методы `map` и `flatMap` позволяют использовать обе монады без необходимости рекурсивно паковать и распаковывать значения. 
