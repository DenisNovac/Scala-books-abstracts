# Exercise 4.1.2: Getting Func-y

Кажадя монада это функтор. Мы можем определить `map` для любой монады:

```scala
trait Monad[F[_]] {
  def pure[A](a: A): F[A]
  def flatMap[A, B](value: F[A])(func: A => F[B]): F[B]
  def map[A, B](value: F[A])(func: A => B): F[B] = ???
}
```

Определите `map` сами.

```scala
def map[A, B](value: F[A])(func: A => B): F[B] = flatMap(value)(value => func(value))  // так вернётся только B
```

`value => ...` извлекает `A` из `F[A]`. Передача в функцию делает из него `B`. Тогда нужно воспользоваться `pure`:

Ответ:

```scala
trait Monad[F[_]] {
  def pure[A](a: A): F[A]
  def flatMap[A, B](value: F[A])(func: A => F[B]): F[B]
  
  def map[A, B](value: F[A])(func: A => B): F[B] = flatMap[A, B](value)(value => pure(func(value)))
}
```

