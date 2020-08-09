# Примеры функторов

Метод `map` на обычных коллекциях Scala вычисляется без промедления (**eagerly**). Однако идея последовательных вычислений более обобщённая. Посмотрим на другие функторы, в которых паттерн работает иначе.

## Futures

`Future` - это функтор который упорядочевает асинхронные вычисления в очереди и приминяет их по очереди (когда предыдущее вычисление заканчивается).

При работе с `Future` у нас нет гарантий касательно его внутреннего состояния. Обёрнутое вычисление может в данный момент выполняться, быть завершённым или отменённым. 

Если Future завершен - функция `map` будет вызвана сразу же. Если нет - внутренний тред пул помещает вызов функции в очередь и вызывает его позже. Мы не знаем, *когда* функции будут вызваны, но мы знаем, что они будут всегда вызваны после выполнения Future. Получается, Future предоставляет то же поведение, что `List`, `Option` и `Either`:

```scala
import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._


val future: Future[String] =
  Future(123).
    map(n => n + 1).
    map(n => n * 2).
    map(n => n + "!")

Await.result(future, 1.second)  // res0: String = 248!
```

> Future из Scala не лучший пример функционального программирования, ведь они не обладают прозрачностью ссылок. Future всегда вычисляют и кешируют результат и не дают возможности настроить поведения. Это значит, что мы можем получить непредвиденные результаты, если завернём в них эффектные операции.

> Выражение называется ссылочно прозрачным, если его можно заменить соответствующим значением без изменения поведения программы. 

Например:

```scala
import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.util.Random

val future1 = {
  // fixed seed
  val r = new Random(0L)
  
  // side-effect заключается в переключении на случайный номер
  val x = Future(r.nextInt)
  
  for {
    a <- x
    b <- x
  } yield (a,b)
}

val future2 = {
  val r = new Random(0L)
  for {
    a <- Future(r.nextInt)
    b <- Future(r.nextInt)
  } yield (a,b)
}


Await.result(future1, 1.second)  // res0: (Int, Int) = (-1155484576,-1155484576)
Await.result(future2, 1.second)  // res1: (Int, Int) = (-1155484576,-723955400)

```

> В теории, мы ожидаем, что result1 и result2 будут равны. Но future2 вызывает nextInt дважды, а эта эффектная операция даёт разные ответы при каждом вызове.

> Ещё одна проблема Future - они начинают выполнение немедленно и не дают возможности отложить выполнение.