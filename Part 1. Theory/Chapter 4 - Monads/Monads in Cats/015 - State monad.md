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

