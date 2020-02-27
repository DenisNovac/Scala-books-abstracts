
/** Это не цикл, это вычисление комбинаций */
for {
    x <- (1 to 3).toList
    y <- (4 to 5).toList
  } yield (x, y)

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

/** Можно развернуть это таким образом */
  def doSomethingVeryLongRunningMaps: Future[Int] =
    doSomethingLongRunning.flatMap { result1 =>
      doSomethingElseLongRunning.map { result2 =>
        result1 + result2
      }
    }

/**
  * Видно, что каждый следующий шаг наступает только если предыдущий закончен.
  * */