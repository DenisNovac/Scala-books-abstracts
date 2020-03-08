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





val e = evalOne("42").runA(Nil).value

val program = for {
    _   <- evalOne("1")
    _   <- evalOne("2")
    ans <- evalOne("+")
  } yield ans

program.runA(Nil).value  // 3


import cats.syntax.all._

/** Функция собственно вычисления */
def evalAll(input: List[String]): CalcState[Int] =
  input.foldLeft(0.pure[CalcState]) { (a,b) =>
    a.flatMap(_ => evalOne(b))
  }

evalAll(List("1", "2", "+", "3", "*")).runA(Nil).value  // 9












