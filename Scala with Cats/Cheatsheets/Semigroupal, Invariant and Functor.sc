import cats.{Functor, Invariant, Semigroupal}


/** Семигрупалл позволяет соединить два контекста в один */

case class Cat[A](id: A)

class CatSemigroupal extends Semigroupal[Cat] {
  override def product[A, B](fa: Cat[A], fb: Cat[B]): Cat[(A,B)] = Cat( (fa.id, fb.id) )
}

implicit val semi = new CatSemigroupal

import cats.syntax.semigroupal._

Cat("Jonny").product(Cat("Samuel"))  //  Cat((Jonny,Samuel))

// Складывать два контекста легко

Semigroupal[Cat].product(Cat("Sam"), Cat("John"))


// Для большего количества контекстов нужен инстанс Invariant в скопе

class CatInvariant extends Invariant[Cat] {
  override def imap[A, B](fa: Cat[A])(f: A => B)(g: B => A): Cat[B] = Cat(f(fa.id))
}

class CatFunctor extends Functor[Cat] {
  override def map[A, B](fa: Cat[A])(f: A => B): Cat[B] = Cat(f(fa.id))
}

//implicit val inv = new CatInvariant
implicit val fun = new CatFunctor  // Функтор подходит туда же, где требуется Invariant, ведь это его наследник

val l = Semigroupal.tuple3(Cat("sammy"), Cat("johny"), Cat("stan"))  //  Cat((sammy,johny,stan))