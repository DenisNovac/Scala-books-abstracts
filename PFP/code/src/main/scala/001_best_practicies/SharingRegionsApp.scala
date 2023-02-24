package `001_best_practicies`

import cats.effect.IO
import cats.effect._
import cats.effect.std._

import scala.concurrent.duration._

object SharingRegionsApp extends IOApp.Simple {

  private def randomSleep: IO[Unit] =
    IO(scala.util.Random.nextInt(100)).flatMap { ms =>
      IO.sleep((ms + 700).millis)
    }.void

  private def p1(sem: Semaphore[IO]): IO[Unit] =
    sem.permit.surround(IO.println("Running P1")) >>
      randomSleep

  private def p2(sem: Semaphore[IO]): IO[Unit] =
    sem.permit.surround(IO.println("Running P2")) >>
      randomSleep

  override def run: IO[Unit] =
    Supervisor[IO].use { s =>
      Semaphore[IO](1).flatMap { sem => // sharing with both p1 and p2
        /* region of sharing starts */

        s.supervise(p1(sem).foreverM).void *>
          s.supervise(p2(sem).foreverM).void *>
          IO.sleep(5.seconds).void

        /* region of sharing ends */
      }
    }
}
