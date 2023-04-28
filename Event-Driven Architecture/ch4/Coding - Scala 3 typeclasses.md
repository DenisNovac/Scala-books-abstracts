# Coding

Some Scala 2 libraries are not available in Scala 3.

**Derevo** was written in experimental macros and can't be supported for 
Scala 3 (however, there is Kittens).

**Newtypes** are also depends on such macros, but Scala 3 ships with opaque
types which gives basic blocks for our own newtypes:

```scala
abstract class Newtype[A](using
  eqv: Eq[A],
  ord: Order[A],
  shw: Show[A]
  enc: Encoder[A],
  dec: Decoder[A]
):
  opaque type Type = A
  
  inline def apply(a: A): Type = a
  
  protected inline final def derive[F[_]](using ev: F[A]): F[Type] = ev

  extension (t: Type) inline def value: A = t

  given Wrapper[A, Type] with
    def iso: Iso[A, Type] =
      Iso[A, Type](apply(_))(_.value)

  given Eq[Type] = eqv
  given Order[Type] = ord
  given Show[Type] = shw
  given Encoder[Type] = enc
  given Decoder[Type] = dec
  given Ordering[Type] = ord.toOrdering


// typeclass allows to covert back and forth from underlying wrapper type
trait Wrapper[A, B]:
  def iso: Iso[A, B]
```

It automatically derives common typeclass instances for our newtypes.

```scala
type Name = Name.Type
object Name extends Newtype[String]

type Age = Age.Type
object Age extends Newtype[Int]
```

**Refined** only support validation in Scala 3 (at the moment of book writing) so **Iron** will be used (exclusive for Scala 3).

```scala

def log(x: Double :| Greater[0.0]): Double = 
  Math.log(x) // just a normal double here

log(-1d) // compile error

// refined type
type SymbolR = DescribedAs[
  Match["^[a-zA-Z0-9]{6}$"],
  "A Symbol should be an alphanumeric of 6 digits"
]

type Symbol = Symbol.Type
object Symbol extends Newtype[String :| SymbolR] // newtype + refine
```

## Copy method

Copy method can bypass any previous validation if we don't use any refined type. 

## Orphan instances

Typeclass instances for types we don't control (such as external library).

In Scala 2 you need to put such instances in some container such as trait or object.

In Scala 3 there is `export` clause. 
```scala
// normal Scala object
object OrphanInstances:
  given Eq[Instant] = Eq.by(_.getEpochSecond)
  given Order[Instant] = Order.by(_.getEpochSecond)
  given Show[Instant] = Show.show[Instant](_.toString)

// in scala there is no package objects since we can do everything on top-level

package domain

export OrphanInstances.given

// to get access
import domain.given

```

## Typeclasses

`using` and `given` instead of `implicit`.
`summon` instead of `implicitly`.

## Match types

Enables dependent typing

```scala
type GraphSt[In] = In match
  case String => CatState
  case Int    => DogState
  case Long   => FoxState
```
