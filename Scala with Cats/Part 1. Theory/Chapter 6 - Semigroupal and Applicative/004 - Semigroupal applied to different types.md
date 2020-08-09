# Применение Semigroupal с разными типами


```scala
/** Future */
import cats.Semigroupal
import cats.instances.future._ // for Semigroupal
import scala.concurrent._
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

/** Они начинают вычисляться в момент, когда мы их создаём */
/** Поэтому на момент вызова product они уже вычисляют результаты */
  val futurePair = Semigroupal[Future].product(Future("Hello"), Future(123))

Await.result(futurePair, 1.second) // res0: (String, Int) = (Hello,123)

import cats.syntax.apply._

case class Cat(
    name: String,
    year: Int,
    food: List[String]
)

val futureCat = (
    Future("Garfield"),
    Future(1978),
    Future(List("Lasagne"))
  ).mapN(Cat.apply)

Await.result(futureCat, 1.second) // res1: Cat = Cat(Garfield,1978,List(Lasagne))
```

А вот сложение List и Either даёт странные результаты:

```scala
/** List */
import cats.instances.list._
Semigroupal[List].product(List(1, 2), List(3, 4))
// res2: List[(Int, Int)] = List((1,3), (1,4), (2,3), (2,4))
/** Неожиданное поведение - получили комбинации вместо одного листа */
```

Может быть, product от Either позволит реализовать желаемое поведение накопления ошибок?

```scala
/** Either */
import cats.instances.either._

/** Мы опять получаем fail-fast поведение, хотя ожидалось, что product сможет
    * накопить сообщения об ошибках */
  type ErrorOr[A] = Either[Vector[String], A]
Semigroupal[ErrorOr].product(
    Left(Vector("Error 1")),
    Left(Vector("Error 2"))
  )
// res3: ErrorOr[(Nothing, Nothing)] = Left(Vector(Error 1))
```

Даже так Either останавливается на первой ошибке.

## Semigroupal применение к Монадам

Причина странных результатов для `Either` и `List` кроется в том, что они оба являются **Монадами**. Монады Cats расширяют Semigroupal. Чтобы предоставлять надёжную постоянную семантику, Монады реализуют `product` в понятиях `map` и `flatMap`. 

Кроме того, Future тоже вычислилась с оговорками. `product` работает через `flatMap`, а он вычисляет последовательно. Нам *показалось*, что вычисление произошло параллельно, ведь оно произошло *до вызова* `product`.

Зачем тогда нужен Semigroupal? Он применим там, где не нужны монады. Это позволяет создавать свои имплементации `product` для нужных типов. 


## Exercise: Product of Monads

Имплементировать `product` в терминах `flatMap`:

```scala
/** 6.3.1.1 Exercise: The Product of Monads */
import cats.Monad
import cats.syntax.flatMap._  // flatMap
import cats.syntax.functor._ // map

def product[M[_]: Monad, A, B](x: M[A], y: M[B]): M[(A, B)] =
  x.flatMap(xx => y.map(yy => (xx, yy)))

def product[M[_]: Monad, A, B](x: M[A], y: M[B]): M[(A, B)] = for {
  xx <- x
  yy <- y
} yield (xx, yy)
```

