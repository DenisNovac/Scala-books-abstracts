# Монада State

`cats.data.State` позволяет передавать дополнительное состояние как часть вычислений.

Мы определяем State для атомарных операций и связываем их средствами монад. Так мы можем моделировать мутабельные состояния в функциональном стиле.

## Создание и распаковка

State[S, A], где S - это тип состояния, а A - тип результата.

```scala
import cats.data.State

val a = State[Int, String] { state =>
    (state, s"The state is $state")
  } // a: cats.data.State[Int,String] = cats.data.IndexedStateT@19c4193e


/** Состояние и результат */
val (state, result) = a.run(10).value
// state: Int = 10
// result: String = The state is 10

/** Только состояние */
val state = a.runS(10).value // state: Int = 10

/** Только результат */
val result = a.runA(10).value // result: String = The state is 10
```

State - это функция, которая делает две вещи:

- Трансформирует входное состояние в выходное;
- Вычисляет результат.


## Композиция и трансформирование State

Каждый индивидуальный инстанс показывает атомарное *состояние*, а их комбинация представляет полную последовательность операций. 

```scala
/** Последовательность действий */

val step1 = State[Int, String] { num => 
  val ans = num + 1
  (ans, s"Result of step1: $ans")
}

val step2 = State[Int, String] { num => 
  val ans = num * 2
  (ans, s"Result of step2: $ans")
}

val both = for {
  a <- step1
  b <- step2
} yield (a, b)

val (state, result) = both.run(20).value
/** state: Int = 42
result: (String, String) = (Result of step1: 21,Result of step2: 42) */
```

Удобно, что шаги переключаются один за другим, хотя мы и не передаём результаты явно (например, не нужно передавать `a` в `step2`)

Обобщённый пример использования - это разбить большое вычисление на промежутки и скомпозировать их через монадные операторы. Cats предоставляет конструкторы для создания шагов:

- `get` представляет стейт как result;
- `set` заменяет входной стейт и возвращает unit как result;
- `pure` прибавляет к стейту result;
- `inspect` создает result из стейта по функции;
- `modify` обновляет стейт через функцию.

```scala


/** Конструкторы для стейтов */

val getDemo = State.get[Int]
getDemo.run(10).value
/*getDemo: cats.data.State[Int,Int] = cats.data.IndexedStateT@1234a788
res0: (Int, Int) = (10,10)*/


val setDemo = State.set[Int](30)
setDemo.run(10).value
/*setDemo: cats.data.State[Int,Unit] = cats.data.IndexedStateT@5f5f8e18
res1: (Int, Unit) = (30,())*/


val pureDemo = State.pure[Int, String]("result")
pureDemo.run(10).value
/*pureDemo: cats.data.State[Int,String] = cats.data.IndexedStateT@2d3ebc98
res2: (Int, String) = (10,result)*/

val inspectDemo = State.inspect[Int, String](_ + "!")
inspectDemo.run(10).value
/*inspectDemo: cats.data.State[Int,String] = cats.data.IndexedStateT@b77995
res3: (Int, String) = (10,10!)*/


val modifyDemo = State.modify[Int](_ + 1)
modifyDemo.run(10).value
/*modifyDemo: cats.data.State[Int,Unit] = cats.data.IndexedStateT@401b7595
res4: (Int, Unit) = (11,())*/
```

Эти конструкторы удобно использовать в for:

```scala
import State._

val program: State[Int, (Int, Int, Int)] = for {
  a <- get[Int] 
  _ <- set[Int](a + 1)  // в промежутках между вычислениями мы обновляем стейт
  b <- get[Int] 
  _ <- modify[Int](_ + 1) // но для стейта есть только его переменная, а не три штуки
  c <- inspect[Int, Int](_ * 1000)
} yield (a,b,c)


val (state, result) = program.run(1).value // state: Int = 3  result: (Int, Int, Int) = (1,2,3000)
```

## Упражнение: Post-Order Calculator

Монада State позволяет имплементировать простые интерпретаторы для сложных выражений, передавая значения мутабельных регистров вместе с результатом. Мы можем увидеть это наглядно, создав калькулятор для выражений в обратной нотации.

Post-order expressions - это математическая нотация, в которой мы пишем опреатор после операндов:

```scala
1 2 +
```

Все, что нужно для реализации - пройти значения слева-направо, накопив стек операндов:

- Если мы видим число - помещаем в стек;
- Если мы видим оператор - мы достаём два операнда из стека, используем оператор и кладём в стек результат.

Это позволяет реализовывать сложные выражения без оглядки на порядок действий.

```scala
/** Exercise 4.9.3 - Post-Order Calculator */
import cats.data.State

type CalcState[A] = State[List[Int], A]

def operand(num: Int): CalcState[Int] =
  State[List[Int], Int] { stack =>
    (num :: stack, num)
  }

def operator(func: (Int, Int) => Int): CalcState[Int] =
  State[List[Int], Int] {
    case b :: a :: tail =>
      val ans = func(a, b)
      (ans :: tail, ans)

    case _ =>
      sys.error("Fail")
  }

def evalOne(sym: String): CalcState[Int] =
  sym match {
    case "+" => operator(_ + _)
    case "-" => operator(_ - _)
    case "*" => operator(_ * _)
    case "/" => operator(_ / _)
    case num => operand(num.toInt)
  }
val e = evalOne("42").runA(Nil).value  // 42
```

Это работает следующим образом. Если входной символ является математическим сивмолом - из старого стека вынимаются a и b, а взамен кладётся ans. Иначе вызывается функция operand, которая просто кладёт число в стек спереди. 

Функция `evalOne` позволяет создать стек из первого числа 42 и инициализирующего стека `Nil`.


```scala
val program = for {
    _   <- evalOne("1")
    _   <- evalOne("2")
    ans <- evalOne("+")

  } yield ans

program.runA(Nil).value  // 3
```

Таким образом можно организовать вычисления.

Наконец, метод evalAll:

```scala
import cats.syntax.all._

/** Функция собственно вычисления */
def evalAll(input: List[String]): CalcState[Int] =
  input.foldLeft(0.pure[CalcState]) { (a,b) =>
    a.flatMap(_ => evalOne(b))
  }

evalAll(List("1", "2", "+", "3", "*")).runA(Nil).value  // 9
```


