# Функторы в Cats

Функтор - это тоже тайпкласс. Инстансы получаются счерез метод `Functor.apply` в объекте-компаньоне.

```scala
import cats.Functor
import cats.instances.list._
import cats.instances.option._

val list1 = List(1, 2, 3)
/** Мап принимает начальный аргумент и функцию-преобразователь */
val list2 = Functor[List].map(list1)(_ * 2)  // list2: List[Int] = List(2, 4, 6)

val option1 = Option(123)

/** Функтор сам по себе не является типом ответа */
val option2 = Functor[Option].map(option1)(_.toString)  // Option[String]
```

`Functor[F]` также предоставляет метод `lift`, который конвертирую функцию типа `A => B` в функцию функтора типа `F[A] => F[B]`:

```scala
val func = (x: Int) => x + 1  // func: Int => Int = <function>9479dba
val liftedFunc = Functor[Option].lift(func)  // liftedFunc: Option[Int] => Option[Int] = cats.Functor<function>538d2c3
liftedFunc(Option(1))  // res0: Option[Int] = Some(2)
//liftedFunc(Option("meeh"))  // type mismatch, required int

// подойдёт любой тип с дыркой
val liftedFuncList = Functor[List].lift(func)
liftedFuncList(List(1,2,3,4))  // 2,3,4,5
```

При этом внутренние требования функции `func` остаются, это только обёртка (нельзя передать "meeh").


# Синтаксис функторов

Главный метод функторов - это `map`. Сложно продемонстрировать его использование на List и Option, ведь они имеют встроенные `map` и компилятор Scala всегда предпочитает встроенные методы вместо расширительных. Придётся придумать воркараунды.

Посмотрим на маппинг на функциях. `Function1` в Scala не имеет встроенного метода `map` (он назван `andThen` вместо этого):

```scala
import cats.instances.function._
import cats.syntax.functor._   // отсюда берётся map

val func1 = (a: Int) => a + 1
val func2 = (a: Int) => a * 2
val func3 = (a: Int) => a + "!"
val func4 = func1.map(func2).map(func3)

func4(123) // 248!
```

Таким образом функции композируются без явной передачи друг в друга.

## Пример использования типа высшего порядка

```scala
/** Абстрагируемся вообще и будем принимать только тип с дыркой */

def doMath[F[_]](start: F[Int])(implicit functor: Functor[F]): F[Int] =
  start.map(n => n + 1 * 2)

doMath(Option(20))  // res3: Option[Int] = Some(22)
doMath(List(1, 2, 3))  // res4: List[Int] = List(3, 4, 5)
```

Т.е. ранее мы импортировали инстансы option и list - мы можем вызывать такую функцию без оглядки на имплиситный аргумент-функтор.

Чтобы понять, как это работает, посмотрим на упрощённый код синтаксиса функтора:

```scala
implicit class FunctorOps[F[_], A](src: F[A]) {
  def map[B](func: A => B)(implicit functor: Functor[F]): F[B] =
    functor.map(src)(func)
}
```

Получается, синтаксис функтора ожидает три типа - тип с дыркой, тип дырки, тип возврата функции.

Компилятор использует этот класс для инжекции метода `map` туда где нет встроенного. 

Использование синтаксиса предполагает наличие инстанса `Functor[F]`. 


## Определение инстансов руками

Для Option имплементация тривиальна - просто вызов встроенного в Option map.

```scala
implicit val optionFunctor: Functor[Option] =
  new Functor[Option] {
    override def map[A, B](value: Option[A])(func: A => B): Option[B] =
      value.map(func)
  }
```

Иногда нужно передавать зависимости в наши инстансы. Например, если мы хотим создать кастомный инстанс функтора для Future, нам нужно передавать туда ExecutionContext:

```scala
implicit val futureFunctor: Functor[Future] = new Functor[Future] {
  override def map[A, B](value: Future[A])(func: A => B): Future[B] = value.map(func) (...) // Error: Cannot find an implicit ExecutionContext. You might pass
}

```

Но мы можем поступить так:

```scala
implicit def futureFunctor(implicit ec: ExecutionContext): Functor[Future] = new Functor[Future] {
  override def map[A, B](value: Future[A])(func: A => B): Future[B] = value.map(func)
}
```

Когда мы запросим функтор для фьючи - компилятор найдёт созданный имплиситный `futureFunctor` и начнёт рекурсивный поиск `ExecutionContext`, который есть в области видимости, если передать его в def:

```scala
// We write this:
Functor[Future]

// The compiler expands to this first:
Functor[Future](futureFunctor)

// And then to this:
Functor[Future](futureFunctor(executionContext))
```
