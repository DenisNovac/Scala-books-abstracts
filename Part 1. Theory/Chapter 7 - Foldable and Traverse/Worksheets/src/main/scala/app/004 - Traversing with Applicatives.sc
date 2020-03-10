


import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

val hostnames = List(
    "alpha.example.com",
    "beta.example.com",
    "gamma.demo.com"
  )

def getUptime(hostname: String): Future[Int] =
    Future(hostname.length * 60) // just for demonstration

/*Аккумулятор*/
Future(List.empty[Int])

/*То же, что*/
import cats.Applicative
import cats.instances.future._
import cats.syntax.applicative._

List.empty[Int].pure[Future]

/*Комбинатор*/
  def oldCompint(accum: Future[List[Int]], host: String): Future[List[Int]] = {
    val uptime = getUptime(host)
    for {
      accum  <- accum
      uptime <- uptime
    } yield accum :+ uptime
  }

/* Эквивалентен теперь */
import cats.syntax.apply._

def newCombine(accum: Future[List[Int]], host: String): Future[List[Int]] =
    (accum, getUptime(host)).mapN(_ :+ _)

/* Можем обобщить для любого аппликативного функтора */

def listTraverse[F[_]: Applicative, A, B](list: List[A])(func: A => F[B]): F[List[B]] =
    list.foldLeft(List.empty[B].pure[F]) { (accum, item) =>
      (accum, func(item)).mapN(_ :+ _)
    }

def listSequence[F[_]: Applicative, B](list: List[F[B]]): F[List[B]] =
    listTraverse(list)(identity)

