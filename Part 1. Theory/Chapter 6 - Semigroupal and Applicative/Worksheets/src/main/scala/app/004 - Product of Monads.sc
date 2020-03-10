/** 6.3.1.1 Exercise: The Product of Monads */
import cats.Monad
import cats.syntax.flatMap._  // flatMap
import cats.syntax.functor._ // map

def product[M[_]: Monad, A, B](x: M[A], y: M[B]): M[(A, B)] =
  x.flatMap(xx => y.map(yy => (xx, yy)))

def product[M[_]: Monad, A, B](x: M[A], y: M[B]): M[(A, B)] = for {
  xx <- x
  yy <- y
} yield (xx, yy)