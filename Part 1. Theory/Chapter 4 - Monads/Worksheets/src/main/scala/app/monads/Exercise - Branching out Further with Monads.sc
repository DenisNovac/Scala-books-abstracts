sealed trait Tree[+A]
final case class Branch[A](left: Tree[A], right: Tree[A]) extends Tree[A]
final case class Leaf[A](value: A) extends Tree[A]

def branch[A](left: Tree[A], right: Tree[A]): Tree[A] =
    Branch(left, right)

def leaf[A](value: A): Tree[A] =
    Leaf(value)

import cats.Monad

import scala.annotation.tailrec

val treeMonad = new Monad[Tree] {
    override def pure[A](x: A): Tree[A] = leaf(x)

    override def flatMap[A, B](fa: Tree[A])(f: A => Tree[B]): Tree[B] = fa match {
      case Leaf(a)      => f(a)
      case Branch(a, b) => Branch(flatMap(a)(f), flatMap(b)(f))
    }

    /** Не-хвостовая рекурсия */
    override def tailRecM[A, B](a: A)(fn: A => Tree[Either[A, B]]): Tree[B] =
      flatMap(fn(a)) {
        case Left(value)  => tailRecM(value)(fn)
        case Right(value) => Leaf(value)
      }
  }

