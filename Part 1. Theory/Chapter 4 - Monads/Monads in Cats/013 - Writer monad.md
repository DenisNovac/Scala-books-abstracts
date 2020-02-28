# Монада Writer

Монада Писатель позволяет выполнять логирование вместе с вычислениями. Можно использовать её для записи сообщений, ошибок или дополнительной информации о вычислениях, а извлекать лог вместе с финальным результатом.

Частое применение монады Writer - запись последовательностей шагов в многопоточных вычислениях где императивный логгинг может намешать сообщений из разных контекстов. У Writer лог привязан к результату, так что конкурентные вычисления не смешивают логи.

> `Writer` - это первый увиденный нами тип из пакета `cats.data`. Этот пакет предоставляет инстансы некоторых тайпклассов. 

## Создание и распаковка Writer-ов

`Writer[W, A]` содержит два значения - лог типа W и результат типа A.

```scala
import cats.data.Writer
import cats.instances.vector._

Writer(
  Vector(
    "It was best of times",
    "It was worst of times"
  ),
  1859
)

// res0: cats.data.WriterT[cats.Id,scala.collection.immutable.Vector[String],Int] = WriterT((Vector(It was best of times, It was worst of times),1859))

```

Заметно, что итоговый класс получился `WriterT[Id, Vector[String], Int]`, а не `Writer[Vector[String], Int]`. Это связано с тем, что Cats имплементит Writer в терминах типа `WriterT`. Это пример концепции *монадного трансформера*, о котором позже.

В целом в подобных ситуациях можно запомнить, что Writer - это альяс для WriterT, поэтому типы WriterT можно читать как Writer:

```scala
type Writer[W, A] = WriterT[Id, W, A]
```

Cats предоставляет возможность создавать Writer, указывая только лог или результат. 


- Если у нас есть только результат - можно использовать стандартный синтаксис `pure`. 

```scala
import cats.syntax.applicative._

type Logged[A] = Writer[Vector[String], A]

123.pure[Logged]  // res1: Logged[Int] = WriterT((Vector(),123))
("123",123).pure[Logged] // res1: Logged[(String, Int)] = WriterT((Vector(),(123,123)))
(Vector("123"),123).pure[Logged]  // res1: Logged[(scala.collection.immutable.Vector[String], Int)] = WriterT((Vector(),(Vector(123),123)))
```

- Если у хочется просто логировать без хранения результата:

```scala
import cats.syntax.writer._

Vector("msg1", "msg2").tell  // res2: cats.data.Writer[scala.collection.immutable.Vector[String],Unit] = WriterT((Vector(msg1, msg2),()))
```

- Если есть лог с результатом:

```scala
val a = Writer(Vector("msg1", "msg2"), 123)  // a: cats.data.WriterT[cats.Id,scala.collection.immutable.Vector[String],Int] = WriterT((Vector(msg1, msg2),123))
```

Извлечь результат и лог можно методами:

```scala
a.value  // res3: cats.Id[Int] = 123
a.written  // res4: cats.Id[scala.collection.immutable.Vector[String]] = Vector(msg1, msg2)

// сразу всё:

a.run // res5: cats.Id[(scala.collection.immutable.Vector[String], Int)] = (Vector(msg1, msg2),123)
```







