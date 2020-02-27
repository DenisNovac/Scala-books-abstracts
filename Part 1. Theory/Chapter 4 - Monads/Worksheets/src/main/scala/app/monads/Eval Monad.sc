import cats.Eval

val now = Eval.now(math.random + 1000)    // val
val later = Eval.later(math.random + 2000)  // lazy val
val always = Eval.always(math.random + 3000) // def

now.value
now.value

later.value
later.value

always.value
always.value

val greeting = Eval.always { println("Step 1"); "Hello" }.map { str =>
    println("Step 2"); s"$str word"
  }

greeting.value

val greeting2 = Eval.now { println("Step 1"); "Hello" }.map { str =>
    println("Step 2"); s"$str word"
  }

greeting2.value

val ans = for {
    a <- Eval.now { println("A"); 40 }
    b <- Eval.always { println("B"); 2 }
  } yield {
    println("A + B")
    a + b
  }

ans.value
ans.value

val saying = Eval
    .always { println("Step 1"); "The cat" }
    .map { str =>
      println("Step 2"); s"$str sat on"
    }
    .memoize
    .map { str =>
      println("Step 3"); s"$str the mat"
    }

saying.value

saying.value



