# Printable Library

Scala предоставляет метод `toString` для конвертации любого значения в String. Но у него есть несколько проблем: он имплементирован для каждого типа в языке, большая часть имплементаций ограничена и мы не можем быстро перейти к специфичной имплементации для нужного типа в IDE (она бросит нас в ).

Задача:

- Определить тайпкласс `Printable[A]` с методом `format(value: A): String`;
- Создать объект `PrintableInstances` с инстансами для `String` и `Int`;
- Создать объект `Printable` с интерфейсными методами `format(value: A): String` и `print(value: A): Unit`.

Я сразу делал следующее упражнение с Syntax классом:

- Создать PrintableSyntax;
- Определить `PrintableOps[A]`
- Определить методы `print` и `format` для `PrintableOps`.

`format` переименовал в `myformat`, без этого подключить `PrintableSyntax` не получилось...

## Решение

Тайпкласс с компаньонами:

```scala
/**
  * Тайпкласс
  */
trait Printable[A] {
  def myformat(value: A): String
}

/**
  * Интерфейсы в объекте-компаньоне тайпкласса
  */
object Printable {
  def myformat[A](value: A)(implicit p: Printable[A]): String =
    p.myformat(value)

  def myprint[A](value: A)(implicit p: Printable[A]): Unit =
    println(myformat(value))
}
```

Инстансы:

```scala
/**
  * Инстансы для конкретных типов, маркер <3 для наглядности
  */
object PrintableInstances {
  /*implicit val stringFormatter: Printable[String] =
    new Printable[String] {
      override def format(value: String): String = value + " <3"
    }*/

  // то же что:
  implicit val stringFormatter: Printable[String] =
    (value: String) => value + " <3"

  implicit val intFormatter: Printable[Int] =
    (value: Int) => value.toString + " <3"
}
```

Класс Syntax (тоже интерфейс):

```scala
/**
  * Подключаемый синтаксис является интерфейсом, но реализован иначе и вписывается красивее
  */
object PrintableSyntax {
  implicit class PrintableOps[A](value: A) {

    def syntaxformat(implicit p: Printable[A]): String =
      p.myformat(value)

    def syntaxprint(implicit p: Printable[A]): Unit =
      println(syntaxformat(p))
  }
}

```

Вызовы:

```scala
import PrintableInstances._
import PrintableSyntax._

object Main extends App {

  val a = "Test String"

  /** Эти вызовы не нуждаются в PrintableSyntax */
  println(Printable.myformat(a)) // Test String <3
  Printable.myprint(a)
  Printable.myprint(123)

  println()

  /** А вот эти нуждаются как в Instances, так и в Syntax */
  println(a.syntaxformat)  // Test String <3 from Syntax
  println(1234.syntaxformat)
  a.syntaxprint
  12345.syntaxprint
}
```


## Использование библиотеки

Код выше создаёт библиотеку общего назначения, которую можно использовать во многих приложениях. 

Создадим приложение для хранения кошек.

Определим тип данных:

```scala
case class Cat(name: String, age: Int, color: String)
```

А затем инстанс в объекте-компаньоне:

```scala
object Cat {
  implicit val catFormatter = new Printable[Cat] {
    override def myformat(cat: Cat): String = {
      val name = Printable.myformat(cat.name)
      val age = Printable.myformat(cat.age)
      val color = Printable.myformat(cat.color)
      s"$name is a $age year-old $color cat."
    }
  }
}
```

После этого можно сразу пользоваться:

```scala
Printable.myprint(Cat("Berta", 23, "Black"))
Cat("Berta", 23, "Black").syntaxprint
```