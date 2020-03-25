import cats.Functor

case class Cat[A](id: A)

// Функтор позволяет добавить типу метод map
class CatFunctorInstance extends Functor[Cat] {
  override def map[A, B](fa: Cat[A])(f: A => B): Cat[B] = Cat(f(fa.id))
}

implicit val catFunctorInstance = new CatFunctorInstance

import cats.syntax.functor._

// map
Cat("Jonny").map(_.toLowerCase) // Cat(jonny)
Cat(47).map(_ => 42) // Cat(42)

// Ещё Функтор умеет создавать функции для оборачивания
val to42 = catFunctorInstance.lift[Int, Int](_ + 42) // Cat[Int] => Cat[Int]
to42(Cat(0)) // Cat(42)


var counter = 0
val removeNameMakeNumber = catFunctorInstance.lift[String, Int]{ name =>
  counter = counter + 1
  counter
}

removeNameMakeNumber(Cat("Garfield"))  // Cat(1)
removeNameMakeNumber(Cat("Stan"))  // Cat(2)

