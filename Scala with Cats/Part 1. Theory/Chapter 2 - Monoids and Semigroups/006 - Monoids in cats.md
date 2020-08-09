# Моноиды в Cats

Моноиды в cats -  это тайпкласс в пакете `cats.kernel.Monoid` с альясом `cats.Monoid`. Посмотрим на три составляющих:

- Тайпкласс;
- Инстансы;
- Интерфейсы.

> Cats Kernel - это подпроект Cats с небольшим набором библиотек для тех, кому не нужен полный набор инструментов. Они определены в `cats.kernel`, но имеют альясы в `cats`.

```scala
import cats.Monoid
import cats.instances.string._
import cats.Semigroup

Monoid[String].combine("Hello ", "there")
Monoid[String].combine("General Kenobi!", Monoid[String].empty)

// Моноиды - это подтип полугрупп. Если не нужен пустой элемент, то можно использовать их

Semigroup[String].combine("Hello ", "there")


import cats.instances.option._
import cats.instances.int._

val a = Option(22)
val b = Option(20)

Monoid[Option[Int]].combine(a,b)  // Some(42)
```

# Monoid Syntax

Для моноидов есть синтаксис.

Combine можно заменить на `|+|` оператор.

```scala
import cats.syntax.monoid._

a |+| b  // Some(42)
```

Или точнее:

```scala
import cats.syntax.semigroup._
a |+| b
1 |+| 2 |+| Monoid[Int].empty 
```

Синтаксис от semigroup работает с empty от Monoid.
