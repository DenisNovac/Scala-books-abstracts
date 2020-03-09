import cats.{Applicative, Monad}
import cats.instances.all._
import cats.implicits._
import cats.syntax.applicative._
import cats.syntax.flatMap._


def compose[M1[_]: Monad, M2[_]: Option] = {
  type Composed[A] = M1[M2[A]]

  new Monad[Composed] {
    override def pure[A](a: A): Composed[A] =
      a.pure[M2].pure[M1]

    // Как написать flatMap для неизвестной монады? Мы заранее ничего о ней не знаем
    override def flatMap[A, B](fa: Composed[A])(f: A => Composed[B]): Composed[B] =
      ??? //fa.flatMap(_.fol

    override def tailRecM[A, B](a: A)(f: A => Composed[Either[A, B]]) = ???


  }
}