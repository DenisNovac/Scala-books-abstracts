# Traverse

foldLeft и foldRight - это гибкие итеративные методы, но они требуют определять аккумуляторы и комбинаторные функции. Traverse предоставляет паттерн для итерации поверх Applicatives.

## Traversing with Futures

Мы можем продемонстрировать Traverse, используя Future.traverse и Future.sequence из стандартной библиотеки. Эти методы предоставляют Future-специфичные реализации паттерна Traverse.

Предположим, у нас есть хосты и метод, спрашивающий, как долго они в сети:

```scala
import scala.concurrent._
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

val hostnames = List(
    "alpha.example.com",
    "beta.example.com",
    "gamma.demo.com"
  )

def getUptime(hostname: String): Future[Int] =
    Future(hostname.length * 60) // just for demonstration

```

Теперь предположим, что мы хотим вытащить все хосты и собрать их аптаймы. Мы не можем просто `map`-нуть, потому что в результате мы хотим не `List[Future[Int]]`, а  `Future[List[Int]]`:

```scala
val allUptimesList: List[Future[Int]] =
  hostnames.map(a => getUptime(a))
```

Нам нужно свести результаты в одну фьючу. Начнём с ручного фолда:

```scala
val allUptimesFold: Future[List[Int]] = 
  hostnames.foldLeft(Future(List.empty[Int])) {
    (accum, host) =>
      val uptime = getUptime(host)
      for {
        accum <- accum
        uptime <- uptime
      } yield  accum :+ uptime
  }


Await.result(allUptimesFold, 1.second)
```

Мы итерируем по хостнеймам, вызываем функцию и складываем результат в лист. В сущности, этот алгоритм и лежит в основе traverse. 

При этом код получается не очень хороший потому что мы создаём и комбинируем Future в каждой итерации. Мы можем заменить это на traverse:

```scala
val allUptimes: Future[List[Int]] =
  Future.traverse(hostnames)(getUptime)
```

Traverse просто инкапсулирует некрасивый код, делая тот же foldLeft. На уровне traverse нам нужно лишь:

- Начать с листа `List[A]`;
- Предоставить функцию `A => Future[B]`;
- Получить на выходе `Future[List[B]]`.

Либо, если мы начинаем с `List[Future[A]]`:

- Начать с `List[Future[A]]`;
- Закончить с `Future[List[A]]`.

Traverse позволяет решать специфичную проблему - ходить по коллекции и аккумулировать результат. Cats предоставляет Travers для всех стандартных коллекций Scala.

Traverse обобщает использование этого паттерна для любого Applicative. 