# Anti-patterns

## Seq as a base trait

Seq is a generic representation - List, Vector and Stream are implementing it while being a completely different structures in reality.

For example, `Seq#toList` used on `LazyList` will load everything in memory at once.

More specific datatypes are better and allows to precisely optimize algorythms.

## Monad transformers in interface

In interfaces `F[Option[User]]` is more desirable than `OptionT[F, User]`. It is not composable in `F` flatmaps in cases where we have, for example, both EitherT and OptionT. 

Alternatively we could use exceptions instead of Option:

```scala
case object UserNotFound extends NoStackTrace

def make[F[_]: ApplicativeThrow]: Users[F] = 
  new Users[F] {
    def findUser(id: String): F[User] = // no Option here, but ApplicativeThrow in implicit parameters
      if (id == adminUser.id) adminUser.pure[F]
      else UserNotFound.raiseError[F]
  }
```

Also `NoStackTrace` is the best option for domain errors since it much cheaper than stack trace on JVM.

## Boolean blindness

```scala
def filter(p: A => Boolean): List[A]
```

We can't say if it keeps or discards the predicate values according to type signature. We can introduce an ADT to make predicates more clear.

```scala
sealed trait Predicate
object Predicate {
  case object Keep extends Predicate
  case object Discard extends Predicate
}

def filterBy(p: A => Predicate): List[A]

List.range(1, 11)
  .filterBy { n => // assumed to be extension methods
    if (n > 5) Perdicate.Keep else Predicate.Discard
  }
```

Of course, it would be too hard to abstract over every library functions but for critical places it could be a good thing.




