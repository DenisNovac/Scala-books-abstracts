# Классы типов (Type classes)

>Классы типов (Type class, тайпкласс) - это паттерн из Haskell. Они позволяют расширять существующие библиотеки без использования наследования или ручного редактирования оригинального кода.

Cats использует их повсеместно для доставки функционала.

В этой главе посмотрим два примера - классы `Show` и `Eq`. 

## Тайпкласс

**Компоненты тайплкасса:**

- Сам тайпкласс;
- Инстансы для определённых типов;
- Интерфейсные методы, открытые юзерам.

Тайплкасс - это интерфейс или АПИ, который представляет некоторый функционал. В Cats они являются трейтами с, как минимум, одним параметром. Например, мы можем представить обобщённое поведение *сериализовать в JSON* таким образом:

```scala
// Json-кейзы
sealed trait Json
final case class JsObject(get: Map[String, Json]) extends Json
final case class JsString(get: String) extends Json
final case class JsNumber(get: Double) extends Json
case object JsNull extends Json

// Поведение "сериализовать в JSON" зашито в этом трейте
trait JsonWriter[A] {
  def write(value: A): Json
}
```

`JsonWriter` это тайпкласс. `Json` и его сабтайпы предоставляют поддерживающий код, связанный с конкретной задачей.

## Инстансы тайпкласса

Инстансы тайпкласса предоставляют имплементации для типов, которые нам нужны. Например, это могут быть типы из стандартной либы Scala или типы из нашей domain-модели. 

В Scala принято определять инстансы через создание конкретных имплементаций тайпкласса и пометки их словом `implicit`.

```scala
object JsonWriterInstances {
  implicit val stringWriter: JsonWriter[String] = 
    new JsonWriter[String] {
      def write(value: String): Json =
        JsString(value)
    }

  // и так далее ...
}
```

Таким образом, `JsonWriter[String]` - это реализация тайпкласса (инстанс) `JsonWriter` для типа `String`.

Пример для сериализации Person в Json:

```scala
final case class Person(name: String, email: String)

object JsonWriterInstances {
  // ... stringWriter как выше

  implicit val personWriter: JsonWriter[Person] = 
    new JsonWriter[Person] {
      def write(value: Person): Json = 
        JsObject(Map (
          "name" -> JsString(value.name),
          "email" -> JsString(value.email)
        ))
    }
}

```

## Интерфейсы тайпклассов

Интерфейс тайпкласса - это функционал, который мы открываем юзерам. Это генерик методы которые принимают инстансы тайпкласса как имплиситные параметры. 

Есть два способа определения интерфейса:

- Интерфейсные объекты (Interface Objects);
- Интерфейсный синтаксис (Interface Syntax).

### Interface Objects

Простейший путь создания интерфейса - это поместить методы в синглтон:

```scala
object Json {
  def toJson[A](value: A)(implicit w: JsonWriter[A]): Json = 
    w.write(value)
}
```

Чтобы использовать такой объект, мы импортируем инстансы тайплкасса, которые хотим использовать и вызываем нужный метод:

```scala
import JsonWriterInstances._

Json.toJson(Person("Dave", "dave@example.com"))

```

### Interface Syntax

Мы могли альтернативно использовать *extension methods* (методы расширения) для расширения существующих типов интерфейсными методами.

```scala
object JsonSyntax {
  implicit class JsonWriterOps[A](value: A) {
    def toJson(implicit w: JsonWriter[A]): Json =
      w.write(value)
  }
}
```

Синтаксис импортируется вместе с инстансами:

```scala
import JsonWriteInstances._
import JsonSyntax._

Person("Dave", "dave@example.com").toJson
```

Полный код примера:

```scala
// сериализуемые данные
sealed trait Json
final case class JsObject(get: Map[String, Json]) extends Json
final case class JsString(get: String) extends Json
final case class JsNumber(get: Double) extends Json
case object JsNull extends Json
final case class Person(name: String, email: String)

// тайпкласс
trait JsonWriter[A] {
  def write(value: A): Json
}
```

```scala
/** 
  * Реализации тайпкласса для нужных классов
  */
object JsonWriterInstances {

  implicit val stringWriter: JsonWriter[String] = 
  new JsonWriter[String] {
    def write(value: String): Json =
      JsString(value)
  }

  implicit val personWriter: JsonWriter[Person] = 
    new JsonWriter[Person] {
      def write(value: Person): Json = 
        JsObject(Map (
          "name" -> JsString(value.name),
          "email" -> JsString(value.email)
        ))
    }
}

/** 
  * Интерфейс
  */
object JsonSyntax {
  implicit class JsonWriterOps[A](value: A) {
    def toJson(implicit w: JsonWriter[A]): Json =
      w.write(value)
  }
}
```

```scala
import JsonWriteInstances._
import JsonSyntax._

Person("Dave", "dave@example.com").toJson
```

Что происходит:

- Метод `toJson` вызван для класса `Person`;
- Создаётся класс `JsonWriterOps[Person]`;
- В `toJson` неявно передаётся `personWriter` типа `JsonWriter[Person]`, который находится в области видимости, т.к. мы импортировали `JsonWriteInstances`.


## Метод implicitly

Стандартная библиотека Scala предоставляет генерик тайпкласс `implicitly`, он очень простой:

```scala
def implicitly[A](implicit value: A): A =
  value
```

Мы можем использовать `implicitly` для вызова любого значения из имплисивного окружения. Просто передаём в него тип и получаем имплиситное значение:

```scala
import JsonWriterInstances._

implicitly[JsonWriter[String]]
// res: JsonWriter[String]
```

Он хорош для дебага. Мы можем использовать его, чтобы убедиться, что компилятор может найти инстанс тайпкласса.
