# Exercise 2.5.4 - Adding all the things

Написать метод `def add(items: List[Int]): Int`, складывающий содержимое листа.

```scala
def add(items: List[Int]): Int = {
  
  @tailrec
  def loop(acc: Int, items: List[Int]): Int = items match {
    case Nil => acc
    case x::xs => loop(acc + x, xs)
  }
  
  loop(0, items)
}

```


Написать тот же метод для `List[Option[Int]]`. Теперь стало неудобно - хочется, чтобы само. Тогда проще перейти на моноиды:

```scala
import cats.syntax.monoid._
import cats.instances.int._
import cats.instances.option._

def addMonoid[A: Monoid](items: List[A]): A = {

  @tailrec
  def loop(acc: A, items: List[A]): A = items match {
    case Nil => acc
    case x::xs => loop(acc |+| x, xs)
  }

  loop(Monoid[A].empty, items)
}

addMonoid[Option[Int]](List(Some(1), Some(2), Some(3)))
```

В книге проще:

```scala
def addMonoidSimple[A: Monoid](items: List[A]): A =
    items.foldLeft(Monoid[A].empty)(_ |+| _)
```


Теперь написать штуку чтобы складывать такой класс:

```scala
case class Order(totalCost: Double, quantity: Double)
```


Ответ:


```scala
// Написать сложение для такой штуки

case class Order(totalCost: Double, quantity: Double)

implicit val orderInstance: Monoid[Order] = new Monoid[Order] {
  override def empty = Order(0, 0)

  override def combine(x: Order, y: Order) = {
    Order(x.totalCost + y.totalCost, x.quantity + y.quantity)
  }
}


// выводилка в консоль

val someOrder = addMonoidSimple(List(Order(1,2), Order(3,4)))

implicit val orderShow: Show[Order] = new Show[Order] {
  override def show(t: Order) = s"Order(${t.totalCost}, ${t.quantity})"
}

import cats.syntax.show._

someOrder.show  // Order(4.0, 6.0)
```