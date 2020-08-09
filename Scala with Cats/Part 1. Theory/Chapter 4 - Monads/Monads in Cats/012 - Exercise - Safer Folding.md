# Exercise 4.6.5 - Safer Folding using Eval

Нативная имплементация `foldRight` не стекобезопасна. Исправить это:

```scala
def foldRight[A, B](as: List[A], acc: B)(fn: (A, B) => B): B =
  as match {
    case head :: tail =>
      fn(head, foldRight(tail, acc)(fn))
    case Nil =>
      acc
  }



val l = (1 to 50000).toList

foldRight(l, 0)((i, acc) => acc + i)  // java.lang.StackOverflowError
```

Ответ:

```scala
import cats.Eval

def foldRightSs[A, B](as: List[A], acc: B)(fn: (A, B) => B): Eval[B] =
  as match {
    case head :: tail => for {
      e <- Eval.defer(foldRightSs(tail, acc)(fn))
    } yield fn(head, e)

    case Nil =>
      Eval.now(acc)
  }


foldRightSs(l, 0)((i, acc) => acc + i).value

```


Не получится сделать рекурсию следующим образом:

```scala
fn(head, Eval.defer(foldRightSs(tail, acc)(fn)))
```

Это связано с тем, что fn имеет определённую сигнатуру: `(fn: (A, B) => B)`, а мы при таком раскладе будем передавать `Eval[B]`.

В учебнике переопределили сигнатуру входной функции:

```scala
def foldRightEval[A, B](as: List[A], acc: Eval[B])(fn: (A, Eval[B]) => Eval[B]): Eval[B] =
    as match {
      case head :: tail =>
        Eval.defer(fn(head, foldRightEval(tail, acc)(fn)))
      case Nil =>
        acc
    }
```
