/**
  * Упражнение Equality, Liberty, and Felinity
  * */
final case class Cat(name: String, age: Int, color: String)

val cat1 = Cat("Barbossa", 42, "Black")
val cat2 = Cat("Phasma", 32, "White")

val optionCat1 = Option(cat1)
val optionCat2 = Option.empty[Cat]

// Сравнить котов

import cats.Eq
import cats.instances.string._
import cats.instances.int._
import cats.syntax.eq._

implicit val catsEq: Eq[Cat] = Eq.instance[Cat]({
  (cat1, cat2) =>
    cat1.name === cat2.name &&
    cat1.age === cat2.age &&
    cat1.color === cat2.color
})




cat1 === cat2
cat1 === cat1

import cats.instances.option._

optionCat1 === optionCat2


