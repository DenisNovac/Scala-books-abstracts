# Exercise 2.4 All set for Monoids

Какие моноиды и полугруппы можно определить для сетов?

```scala
trait Semigroup[A] {
  def combine(x: A, y: A): A
}

trait Monoid[A] extends Semigroup[A] {
  def empty: A
}

implicit def setUnionMonoid[A]: Monoid[Set[A]] =
    new Monoid[Set[A]] {
      def combine(a: Set[A], b: Set[A]): Set[A] = a.union(b)
      def empty                                 = Set.empty[A]
    }

implicit def setIntersectionSemigroup[A]: Semigroup[Set[A]] =
    new Semigroup[Set[A]] {

      def combine(a: Set[A], b: Set[A]) =
        a.intersect(b)
    }

implicit def symDiffMonoid[A]: Monoid[Set[A]] =
    new Monoid[Set[A]] {

      def combine(a: Set[A], b: Set[A]): Set[A] =
        (a.diff(b)).union(b.diff(a))
      def empty: Set[A] = Set.empty
    }

```