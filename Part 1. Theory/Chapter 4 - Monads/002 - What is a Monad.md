# Что такое Монада

> *Монада - это моноид в моноидальной категории эндофункторов.*

Проще говоря - монада это механизм для последовательных вычислений. Ранее мы сказали то же самое о функторах, поэтому нужно разъяснение.

Функторы действительно делают то же самое: они позволяют совершить последоватлеьные вычисления с игнорированием некоторых "усложненностей" текущего типа данных:

- Option - может быть значением или не быть;
- Either - может быть значением, а может быть ошибкой;
- List - может быть пустым или непустым.

Однако, функторы ограничены тем, что они позволяют определить только одно такое "усложнение" в начале последовательности. Они не учитывают дальнейшие усложнения на каждом шаге последовательности.

Метод `flatMap` монад позволяет указывать, что делать дальше, учитывая промежуточные усложнения. Метод `flatMap` типа Option учитывает промежуточные Option, для листов - промежуточные листы. 

Примеры.

## Options

```scala
/** Оба этих метода могут вернуть None */
def parseInt(str: String): Option[Int] =
  scala.util.Try(str.toInt).toOption

def divide(a: Int, b: Int): Option[Int] =
  if (b == 0) None else Some(a / b)

/** FlatMap позволяет игнорировать это при последовательных вычислениях */

def stringDivideBy(aStr: String, bStr: String): Option[Int] =
  parseInt(aStr).flatMap { aNum =>
    parseInt(bStr).flatMap { bNum =>
      divide(aNum, bNum)
    }
  }

stringDivideBy("1", "2")  // res0: Option[Int] = Some(0)
stringDivideBy("1", "0")  // res1: Option[Int] = None
stringDivideBy("3", "2")  // res2: Option[Int] = Some(1)
```

Последовательность действий:

- Первый вызов `parseInt` вернёт `None` или `Some`;
- Если он вернул `Some` - `flatMap` вызывает следующую функцию и передаёт туда `aNum`;
- Второй вызов `parseInt` так же возвращает `None` или `Some`;
- Если вернул `Some` - опять вызывается следующая функция;
- Вызов `divide` возвращает `Some` или `None`.

Результат вычисления снова Option, поэтому можно снова вызывать `flatMap` и продолжить вычисления. В результате получается поведение, при котором None на любом этапе вернёт None в конце.

Выходит, что мы передаём в divide предполагаемые Int-ы, хотя это могут быть None или, как минимум, Some[Int]. Мы не задумываемся о внутренних усложнениях вроде типов-контейнеров `Some[Int](x)`.

Каждая монада является функтором, что означает, что она содержит и метод `map`. А наличие `flatMap` и `map` позволяет использовать for comprehensions:

```scala
def stringDivideBy(aStr: String, bStr: String): Option[Int] =
  for {
    aNum <- parseInt(aStr)
    bNum <- parseInt(bStr)
    ans <- divide(aNum, bNum)
  } yield ans
```

## Lists

При первом знакомстве с flatMap и for comprehensions можно решить, что это аналог цикла for для итерации по листу:

```scala
for {
  x <- (1 to 3).toList
  y <- (4 to 5).toList
} yield (x, y)  // res0: List[(Int, Int)] = List((1,4), (1,5), (2,4), (2,5), (3,4), (3,5))
```

Но монадное поведение листа можно объяснить иначе. Если думать о `List` как о наборе результатов, `flatMap` становится конструкцией расчёта перестановок и кобинаций. 

Например, в примере выше `flatMap` генерирует комбинации через последовательность операций:

- get x;
- get y;
- return tuple (x,y).

## Futures

`Future` - это монада для последовательных асинхронных вычислений без волнений о том, что они асинхронные:

```scala
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

def doSomethingLongRunning: Future[Int] = ???
def doSomethingElseLongRunning: Future[Int] = ???

def doSomethingVeryLongRunning: Future[Int] =
    for {
      result1 <- doSomethingLongRunning
      result2 <- doSomethingElseLongRunning
    } yield result1 + result2
```

Эту запись можно развернуть таким образом:

```scala
def doSomethingVeryLongRunningMaps: Future[Int] =
  doSomethingLongRunning.flatMap { result1 =>
    doSomethingElseLongRunning.map { result2 =>
      result1 + result2
    }
  }
```

Здесь явно видно, что код в каждой строке for выполняется *последовательно* - аргумент от первого значения передаётся в функцию далее. Каждая Future в этой последовательности создаётся функцией, принимающей результат предыдущей Future. **Каждый следующий шаг наступает только если предыдущий закончен**. Главный смысл монад в последовательном вычислении.
