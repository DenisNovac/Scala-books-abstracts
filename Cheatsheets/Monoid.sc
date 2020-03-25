import cats.kernel.{Monoid, Semigroup}

case class Cat(name: String)

/** Полугруппа позволяет складывать */
class CatSemigroupInstance extends Semigroup[Cat] {
  override def combine(x: Cat, y: Cat) = Cat(x.name + " and " + y.name)
}

/** Моноид имеет пустой элемент */
class CatMonoidInstance extends Monoid[Cat] {
  override def empty = Cat("")
  override def combine(x: Cat, y: Cat) = Cat(x.name + " and " + y.name)
}


import cats.syntax.semigroup._

// Инстанс нужно сначала создать
implicit val catMonoidInstance = new CatMonoidInstance()


// не работает напрямую, т.к. в Monoid есть те же методы. В этой ситуации непонятно, какой имплисит взять
// Отдельно от Monoid будет работать
// implicit val catSemigroupInstance = new CatSemigroupInstance


Cat("John") |+| Cat("Sam")  // синтаксис полугруппы
Cat("John") |+| Cat("Sam") |+| Monoid[Cat].empty  // синтаксис моноида

