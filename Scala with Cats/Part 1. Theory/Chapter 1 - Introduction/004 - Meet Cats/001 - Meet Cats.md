# Meet Cats

В предыдущей секции мы смотрели на тайпклассы в Scala. Теперь посмотрим на то, как они имплементированы в Cats.

Cats написана в модульной структуре, которая позволяет выбирать, какие тайпклассы, инстансы и методы нам нужны. 

`cats.Show` - это эквивалент тайпкласса `Printable`, определённого ранее. Он предоставляет возможность для написания консольного вывода без использования `toString`.

## Использование cats

Подключение cats в `build.sbt`:

```scala
// https://mvnrepository.com/artifact/org.typelevel/cats-core
libraryDependencies += "org.typelevel" %% "cats-core" % "2.1.0"
```

Использование:

```scala
import cats.Show

val showInt = Show.apply[Int] // Error:(3, 25) could not find implicit value for parameter instance: cats.Show[Int]
```

Объект-компаньон каждого тайпкласса Cats имеет метод `apply`. Метод `apply` использует имплиситы для поиска конкретных инстансов, поэтому сначала нужно получить такой инстанс в текущей области.

## Дефолтные инстансы

Пакет `cats.instances` имеет дефолтные инстансы для большого количества типов:

- cats.instances.int;
- cats.instances.string;
- cats.instances.list;
- cats.instances.option;
- cats.instances.all - все инстансы, которые поставляет Cats.

Подключение:

```scala
import cats.Show
import cats.instances.int._
import cats.instances.string._

val showInt = Show.apply[Int]
val showStr = Show.apply[String]

showInt.show(123) // res0: String = 123
showStr.show("abc") // res1: String = abc
```

## Синтаксис

Cats Show имеет вариант для работы через синтаксис:


```scala
import cats.instances.int._
import cats.instances.string._
import cats.syntax.show._

123.show
"abc".show
```

## Импорт всего

В этой книге используются специфичные импорты, демонстрирующие точные пакеты. Но есть и шорткаты:

- `import cats._` - все тайпклассы;
- `import cats.instances.all._` - все инстансы тайпклассов;
- `import cats.syntax.all._` - все синтаксические интерфейсы;
- `import cats.implicits._` - все инстансы + все синтаксические интерфейсы.


Большая часть разработчиков просто делают так:

```scala
import cats._
import cats.implicits._
```

