# Методы Validated

Validated включает многие методы, которые имеет `cats.syntax.either` и обычный Either.

```scala
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


```

## Упражнение: валидация формы

```scala
/** 6.4.4 Exercise: Form Validation */

/** Валидация юзера */
case class User(name: String, age: Int)

/**
  * Имя и возраст указаны;
  * Имя непустое;
  * Возраст неотрицателен.
  * */


def readName(map: Map[String, String]): Either[List[String], String] = {
  val n = map.get("name")
  n match {
    case Some(x) if !x.isBlank => Right(x)
    case _ => Left(List("The name is empty"))
  }
}

def readAge(map: Map[String, String]): Either[List[String], Int] = {
  val n = map.get("age")
  try {
    n match {
      case Some(x) if x.toInt > 0 => Right(x.toInt)
      case Some(x) if x.toInt <= 0 => Left(List("The age can not be negative"))
      case _ => Left(List("The age is incorrect"))
    }
  } catch {
    case e: NumberFormatException => Left(List("The age is incorrect"))
  }

}


import cats.syntax.either._
import cats.syntax.apply._
import cats.instances.list._

def validation(data: Map[String, String]) = (
  readName(data).toValidated,
  readAge(data).toValidated
).mapN(User.apply)



validation(Map("name" -> "Dave", "age" -> "37"))
// res0: cats.data.Validated[List[String],User] = Valid(User(Dave,37))

validation(Map("name" -> "", "age" -> "37"))
validation(Map("name" -> "Dave", "age" -> ""))
validation(Map("name" -> "", "age" -> ""))
//res1: cats.data.Validated[List[String],User] = Invalid(List(The name is empty))
//res2: cats.data.Validated[List[String],User] = Invalid(List(The age is incorrect))
//res3: cats.data.Validated[List[String],User] = Invalid(List(The name is empty, The age is incorrect))
```