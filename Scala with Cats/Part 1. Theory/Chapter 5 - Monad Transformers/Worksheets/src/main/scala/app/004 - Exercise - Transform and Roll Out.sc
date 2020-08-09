import cats.data.EitherT

import scala.Boolean
import scala.concurrent.{Await, Future}

//type Response[A] = Future[Either[String, A]]

type Response[A] = EitherT[Future, String, A]

val powerLevels = Map(
    "Jazz"      -> 6,
    "Bumblebee" -> 8,
    "Hot Rod"   -> 10
  )

import cats.syntax.applicative._
import scala.concurrent.ExecutionContext.Implicits.global
import cats.instances.future._
import scala.concurrent.duration._

def getPowerLevel(autobot: String): Response[Int] =
    powerLevels.get(autobot) match {
      case Some(avg) => avg.pure[Response]
      case None      => EitherT.left(Future(s"$autobot is unreachable"))
    }

def canSpecialMove(ally1: String, ally2: String): Response[Boolean] =
    for {
      en1 <- getPowerLevel(ally1)
      en2 <- getPowerLevel(ally2)
    } yield en1 + en2 > 15

def tacticalReport(ally1: String, ally2: String): String = {
    val stack = canSpecialMove(ally1, ally2).value

    Await.result(stack, 1.second) match {
      case Left(msg) =>
        s"Error: $msg"
      case Right(true) =>
        s"Ready"
      case Right(false) =>
        "Not ready"
    }
}
