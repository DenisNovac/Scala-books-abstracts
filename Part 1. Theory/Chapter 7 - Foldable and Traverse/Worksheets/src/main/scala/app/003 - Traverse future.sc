import scala.concurrent._
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

val hostnames = List(
    "alpha.example.com",
    "beta.example.com",
    "gamma.demo.com"
  )

def getUptime(hostname: String): Future[Int] =
    Future(hostname.length * 60) // just for demonstration

val allUptimesList: List[Future[Int]] =
  hostnames.map(a => getUptime(a))

val allUptimesFold: Future[List[Int]] =
  hostnames.foldLeft(Future(List.empty[Int])) {
    (accum, host) =>
      val uptime = getUptime(host)
      for {
        accum <- accum
        uptime <- uptime
      } yield  accum :+ uptime
  }


Await.result(allUptimesFold, 1.second)


val allUptimes: Future[List[Int]] =
  Future.traverse(hostnames)(getUptime)

Await.result(allUptimes, 1.second)