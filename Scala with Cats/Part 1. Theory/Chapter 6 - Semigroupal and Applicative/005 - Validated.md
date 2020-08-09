# Validated

Невозможно реализовать поведение накопления ошибок через монады - они задизайнены так, чтобы реализовывать fail-fast поведение.

Cats предоставляет тип `Validated`, который является Semigroupal, но не монадой.

## Создание Validated

```scala
/** Validated */

import cats.Semigroupal
import cats.data.Validated
import cats.instances.list._


type AllErrorsOr[A] = Validated[List[String], A]
Semigroupal[AllErrorsOr].product(
  Validated.invalid(List("Error 1")),
  Validated.invalid(List("Error 2"))
)
// res0: AllErrorsOr[(Nothing, Nothing)] = Invalid(List(Error 1, Error 2))




/** Создание инстансов напрямую из подтипов */
val v = Validated.Valid(123)  // v: cats.data.Validated.Valid[Int] = Valid(123)
val i = Validated.Invalid(List("Badness"))  // i: cats.data.Validated.Invalid[List[String]] = Invalid(List(Badness))




/** Создание через методы */
/** Методы умны и возвращают супертип Validated, а не подтипы */
/** Синтаксис похож на Either - есть Left и Right */
val vv = Validated.valid[List[String], Int](123)  // vv: cats.data.Validated[List[String],Int] = Valid(123)
val ii = Validated.invalid[List[String], Int](List("Badness"))  // ii: cats.data.Validated[List[String],Int] = Invalid(List(Badness))




/** Синтаксис */
import cats.syntax.validated._

123.valid[List[String]]  // res1: cats.data.Validated[List[String],Int] = Valid(123)
List("Badness").invalid[Int]  // res2: cats.data.Validated[List[String],Int] = Invalid(List(Badness))

/** Как и у Either тип принимает тип противоположного аргумента */





/** Через applicative и applicativeError */
import cats.syntax.applicative._
import cats.syntax.applicativeError._

type ErrorsOr[A] = Validated[List[String], A]

123.pure[ErrorsOr]  // res3: ErrorsOr[Int] = Valid(123)
List("Badness").raiseError[ErrorsOr, Int]  // res4: ErrorsOr[Int] = Invalid(List(Badness))





/** Из других типов */
Validated.catchOnly[NumberFormatException]("foo".toInt)  
// res5: cats.data.Validated[NumberFormatException,Int] = Invalid(java.lang.NumberFormatException: For input string: "foo")

Validated.catchNonFatal(sys.error("Badness"))  
// res6: cats.data.Validated[Throwable,Nothing] = Invalid(java.lang.RuntimeException: Badness)

Validated.fromTry(scala.util.Try("foo".toInt)) 
 // res7: cats.data.Validated[Throwable,Int] = Invalid(java.lang.NumberFormatException: For input string: "foo")

Validated.fromEither[String, Int](Left("Badness"))  
// res8: cats.data.Validated[String,Int] = Invalid(Badness)

Validated.fromOption[String, Int](None, "Badness")  
// res9: cats.data.Validated[String,Int] = Invalid(Badness)
```