import eu.timepit.refined.api.RefinedTypeOps
import eu.timepit.refined.numeric.Greater
import io.estatico.newtype.macros._
import eu.timepit.refined.api.Refined
import eu.timepit.refined.types.string.NonEmptyString
import eu.timepit.refined.collection.Contains
import eu.timepit.refined.auto._
import cats.data.ValidatedNel
import cats.implicits._

@newtype case class Username(private val value: NonEmptyString)
@newtype case class Email(private val value: String Refined Contains['@'])

// compile-time validation and zero-cost wrapper
Username("denis")
Email("test@gmail.com")

// runtime validation

val str: String = "some runtime value"

def validate(str: String): Either[String, NonEmptyString] =
  NonEmptyString.from(str)

validate(str)
validate("")

// validated

type GTFive = Int Refined Greater[5]
object GTFive extends RefinedTypeOps[GTFive, Int] // for .from method

case class MyType(a: NonEmptyString, b: GTFive)

def validate(a: String, b: Int): ValidatedNel[String, MyType] =
  (
    NonEmptyString.from(a).toValidatedNel,
    GTFive.from(b).toValidatedNel
  ).mapN(MyType.apply)

// ok
validate("Name", 6)

// res5: ValidatedNel[String, MyType] = Invalid(e = NonEmptyList(head = "Predicate isEmpty() did not fail.", tail = List("Predicate failed: (1 > 5).")))
validate("", 1)
