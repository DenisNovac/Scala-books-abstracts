

def foldRight[A, B](as: List[A], acc: B)(fn: (A, B) => B): B =
    as match {
      case head :: tail =>
        fn(head, foldRight(tail, acc)(fn))
      case Nil =>
        acc
    }

val l = (1 to 50000).toList

//foldRight(l, 0)((i, acc) => acc + i)  // java.lang.StackOverflowError

import cats.Eval

def foldRightSs[A, B](as: List[A], acc: B)(fn: (A, B) => B): Eval[B] =
    as match {
      case head :: tail =>
        for {
          e <- Eval.defer(foldRightSs(tail, acc)(fn))
        } yield fn(head, e)

      case Nil =>
        Eval.now(acc)
    }

foldRightSs(l, 0)((i, acc) => acc + i).value

