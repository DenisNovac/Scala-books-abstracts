import cats.{Applicative, Apply, Functor}
case class Cat[A](id: A)

/** Apply: Functor + Semigroupal с добавлением ap */
class CatApply extends Apply[Cat] {

  /** Применить функцию к некоторому типу (map с ручной обёрткой в тип) */
  override def ap[A, B](ff: Cat[A => B])(fa: Cat[A]): Cat[B] = {
    val func: A => B = ff.id
    Cat(func(fa.id))
  }

  // Оверрайд map из functor
  //override def map[A, B](fa: Cat[A])(f: A => B): Cat[B] = Cat(f(fa.id))

  // Правильнее здесь было бы воспользоваться ap
  override def map[A, B](fa: Cat[A])(f: A => B): Cat[B] = ap(Cat[A => B](f))(fa)
}

implicit val apply = new CatApply
def f(s: String): Int = s.length

// нужно явно показать, что в Cat передаётся функция
Apply[Cat].ap(Cat[String => Int](_.length))(Cat("Sam"))
// либо неанонимную, но тогда явно указать, что это передача функции без вызова, написав f(_)
Apply[Cat].ap(Cat(f(_)))(Cat("Sam"))

Apply[Cat].ap(Cat[String => String](_ => "Joseph"))(Cat("Sam"))  // Cat(Joseph)

// Можно вызвать и map
Apply[Cat].map(Cat("Stella"))(name => (name.map(_ + "a")).mkString(""))  // Cat(Sataealalaaa)

/** Apply позволяет делать примерно то же, что и map, но функцию нужно сначала обернуть в тип
 *  При этом map нужно оверрайдить руками. Applicative делает всё то же самое, но map оверрайдить не надо.
 * */

import cats.syntax.apply._

// Выполнить то, что слева и заменить на то, что справа
Cat("Sammy") *> Cat("Not sammy")  // Cat(Not sammy)

import cats.instances.either._
import cats.syntax.either._

val a = "a".asRight[String]
val z = "ERROR".asLeft[String]
val b = "b".asRight[String]
val c = "c".asRight[String]


a *> b  // Right(b)
b *> a  // Right(a)
a *> z  // Left(ERROR)
// Пока всё как раньше, но

// Если слева была ошибка - замены не произойдёт
z *> a  // Left(ERROR)









/** Applicative добавляет pure, а map уже захардкожен через ap и pure */

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

import cats.syntax.applicative._
"Stan".pure[Dog] // pure - это синтаксис Applicative

import cats.syntax.functor._
Dog("Stan").map(_.length)  // Map - это синтаксис functor

/**
 * Это происходит т.к. Applicative является подтипом Functor. Поэтому он подходит туда, где имплисивно
 * ожидается инстанс Functor (например, в синтаксисе cats.syntax.functor добавляется map и ожидается Functor.
 *
 * Разница между Applicative и Functor в том, что Applicative добавляет Map, но не просит её реализовывать, а
 * строит самостоятельно по более низкоуровневым правилам (через оборачивание pure и ap).
 * */