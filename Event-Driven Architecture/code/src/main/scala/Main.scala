import cats.effect.IOApp
import cats.effect.ExitCode
import cats.effect.IO
import cats.effect.Spawn
import cats.effect.std.Supervisor
import cats.implicits._

import scala.concurrent.duration._

object Main extends IOApp {

  override def run(args: List[String]): IO[ExitCode] =
    Spawn[IO]
      .start(
        (IO.sleep(20.seconds) >> IO.println("Finished"))
          .timeout(10.seconds)
          .onError(t => IO.println(s"Error happened: $t"))
      ) >>
      IO.println("Runned in background") >> IO.never >> IO(ExitCode.Success)

}
