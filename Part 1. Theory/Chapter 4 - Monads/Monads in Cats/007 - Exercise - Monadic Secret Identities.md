# Exercise 4.3.1 - Monadic Secret Identities

Создать pure, map и flatMap для Id руками.

```scala
import cats.Id

def pure[A](value: A): Id[A] = value

def map[A, B](initial: Id[A])(func: A => B): Id[B] = func(initial)

def flatMap[A, B](initial: Id[A])(func: A => Id[B]): Id[B] = func(initial)


val p = pure(1)  // p: cats.Id[Int] = 1
val m = map(p)(value => value+123)  // m: cats.Id[Int] = 124
val f = flatMap(m)(value => 23)  // f: cats.Id[Int] = 23

```

По сути value и initial всегда обычные данные (Id - это просто альяс) и никаких сложностей в реализации просто нет.

Каждый тайпкласс позволяет избежать дополнительных описаний. В случае с Id - это вообще обычные значения, поэтому map и flatMap одинаковы. 
