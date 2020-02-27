# Either Monad

В Scala 2.11 Either нельзя было использовать в for-comprehensions напрямую. В Scala 2.12 было принято решение, что правая сторона представляет "успех", а левая - "неудачу", поэтому были введены методы map и flatMap:

```scala
import cats.syntax.either._ // for map and flatMap

val either1 = Right(10) // either1: scala.util.Right[Nothing,Int] = Right(10)
val either2 = Right(32) // either2: scala.util.Right[Nothing,Int] = Right(32)

for {
  a <- either1
  b <- either2
} yield a + b  // res0: scala.util.Either[String,Int] = Right(42)
```

Только ради for смысла делать импорт в Scala 2.12 нет, но он и ничего не сломает.

## Создание инстансов

Инстансы в синтаксисе Cats можно создавать благодаря методам тайпкласса:

```scala
val a = 10.asRight[String]  // String - тип левой части, без него будет Nothing
// a: Either[String,Int] = Right(10)
val b: Either[String, Int] = 24.asRight  // тут его можно опустить
// b: Either[String,Int] = Right(24)

for {
  first <- a
  second <- b
} yield first+second  // res1: scala.util.Either[String,Int] = Right(34)
```

В отличие от `apply` (например `Right(10)`) - `asRight` и `asLeft` всегда возвращают именно Either (хотя можно и через apply: `val either2: Either[String, Int] = Right(32)`).

Например, это полезно, когда работаешь одновременно с Left и Right. Сами по себе они несовместимы, а вот Either универсален.

```scala
def countPositive(nums: List[Int]) =
  nums.foldLeft(Right(0)) { (accumulator, num) =>
    if(num > 0) {
      accumulator.map(_ + 1)
    } else {
      Left("Negative. Stopping!")
    }
}
// <console>:21: error: type mismatch;
// found
// required: scala.util.Right[Nothing,Int]: scala.util.Either[Nothing,Int]
//accumulator.map(_ + 1)

// <console>:23: error: type mismatch;
// found // required: scala.util.Right[Nothing,Int]: scala.util.Left[String,Nothing]
// Left("Negative. Stopping!")

```

Т.к. `foldLeft` был инициализирован через `Right` - вся конструкция ожидает `Right`. А вот так `nums.foldLeft(0.asRight[String])` - заработает.

## Дополнительный синтаксис

Тайпкласс добавляет расширяющие методы в компаньон Either. Это методы `catchOnly` и `catchNonFatal` для перехвата эксепшенов в виде Either и методы для создания Either из других типов данных (option, try):


```scala
Either.catchOnly[NumberFormatException]("foo".toInt) // res2: Either[NumberFormatException,Int] = Left(java.lang.NumberFormatException: For input string: "foo")


Either.catchNonFatal(sys.error("Badness"))  // res3: Either[Throwable,Nothing] = Left(java.lang.RuntimeException: Badness)

Either.fromTry(scala.util.Try("foo".toInt))  // res4: Either[Throwable,Int] = Left(java.lang.NumberFormatException: For input string: "foo")

Either.fromOption[String, Int](None, "None")  // res5: Either[String,Int] = Left(None)
Either.fromOption(Some(12), "None") // res6: Either[String,Int] = Right(12)
```

