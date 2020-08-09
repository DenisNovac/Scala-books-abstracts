# Упражнение 5.4 Monads: Transform and Roll Out

Автоботы посылают друг другу сообщения таким методом:

```scala
type Response[A] = Future[Either[String, A]]

def getPowerLevel(autobot: String): Response[Int] = ???
```

Сообщение идут некоторое время и могут быть перехвачены Десепктиконами. Поэтому это Future от Either.

- Оптимусу Прайму надоели for comprehensions, поэтому он хочет использовать Response через Монадный трансформер:

```scala
type Response[A] = EitherT[Future, String, A]
```

- Проверить код, написано имплементацию `getPowerLevel`:

```scala
import cats.syntax.applicative._
import scala.concurrent.ExecutionContext.Implicits.global
import cats.instances.future._


def getPowerLevel(autobot: String): Response[Int] =
  powerLevels.get(autobot) match {
    case Some(avg) => avg.pure[Response]
    case None => EitherT.left(Future(s"$autobot is unreachable"))
  }
```

- Два автобота могут выполнить особый приём, если их суммарная энергия больше 15:

```scala
def canSpecialMove(ally1: String, ally2: String): Response[Boolean] = for {
    en1 <- getPowerLevel(ally1)
    en2 <- getPowerLevel(ally2)
  } yield en1+en2 > 15
```

- Наконец, метод `tacticalReport` должен брать два имени и писать, могут ли они соврешить специальный приём:

```scala
def tacticalReport(ally1: String, ally2: String): String = {
    val stack = canSpecialMove(ally1, ally2).value

    Await.result(stack, 1.second) match {
      case Left(msg) =>
        s"Error: $msg"
      case Right(true) =>
        s"Ready"
      case Right(false) =>
        "Not ready"
    }
}
```

Мы можем вывести ошибку не во время возникновения, а намного позже.