# Eval Monad

`cats.Eval` - это монада для абстракции надо различными *моделями вычислений*. Мы обычно думаем о двух моделях - незамедлительная и ленивая. `Eval` позволяет определять, будет ли результат *мемоизирован*:

>*Мемоизация (запоминание, от англ. memoization) — в программировании сохранение результатов выполнения функций для предотвращения повторных вычислений. Это один из способов оптимизации, применяемый для увеличения скорости выполнения компьютерных программ*

Незамедлительное вычисление происходит мгновенно, а ленивая - по требованию. Мемоизированные вычисления запускаются по требованию, а результаты кешируются в памяти.

Например, `vals` в Scala мемоизированы и незамедлительны, а `defs` - ленивые и немемоизированные. 

```scala
val x = {  // вычислится единожды
  println("Computing X")
  math.random
}
// Computing X
// x: Double = 0.828821237871653
x // first access
// res0: Double = 0.828821237871653
x // second access
// res1: Double = 0.828821237871653

def y = {  // вычислится каждый раз 
  println("Computing Y")
  math.random
}
// y: Double
y // first access
// Computing Y
// res2: Double = 0.8068798927021629
y // second access
// Computing Y
// res3: Double = 0.9741888122553769

lazy val z = {
  println("Computing Z")
  math.random
}
// z: Double = <lazy>
z // first access
// Computing Z
// res4: Double = 0.8103134694961557
z // second access
// res5: Double = 0.8103134694961557
```

`lazy vals` - ленивы и мемоизованны.

## Модели вычисления Eval

`Eval` имеет три подтипа: Now, Later, Always. 

```scala
import cats.Eval

val now = Eval.now(math.random + 1000)  // now: cats.Eval[Double] = Now(1000.8890187741796)
val later = Eval.later(math.random + 2000)  // later: cats.Eval[Double] = cats.Later@6f25d833
val always = Eval.always(math.random + 3000)  // always: cats.Eval[Double] = cats.Always@315d7b4

now.value  // res0: Double = 1000.8890187741796
now.value  // res0: Double = 1000.8890187741796

later.value  // res1: Double = 2000.639597360145
later.value  // res1: Double = 2000.639597360145

always.value  // res2: Double = 3000.3751549443864
always.value  // res5: Double = 3000.4968247795314
```

- `now` работает как `val` - незамедлительно и мемоизированно (единожды);
- `later` - ленивые `lazy val` - по запросу и единожды;
- `always` - похоже на `def` - лениво и немемоизовано.

## Eval как монада

Как и все монады, Eval обладает методами map и flatMap для добавления вычислений по цепочке. Однако, в этом случае цепочка хранится явно как лист функций. Они не будут выполняться пока не будет запрошен результат.

```scala
// greeting: cats.Eval[String] = cats.Eval$$anon$4@365c2e6e
val greeting = Eval.
  always { println("Step 1"); "Hello" }.
  map { str => println("Step 2"); s"$str word"}

greeting.value

// Step 1
// Step 2
// res6: String = Hello word
```

Функции маппинга всегда ленивы:

```scala
// Step 1
// greeting2: cats.Eval[String] = cats.Eval$$anon$4@53ecda01
val greeting2 = Eval.
  now { println("Step 1"); "Hello" }.
  map { str => println("Step 2"); s"$str word"}

greeting2.value
// Step 2
// res7: String = Hello word


// A
// ans: cats.Eval[Int] = cats.Eval$$anon$4@6e391400
val ans = for {
    a <- Eval.now { println("A"); 40 }
    b <- Eval.always { println("B"); 2 }
  } yield {
    println("A + B")
    a + b
  }

ans.value
// B
// A + B
// res8: Int = 42

ans.value
// B
// A + B
// res8: Int = 42
```

### Memoize

Eval имеет метод memoize, который позволяет запомнить цепочку вычислений.

```scala
val saying = Eval
    .always { println("Step 1"); "The cat" }
    .map { str =>
      println("Step 2"); s"$str sat on"
    }
    .memoize
    .map { str =>
      println("Step 3"); s"$str the mat"
    }

saying.value  // Step 1 Step 2 Step 3 res10: String = The cat sat on the mat

saying.value // Step 3 res11: String = The cat sat on the mat
```

Результаты вплоть до `memoize` кешируются, а остальные сохраняют свою семантику.


## Trampolining (стекобезопасность) and Evel.defer

Одна из полезных особенностей Eval - это то, что его методы `map` и `flatMap` - *trampolined*. Это значит, что мы можем вкладывать вызовы map и flatMap без потребления стека. Это свойство называется "стекобезопасность". 

Попробуем улучшить такой рекурсивный метод:

```scala
def factorial(n: BigInt): BigInt =
  if(n == 1) n else n * factorial(n - 1)

factorial(50000)  // java.lang.StackOverflowError
```

Его можно переписать на Eval:

```scala

def factorial(n: BigInt): Eval[BigInt] =
  if (n == 1) {
    Eval.now(n)
  } else {
    factorial(n - 1).map(_ * n)
  }

factorial(50000)  // java.lang.StackOverflowError

```

Это не сработало потому что мы делаем все рекурсивные вызовы к `factorial` перед началом работы с методом `map` у самого `Eval`. В качестве воркараунда можно использовать `Eval.defer`, который берёт существующий инстанс `Eval` и откладывает его вычисление. Этот метод также стекобезопасен.

```scala
def factorial(n: BigInt): Eval[BigInt] =
  if (n == 1) {
    Eval.now(n)
  } else {
    Eval.defer(factorial(n - 1).map(_ * n))
  }

factorial(50000).value  // res0: BigInt = 33473205095971448369154760940714864779127732238104548077301003219901680221443656416973812310719169308798480438190208299893616384743066693742630572845363784038325756282123359987268244078235972356040853854441373383753568565536371168327405166076155165921406156075461294201790567479665498629242220022541553510718159801615476451810616674970217996537474972541139338191638823500630307644256874857271394651081909874909643486268589229807870031031008962861154553979911612940652327396971497211031261142860733793509687837355811830609551728906603833592532851635961730885279811957399495299450306354442478492641028990069559634883529900557676550929175475920788044807622562415165130459046318068517406766360012329556454065724225175473428183121029195715593787423641117194513838593038006413132976312508...
```

Таким образом, Eval - полезное средство для усиления стекобезопасности, когда происходит работа с огромным количеством структур данных и вычислений. Но стекобезопасность не бесплатна. Она избегает потребления стака, но создаёт цепочку объектов (функций) в куче. Лимиты вложений существуют, но ограничены они кучей, а не стеком.

