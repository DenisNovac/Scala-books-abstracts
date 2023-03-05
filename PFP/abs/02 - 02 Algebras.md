# Algebras

Algebra describes a new language (DSL) within a host language.

They could be encoded through TF, interfaces with single-kinded type, class with methods, case class... 

```scala
trait Items[F[_]] {
  def getAll: F[List[Item]]
  def add(item: Item): F[Unit]
}
```

This is a tagless final aencoded algebra. Not the same as typeclass (while both are using TF). Difference: typeclasses should have coherent instances, tagless algebras could have many implementations (*interpreters*).

Tagless Algebras is an interfaces that abstracts over effect type. TA should not have typeclass constraints. Typeclass constraints define capabilities which is belongs to programs and interpreters and Algebras should be abstract.

## Naming conventions

ItemService, ItemAlgebra, ItemAlg, etc...

```scala
trait ItemService[F[_]] {
  def getAll: F[List[Item]]
  def add(item: Item): F[Unit]
}
```

## Interpreters

Algebra would have two interpreters: one for testing and one for real things. Implementations with ref might be a testing interpreter while Redis might be a real interpreter.

## Programs

Programs are using algebras and programs to describe business logic. They could have type constraints, but no state, its pure business logic.

```scala
class ItemCounter[F[_]: Apply](counter: Counter[F], items: Items[F]) {

  def addItem(item: Item): F[Unit] =
    items.add(item) *>
      counter.incr
}

```

We only use `Apply` here to constraint ourselves to the least powerful typeclass which suits our needs. Also `*>` could be done sequentially or parallelly depending on typeclass instance. To ensure sequential composition we could use `FlatMap`.

Programs could also be a functions or be a set of combined functions:

```scala
def program[F[_]: Console: Monad]: F[Unit]

class MasterMind[F[_]: Console: Monad] (items: IntemsCounter[F], counter[F]) {

  private def addItem(item: Item): F[Unit] =
    items.add(item) *>
      counter.incr

  def logic(item: Item): F[Unit] = 
    for {
      _ <- items.addItem(item)
      c <- counter.get
      _ <- Console[F].println("counter: " + c)
    } yield ()
}
```

But they are always describing pure buisiness logic and nothing else (no database queries, states, etc).

## Business logic

What is a business logic?

- Combine pure computations in terms of tagless algebras and programs;
  - only doing what effect constraints allows;
- perform logging (or other console things) only via tagless algebra.


