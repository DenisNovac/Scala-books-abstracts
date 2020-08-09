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