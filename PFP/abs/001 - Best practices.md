# Chapter 1 - Best Practices

## Value classes

```scala
case class Username(val value: String) extends AnyVal
case class Email(val value: String) extends AnyVal

def lookup(username: Username, email: Email)


lookup(Username("a@gmail.com"), Email("Denis")) // we still can make mistake

// private constructors:

case class Username private(val value: String) extends AnyVal
case class Email private(val value: String) extends AnyVal

def mkUsername(value: String): Option[Username] =
  (value.nonEmpty).guard[Option].as(Username(value))

def mkEmail(value: String): Option[Email] =
    (value.contains("@")).guard[Option].as(Email(value))


// but we still can do .copy since thats a case classes so:
sealed abstract case class Username private(val value: String) 
sealed abstract case class Email private(val value: String) 

// more expensive because no AnyVal now but safer
```

Also Value classes are actually instantiated when:
 - treated as another type;
 - assigned to an array;
 - doing runtime type checks such as pattern matching.

Language can't guarantee those types won't allocate the memory

## Newtypes

Newtypes might be more cheaper alternative to Value classes. Its a library which gives zero-cost wrappers with no runtime overhead. Also no `.copy` method out of the box.

```scala
import io.estatico.newtype.macros._

@newtype case class Username(private val value: String)
@newtype case class Email(private val value: String)

Email("test") // still need smart constructor
```

## Refinement types

Newtypes doesn't make any validation but refined library does:

```scala
import io.estatico.newtype.macros._
import eu.timepit.refined.api.Refined
import eu.timepit.refined.types.string.NonEmptyString
import eu.timepit.refined.collection.Contains
import eu.timepit.refined.auto._

@newtype case class Username(private val value: NonEmptyString)
@newtype case class Email(private val value: String Refined Contains['@'])

// compile-time validation and zero-cost wrapper
Username("denis")
Email("test@gmail.com")
```

Thus we are getting compile-time validation which doesn't affect runtime memory consumption.

## Runtime validation

```scala
val str: String = "some runtime value"

def validate(str: String): Either[String, NonEmptyString] =
  NonEmptyString.from(str)

validate(str) // ok
validate("") // error
```

### Validated

Refined types are making validation through Either which is Monad and will fail on any error. `cats.data.Validated` is Applicative so it will *accumulate* errors.

```scala
import cats.data.ValidatedNel
import cats.implicits._

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
```

`.toEitherNel` with `parMapN` (parallel exec but for monads) might be an alternative, it will return Left instead of Invalid.

To accumulate errors we should have `Semigroup` instance for error type, here it is `String` and it is included in cats library.

Example with newtype and toEitherNel:

```scala
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

// ok
mkPerson("den", "Denis", "test@email.com")

mkPerson("", "", "")
//res1: EitherNel[String, Person] = Left(
//  value = NonEmptyList(
//    head = "Predicate isEmpty() did not fail.",
//    tail = List("Predicate isEmpty() did not fail.", "Predicate isEmpty() did not fail.")
//  )
//)

```

That's a lot of boilerplate but i could actually be reduced to generic methods with `refineV` and `coerce`:


```scala
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
```

## State

Interface should know nothing about state

If we use somehting like `Ref` directly, function may access sand modify state at any time.

```scala
import cats.effect.kernel.Ref
import cats.Functor
import cats.syntax.functor._

// state should be covered with some interface to restrict usages
trait Counter[F[_]] {
  def incr: F[Unit]
  def get: F[Int]
}

object Counter {
  def make[F[_]: Functor: Ref.Make]: F[Counter[F]] =
    // this ref is sealed inside of Counter instance now so it is impossible to access it outside
    // state shall not leak, it could be misused if it was an argument to make
    Ref.of[F, Int](0).map { ref =>
      new Counter[F] {
        override def incr: F[Unit] = ref.update(_ + 1)
        override def get: F[Int]   = ref.get
      }
    }
}
```

### Sequential vs concurrent state

`State` monad is a sequential state while `Ref` is concurrent-safe. `State` monad won't work concurrently, it is not safe.

## Shared state

### Regions of sharing

Regions of sharing are denoted by `flatMap` call.

```scala

private def p1(sem: Semaphore[IO]): IO[Unit] =
  sem.permit.surround(IO.println("Running P1")) >>
    randomSleep

private def p2(sem: Semaphore[IO]): IO[Unit] =
  sem.permit.surround(IO.println("Running P2")) >>
    randomSleep

override def run: IO[Unit] =
  Supervisor[IO].use { s =>
    Semaphore[IO](1).flatMap { sem => // sharing with both p1 and p2
      /* region of sharing starts */

      s.supervise(p1(sem).foreverM).void *>
        s.supervise(p2(sem).foreverM).void *>
        IO.sleep(5.seconds).void

      /* region of sharing ends */
    }
  }
```

It allows us to reason where something is used since now it is passed as argument.
That is why concurrent data structures wrapped in `F` mostly.



