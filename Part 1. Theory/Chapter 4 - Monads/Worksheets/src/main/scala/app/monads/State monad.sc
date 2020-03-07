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





/** Конструкторы для стейтов */

val getDemo = State.get[Int]
getDemo.run(10).value

val setDemo = State.set[Int](30)
setDemo.run(10).value

val pureDemo = State.pure[Int, String]("result")
pureDemo.run(10).value

val inspectDemo = State.inspect[Int, String](_ + "!")
inspectDemo.run(10).value

val modifyDemo = State.modify[Int](_ + 1)
modifyDemo.run(10).value



/** For */
import State._

val program: State[Int, (Int, Int, Int)] = for {
  a <- get[Int]
  _ <- set[Int](a + 1)
  b <- get[Int]
  _ <- modify[Int](_ + 1)
  c <- inspect[Int, Int](_ * 1000)
} yield (a,b,c)

val (state, result) = program.run(1).value























