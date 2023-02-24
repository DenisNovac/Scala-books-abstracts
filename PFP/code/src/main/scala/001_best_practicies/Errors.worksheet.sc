import scala.util.control.NoStackTrace
import cats.effect.std.Random
import cats.MonadThrow
import cats.implicits._
import BuisinessError._

sealed trait BuisinessError extends NoStackTrace
object BuisinessError {
  type RandomError = RandomError.type
  case object RandomError extends BuisinessError
}

case class Category(value: String)

trait Categories[F[_]] {

  // no error info in type
  def findAll: F[List[Category]]

  // or with error info in type
  def findAllE: F[Either[RandomError, List[Category]]]
}

object Categories {
  def make[F[_]: MonadThrow: Random] =
    new Categories[F] {

      override def findAll: F[List[Category]] =
        Random[F].nextInt.flatMap {
          case n if n > 100 =>
            List.empty[Category].pure[F]
          case _            =>
            RandomError.raiseError[F, List[Category]]
        }

      override def findAllE: F[Either[RandomError, List[Category]]] =
        findAll.attempt.map { // mapping error
          _.left.map(_ => RandomError)
        }

    }

}
