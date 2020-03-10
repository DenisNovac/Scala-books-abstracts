/** Semigroupal applied to different types */



/** Future */
import cats.Semigroupal
import cats.instances.future._ // for Semigroupal
import scala.concurrent._
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

/** Они начинают вычисляться в момент, когда мы их создаём */
/** Поэтому на момент вызова product они уже вычисляют результаты */
  val futurePair = Semigroupal[Future].product(Future("Hello"), Future(123))

Await.result(futurePair, 1.second) // res0: (String, Int) = (Hello,123)

import cats.syntax.apply._

case class Cat(
    name: String,
    year: Int,
    food: List[String]
)

val futureCat = (
    Future("Garfield"),
    Future(1978),
    Future(List("Lasagne"))
  ).mapN(Cat.apply)

Await.result(futureCat, 1.second) // res1: Cat = Cat(Garfield,1978,List(Lasagne))






/** List */
import cats.instances.list._
Semigroupal[List].product(List(1, 2), List(3, 4))
// res2: List[(Int, Int)] = List((1,3), (1,4), (2,3), (2,4))
/** Неожиданное поведение - получили комбинации вместо одного листа */






/** Either */
import cats.instances.either._

/** Мы опять получаем fail-fast поведение, хотя ожидалось, что product сможет
    * накопить сообщения об ошибках */
  type ErrorOr[A] = Either[Vector[String], A]
Semigroupal[ErrorOr].product(
    Left(Vector("Error 1")),
    Left(Vector("Error 2"))
  )
// res3: ErrorOr[(Nothing, Nothing)] = Left(Vector(Error 1))

