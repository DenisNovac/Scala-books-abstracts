import cats.effect.kernel.Ref
import cats.Functor
import cats.syntax.functor._

// state should be covered with some interface to restrict usages
trait Counter[F[_]] {
  def incr: F[Unit]
  def get: F[Int]
}

object Counter {
  def make[F[_]: Functor: Ref.Make]: F[Counter[F]] =
    // this ref is sealed inside of Counter instance now so it is impossible to access it outside
    // state shall not leak, it could be misused if it was an argument to make
    Ref.of[F, Int](0).map { ref =>
      new Counter[F] {
        override def incr: F[Unit] = ref.update(_ + 1)
        override def get: F[Int]   = ref.get
      }
    }
}
