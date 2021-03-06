import cats.Functor
import cats.syntax.functor._

sealed trait Tree[+A]

final case class Branch[A](left: Tree[A], right: Tree[A])
  extends Tree[A]

final case class Leaf[A](value: A) extends Tree[A]


implicit val treeFunctor: Functor[Tree] = new Functor[Tree] {
  override def map[A, B](fa: Tree[A])(f: A => B): Tree[B] = fa match {
    case Leaf(x) => Leaf(f(x))
    case Branch(l, r) => Branch(map(l)(f), map(r)(f))
  }
}



val simple_tree: Tree[Int] = Branch[Int](Leaf(1), Leaf(2))
simple_tree.map(i => i*23)

val hardcore_tree: Tree[Int] = Branch[Int](Branch(Leaf(34), Leaf(43)), Leaf(3))
hardcore_tree.map(i => i-30)
