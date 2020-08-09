# Монада Reader

Reader позволяет выполнять последовательности операций, которые зависят от некоторого инпута. Инстансы ридера оборачивают функции одного аргумента и позволяют их композировать.

Частое применение - **Dependency Injection**. Если у нас есть несколько операций, которые полагаются на внешнюю конфигурацию - мы можем сцепить их и произвести одну большую операцию, требующую конфигурацию как параметр.

Создание ридера:

```scala
import cats.data.Reader

case class Cat(name: String, favoriteFood: String)

val catName: Reader[Cat, String] =
  Reader(cat => cat.name)  // catName: cats.data.Reader[Cat,String] = Kleisli(<function>49169f)

catName.run(Cat("Garfield", "Lasagne"))  // res0: cats.Id[String] = Garfield

```

Какие преимущества это даёт?

## Композирование Ридеров

Обычно набор ридеров создаётся для приёма одного и того же типа конфигураций, потом они комбинируются через `map` и `flatMap` и уже затем вызывают run чтобы инжектировать конфиг.

`map` просто расширяет внутреннюю функцию ридера, дополняя чем-то результат его обычной функции:

```scala
val greetKitty: Reader[Cat, String] =
  catName.map(name => s"Hello, $name")
greetKitty.run(Cat("Heathcliff", "junk food"))  // res1: cats.Id[String] = Hello, Heathcliff
```

`flatMap` позволяет кобинировать ридеры от одного инпута.

```scala
/** Комбинирование ридеров одного типа */
val feedKitty: Reader[Cat, String] =
  Reader(cat => s"Have a nice bowl of ${cat.favoriteFood}")

val greetAndFeed: Reader[Cat, String] =
  for {
    greet <- greetKitty
    feed <- feedKitty
  } yield s"$greet. $feed"  //


greetAndFeed(Cat("Garfield", "lasagne")) // res2: cats.Id[String] = Hello, Garfield Have a nice bowl of lasagne
```

## Exercise 4.8.3 - Hacking on Readers

Классическое использование ридеров - строить программы, принимающие конфигурации как параметр. Напишем систему логина. Наша конфигурация будет состоять из двух баз данных - листа юзеров и паролей.

```scala
import cats.data.Reader
import cats.syntax.all._

case class Db(
    usernames: Map[Int, String],
    passwords: Map[String, String]
)

type DbReader[T] = Reader[Db, T]

/** Методы, которые генерируют ридеры для поиска логина и пароля */

  def findUsername(userId: Int): DbReader[Option[String]] =
    Reader { db =>
      db.usernames.find(user => user._1 == userId).map(user => user._2)
    }

def checkPassword(
      username: String,
      password: String
  ): DbReader[Boolean] =
    Reader { db =>
      db.passwords.exists(user => user._1 == username & user._2 == password)
    }

def checkLogin(userId: Int, password: String): DbReader[Boolean] =
    for {
      user <- findUsername(userId)
      passwordOk <- user
                     .map { username =>
                       checkPassword(username, password)
                     }
                     .getOrElse(false.pure[DbReader])
    } yield passwordOk


val users = Map(
  1 -> "dude",
  2 -> "kate"
)

val passwords = Map(
  "dude" -> "123",
  "kate" -> "iloveyou"
)

val db = Db(users, passwords)

checkLogin(1, "123").run(db)  // res0: cats.Id[Boolean] = true
```

## Когда использовать ридеры

Ридеры предоставляют инструмент для DI. Мы пишем шаги программы как инстансы ридера, связываем их в цепочку и строим функцию, которая принимает зависимость в качестве инпута. Они наиболее полезны когда:

- Мы строим программу, которая может быть представлена функцией;
- Нам нужно описать инжекцию известных параметров;
- Мы хотим тестировать части программы изолированно.

> Kleisli - это тип, предоставляющий обобщённую форму Reader-а. Она обобщает конструктор типа результирующего типа.