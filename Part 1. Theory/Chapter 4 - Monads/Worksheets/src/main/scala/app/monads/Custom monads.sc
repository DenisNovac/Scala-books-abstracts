import cats.Monad

import scala.annotation.tailrec


val optionMonad = new Monad[Option] {
  override def pure[A](opt: A): Option[A] = Some(opt)

  override def flatMap[A, B](opt: Option[A])(fn: A => Option[B]): Option[B] = opt flatMap fn

  @tailrec
  override def tailRecM[A, B](a: A)(fn: A => Option[Either[A, B]]): Option[B] =
    fn(a) match {
      case None => None
      case Some(Left(a1)) => tailRecM(a1)(fn)
      case Some(Right(b)) => Some(b)
    }
}