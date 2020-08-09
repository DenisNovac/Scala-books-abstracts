import cats.Functor
import cats.instances.list._
import cats.instances.option._

val list1 = List(1, 2, 3)

/** Мап принимает начальный аргумент и функцию-преобразователь */
  val list2 = Functor[List].map(list1)(_ * 2) // List[Int]

val option1 = Option(123)

/** Функтор сам по себе не является типом ответа */
  val option2 = Functor[Option].map(option1)(_.toString) // Option[String]

/** Lift позволяет обернуть функцию в некоторый тип F с дыркой */
  val func = (x: Int) => x + 1
val liftedFunc = Functor[Option].lift(func)
liftedFunc(Option(1))
val liftedFuncList = Functor[List].lift(func)
liftedFuncList(List(1, 2, 3, 4)) // 2,3,4,5
//liftedFunc(Option("meeh"))  // type mismatch, required int



/** Синтаксис предоставляет интерфейс тайпкласса map для инстансов */
import cats.instances.function._
import cats.syntax.functor._ // отсюда берётся map

val func1 = (a: Int) => a + 1
val func2 = (a: Int) => a * 2
val func3 = (a: Int) => a + "!"
val func4 = func1.map(func2).map(func3)

func4(123) // 248!




/** Абстрагируемся вообще и будем принимать только тип с дыркой */

  def doMath[F[_]](start: F[Int])(implicit functor: Functor[F]): F[Int] =
    start.map(n => n + 1 * 2)

doMath(Option(20))
doMath(List(1, 2, 3))

