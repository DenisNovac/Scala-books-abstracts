import cats.Monoid
import cats.instances.string._
import cats.Semigroup

Monoid[String].combine("Hello ", "there")
Monoid[String].combine("General Kenobi!", Monoid[String].empty)

// Моноиды - это подтип полугрупп. Если не нужен пустой элемент, то можно использовать их

Semigroup[String].combine("Hello ", "there")


import cats.instances.option._
import cats.instances.int._

val a = Option(22)
val b = Option(20)

Monoid[Option[Int]].combine(a,b)


import cats.syntax.semigroup._
a |+| b
1 |+| 2 |+| Monoid[Int].empty
