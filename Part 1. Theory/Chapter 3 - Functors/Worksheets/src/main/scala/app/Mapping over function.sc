import cats.instances.function._
import cats.syntax.functor._

val func1: Int => Double =
  (x: Int) => x.toDouble

val func2: Double => Double =
  (y: Double) => y * 2

(func1 map func2)(1)  // копмозиция через map, работает только с синтаксисом Functor-а

(func1 andThen func2)(1)  // композиция через andThen (стандартная либа Scala)

func2(func1(1))  // композиция вручную
