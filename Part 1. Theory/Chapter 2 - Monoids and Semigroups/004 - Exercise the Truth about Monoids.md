# Упражнение 2.3 - The truth about monoids

Как много моноидов можно определить для Boolean? 

Использовать для начала:

```scala
trait Semigroup[A] {
  def combine(x: A, y: A): A
}

trait Monoid[A] extends Semigroup[A] {
  def empty: A
}

object Monoid {

  def apply[A](implicit monoid: Monoid[A]) =
    monoid
}
```

```scala
val boolMonoidOr: Monoid[Boolean] = new Monoid[Boolean]() {
  override def empty = false  // нейтральный элемент

  override def combine(x: Boolean, y: Boolean) = x | y
}

val boolMonoidAnd: Monoid[Boolean] = new Monoid[Boolean]() {
  override def empty = true  // нейтральный элемент

  override def combine(x: Boolean, y: Boolean) = x & y
}



boolMonoidOr.combine(false, boolMonoidOr.empty)  // false
boolMonoidOr.combine(true, boolMonoidOr.empty)  // true


boolMonoidAnd.combine(false, boolMonoidAnd.empty)  // false
boolMonoidAnd.combine(true, boolMonoidAnd.empty)  // true
```


В книге определили ещё два неочевидных:

```scala
implicit val booleanEitherMonoid: Monoid[Boolean] =
    new Monoid[Boolean] {

      def combine(a: Boolean, b: Boolean) =
        (a && !b) || (!a && b)
      def empty = false
    }

implicit val booleanXnorMonoid: Monoid[Boolean] =
    new Monoid[Boolean] {

      def combine(a: Boolean, b: Boolean) =
        (!a || b) && (a || !b)
      def empty = true
    }

```

