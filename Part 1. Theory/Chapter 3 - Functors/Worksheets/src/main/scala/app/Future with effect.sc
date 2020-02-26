import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.util.Random

val future1 = {
  // fixed seed
  val r = new Random(0L)

  // side-effect заключается в переключении на случайный номер
  val x = Future(r.nextInt)

  for {
    a <- x
    b <- x
  } yield (a,b)
}

val future2 = {
  val r = new Random(0L)
  for {
    a <- Future(r.nextInt)
    b <- Future(r.nextInt)
  } yield (a,b)
}


Await.result(future1, 1.second)
Await.result(future2, 1.second)