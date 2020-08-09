# Упражнение

Переделать предыдущий код так, чтобы использовать вместо `Printable` `Show` из Cats.

```scala
import cats.Show
import cats.syntax.all._
import cats.instances.all._


final case class Cat(name: String, age: Int, color: String)


object Main extends App {

  val a = "Test String"

  implicit val stringShow: Show[String] =
    Show.show[String](string => s"$string <3")

  println(stringShow.show(a))
  //println("abc".show) // Error:(19, 17) value show is not a member of String
  println(123.show)

  implicit val catShow: Show[Cat] =
    Show.show[Cat](cat => s"${cat.name} is a ${cat.age} year-old ${cat.color} cat.")

  println(Cat("Berta", 23, "Black").show)
}
```

Show отвалился т.к. создание stringShow создало второй show в скоупе и имплисивно подставить уже не получается. Но можно уточнить импорт:

```scala
import cats.Show
import cats.syntax.all._
import cats.instances.int._
```

Тогда инстанса Show для String не будет добавлено извне и код заработает.