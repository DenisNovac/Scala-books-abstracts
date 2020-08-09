import cats.Foldable
import cats.instances.list._

val ints = List(1, 2, 3)
Foldable[List].foldLeft(ints, 0)(_ + _)  // 6

import cats.instances.option._

val maybeInt = Option(123)
Foldable[Option].foldLeft(maybeInt, 10)(_ + _)  // 133


/** foldRight описан в монаде Eval для стекобезопасности */

/** Stream-ы депрекейтнуты, теперь это LazyList */
/** Мне не удалось вызвать оверфлоу стека, только хипа */
import scala.collection.immutable.LazyList
def bigData = (1L to 1_000_000L).to(LazyList)
//bigData.foldRight(0L)(_ + _)  // java.lang.OutOfMemoryError: Java heap space

import cats.Eval
import cats.instances.lazyList._

val eval: Eval[Long] =
  Foldable[LazyList].
    foldRight(bigData, Eval.now(0L)) { (num, eval) =>
      eval.map(_ + num)
    }

eval.value

/**
 * Как написано в книге - Stream раньше не был стекобезопасным.
 * Теперь это LazyList и, видимо, теперь он безопасен
 * */


Foldable[Option].nonEmpty(Option(42))
Foldable[List].find(List(1,2,3))(_ % 2 == 0)

import cats.instances.list._
import cats.instances.int._

Foldable[List].combineAll(List(1,2))  // 3

import cats.instances.string._

Foldable[List].foldMap(List(1,2,3))(_.toString)  // 123

/** Композиция Foldable */
import cats.instances.vector._
val ints = List(Vector(1,2,3), Vector(4,5,6))
(Foldable[List] compose Foldable[Vector]).combineAll(ints)  // 21



import cats.syntax.foldable._

List(1,2).combineAll  // 3
List(1,2).foldMap(_.toString) // 12















