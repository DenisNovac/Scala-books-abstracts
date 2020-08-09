# Identity Monad

В прошлый раз мы написали метод:

```scala
import cats.Monad
import cats.syntax.functor._
import cats.syntax.flatMap._


def sumSquare[F[_]: Monad](a: F[Int], b: F[Int]): F[Int] = for {
  x <- a
  y <- b
} yield x*x + y*y


sumSquare(1,2)

/*
Error:(12, 1) no type parameters for method sumSquare: (a: F[Int], b: F[Int])(implicit evidence$1: cats.Monad[F])F[Int] exist so that it can be applied to arguments (Int, Int)
--- because ---
argument expression's type is not compatible with formal parameter type;
found   : 1
required: ?F[Int]
sumSquare(1,2)

Error:(12, 11) type mismatch;
found   : Int(1)
required: F[Int]
sumSquare(1,2)

Error:(12, 13) type mismatch;
found   : Int(2)
required: F[Int]
sumSquare(1,2)
*/
```

Но он не работает на простых значениях, которые не обёрнуты в монаду.

Было бы довольно удобно использовать один метод для монад и немонад. Это позволило бы абстрагироваться над монадным и не-монадным кодом. Для этого Cats предоставляет тип `Id` для связывания:

```scala
sumSquare(1: Id[Int], 2: Id[Int])  // res0: cats.Id[Int] = 5

val n: Int = sumSquare(1: Id[Int], 2: Id[Int])  // res0: cats.Id[Int] = 5
```

Мы получили Id, но скастовали его в Int без дополнительных затрат, т.к. Id - это просто альяс:

```scala
type Id[A] = A
```

Cats предоставлят тайпклассы Functor и Monad для Id. Благодаря этому можно сделать следующее:

```scala
val first = sumSquare(1: Id[Int], 2: Id[Int]) 
val second = sumSquare(43: Id[Int], 23: Id[Int])

for {
    x <- first
    y <- second
  } yield x + y
```

Таким образом можно использовать монадный код вместе с немонадным. Например, мы можем асинхронно юзать фьючу в проде и синхронно в тестах через Id. 
