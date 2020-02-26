import cats.Functor
import cats.syntax.functor._
import scala.concurrent.{Future, ExecutionContext}


implicit val optionFunctor: Functor[Option] =
  new Functor[Option] {
    override def map[A, B](value: Option[A])(func: A => B): Option[B] =
      value.map(func)
  }


implicit def futureFunctor(implicit ec: ExecutionContext): Functor[Future] = new Functor[Future] {
  override def map[A, B](value: Future[A])(func: A => B): Future[B] = value.map(func)
}

