import cats.{Applicative, Apply, Functor}
case class Cat[A](id: A)

/** Apply: Functor + Semigroupal с добавлением ap */
class CatApply extends Apply[Cat] {

  /** Применить функцию к некоторому типу (map с ручной обёрткой в тип) */
  override def ap[A, B](ff: Cat[A => B])(fa: Cat[A]): Cat[B] = {
    val func: A => B = ff.id
    Cat(func(fa.id))
  }

  override def map[A, B](fa: Cat[A])(f: A => B): Cat[B] = Cat(f(fa.id))
}

implicit val apply = new CatApply
def f(s: String): Int = s.length

//Cat("Sammy").map - такого метода ещё нет

// нужно явно показать, что в Cat передаётся функция
Apply[Cat].ap(Cat[String => Int](_.length))(Cat("Sam"))
// либо неанонимную, но тогда явно указать, что это передача функции без вызова, написав f(_)
Apply[Cat].ap(Cat(f(_)))(Cat("Sam"))

Apply[Cat].ap(Cat[String => String](_ => "Joseph"))(Cat("Sam"))  // Cat(Joseph)


/** Apply позволяет делать примерно то же, что и map, но функцию нужно сначала обернуть в тип */




/** Applicative добавляет pure, благодаря которому можно вызвать map очень легко */

case class Dog[A](id: A)

class DogAppicative extends Applicative[Dog] {
  override def pure[A](x: A): Dog[A] = Dog(x)  // Обернуть тип
  override def ap[A, B](ff: Dog[A => B])(fa: Dog[A]): Dog[B] = Dog(ff.id(fa.id))
}

implicit val applicative = new DogAppicative

// Вместо оборачивания Dog[String => Int](f) можно вызвать pure
Applicative[Dog].ap(Applicative[Dog].pure(f(_)))(Dog("Samuel"))  // Dog6


// map в Applicative работает именно так: просто поверх ap
// def map[A, B](fa: F[A])(f: A => B): F[B] = ap(pure(f))(fa)

Applicative[Dog].map(Dog("Stan"))(_.length)  // Dog4

// Инстанс Applicative подходит для Functor
Functor[Dog].map(Dog("Stan"))(_.length)  // Dog4



import cats.syntax.functor._
Dog("Stan").map(_.length)  // Map - это синтаксис functor

import cats.syntax.applicative._
"Stan".pure[Dog] // pure - это синтаксис Applicative

