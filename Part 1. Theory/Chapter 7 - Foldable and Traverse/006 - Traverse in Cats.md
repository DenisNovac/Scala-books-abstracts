# Traverse in Cats

```scala
def listTraverse[F[_]: Applicative, A, B](list: List[A])(func: A => F[B]): F[List[B]] =
    list.foldLeft(List.empty[B].pure[F]) { (accum, item) =>
      (accum, func(item)).mapN(_ :+ _)
    }

def listSequence[F[_]: Applicative, B](list: List[F[B]]): F[List[B]] =
    listTraverse(list)(identity)

```

Написанные методы умеют возвращать только запакованные листы. В реальной библиотеке это не так. Cats предоставляет инстансы для листов, векторов, стримов, опций и either. И синтаксис, как и ранее:

```scala
import cats.Traverse
import cats.instances.future._
import cats.instances.list._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global


val numbers1: List[Future[Int]] = List(Future(1), Future(2))
val numbers2: Future[List[Int]] = Traverse[List].sequence(numbers1)

import cats.syntax.traverse._

val numbers3: Future[List[Int]] = numbers1.sequence
```


