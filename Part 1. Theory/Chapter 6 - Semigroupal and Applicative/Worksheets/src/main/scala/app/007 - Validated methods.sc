import cats.Semigroupal
import cats.data.Validated
import cats.instances.list._
import cats.data.NonEmptyVector
import cats.syntax.validated._
import cats.instances.vector._
import cats.syntax.apply._

123.valid.map(_ * 100)
"?".invalid.leftMap(_.toString)
123.valid[String].bimap(_ + "!", _ * 100)
"?".invalid[Int].bimap(_ + "!", _ * 100)
//res0: cats.data.Validated[Nothing,Int] = Valid(12300)
//res1: cats.data.Validated[String,Nothing] = Invalid(?)
//res2: cats.data.Validated[String,Int] = Valid(12300)
//res3: cats.data.Validated[String,Int] = Invalid(?!)




/** flatMap отсутствует, но есть andThen */

32.valid.andThen { a =>
  10.valid.map{ b =>
    a + b
  }
}  // res4: cats.data.Validated[Nothing,Int] = Valid(42)


/** Конверция между Either и Validated */

import cats.syntax.either._
"Badness".invalid[Int]
"Badness".invalid[Int].toEither
"Badness".invalid[Int].toValidatedNec
"Badness".invalid[Int].toValidatedNel

//res5: cats.data.Validated[String,Int] = Invalid(Badness)
//res6: Either[String,Int] = Left(Badness)
//res7: cats.data.ValidatedNec[String,Int] = Invalid(Chain(Badness))
//res8: cats.data.ValidatedNel[String,Int] = Invalid(NonEmptyList(Badness))


"fail".invalid[Int].getOrElse(0)  // 0

"fail".invalid[Int].fold(_ + "!!!", _.toString)  // res10: String = fail!!!

