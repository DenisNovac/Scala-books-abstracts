import scala.annotation.nowarn
import eu.timepit.refined.api.RefinedTypeOps
import io.estatico.newtype.macros._
import eu.timepit.refined.types.string.NonEmptyString
import eu.timepit.refined.auto._
import cats.data.EitherNel
import cats.implicits._

type UserNameR = NonEmptyString
object UserNameR extends RefinedTypeOps[UserNameR, String]

type NameR = NonEmptyString
object NameR extends RefinedTypeOps[NameR, String]

type EmailR = NonEmptyString
object EmailR extends RefinedTypeOps[EmailR, String]

@newtype case class UserName(private val value: UserNameR)
@newtype case class Name(private val value: NameR)
@newtype case class Email(private val value: EmailR)

@nowarn
final case class Person(
    username: UserName,
    name: Name,
    email: Email
)

object Manual {

  // need one more map since newtype are in place
  def mkPerson(
      u: String,
      n: String,
      e: String
  ): EitherNel[String, Person] = (
    UserNameR.from(u).toEitherNel.map(UserName.apply),
    NameR.from(n).toEitherNel.map(Name.apply),
    EmailR.from(e).toEitherNel.map(Email.apply)
  ).parMapN(Person.apply)

}

object Auto {

  object NewtypeRefinedOps {
    import io.estatico.newtype.Coercible
    import io.estatico.newtype.ops._
    import eu.timepit.refined.refineV
    import eu.timepit.refined.api.Refined
    import eu.timepit.refined.api.Validate

    final class NewtypeRefinedPartiallyApplied[A] {
      def apply[T, P](raw: T)(implicit
          c: Coercible[Refined[T, P], A],
          v: Validate[T, P]
      ): EitherNel[String, A] =
        refineV[P](raw).toEitherNel.map(_.coerce[A])
    }

    def validate[A]: NewtypeRefinedPartiallyApplied[A] =
      new NewtypeRefinedPartiallyApplied[A]
  }

  import NewtypeRefinedOps._

  def mkPerson(
      u: String,
      n: String,
      e: String
  ): EitherNel[String, Person] =
    (
      validate[UserName](u),
      validate[Name](n),
      validate[Email](e)
    ).parMapN(Person.apply)
}

// ok
Manual.mkPerson("den", "Denis", "test@email.com")

Manual.mkPerson("", "", "")
// res1: EitherNel[String, Person] = Left(
//  value = NonEmptyList(
//    head = "Predicate isEmpty() did not fail.",
//    tail = List("Predicate isEmpty() did not fail.", "Predicate isEmpty() did not fail.")
//  )
// )

Auto.mkPerson("den", "Denis", "test@email.com")
Auto.mkPerson("", "", "")
