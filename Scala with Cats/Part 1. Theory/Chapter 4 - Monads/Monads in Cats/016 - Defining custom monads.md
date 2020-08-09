# Определение собственных монад

Мы можем создавать собственные монады для любых типов, предоставляя имплементацию методов `pure`, `flatMap` и `tailRecM`.

Пример создания монады для Option:

```scala
import cats.Monad

import scala.annotation.tailrec


val optionMonad = new Monad[Option] {
  override def pure[A](opt: A): Option[A] = Some(opt)

  override def flatMap[A, B](opt: Option[A])(fn: A => Option[B]): Option[B] = opt flatMap fn

  @tailrec
  override def tailRecM[A, B](a: A)(fn: A => Option[Either[A, B]]): Option[B] =
    fn(a) match {
      case None => None
      case Some(Left(a1)) => tailRecM(a1)(fn)
      case Some(Right(b)) => Some(b)
    }
}
```

`tailRecM` - это оптимизация, используемая Cats для ограничения стека, занимаемого вложенными флатмапами. Этот метод должен рекурсивно вызывать сам себя пока результат fn не станет `Right`.

Если этот метод получился тейл-рекурсивным - Cats гарантирует стекобезопасность в рекурсивных ситуациях вроде фолдинга по огромным листам. Если он не тейл-рекурсивный - этих гарантий нет. Все встроенные в Cats монады имеют `tailRecM` с хвостовой рекурсией.

## Упражнение - Branching out Further with Monads

Написать монаду для бинарного дерева. 

```scala
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

```

flatMap пишется понятно. А вот код tailRecM довольно сложен в любом случае.

Не-хвостовой вариант написан исходя из простого следования типам (flatMap подходит для обеспечения соответствия типов).

Хвостовая рекурсия пишется гораздо сложнее.