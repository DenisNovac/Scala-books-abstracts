import cats.Traverse
import cats.instances.future._
import cats.instances.list._

import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global


val numbers1: List[Future[Int]] = List(Future(1), Future(2))
val numbers2: Future[List[Int]] = Traverse[List].sequence(numbers1)



import cats.syntax.traverse._

val numbers3: Future[List[Int]] = numbers1.sequence
