# Implicit vs explicit

```scala
def program[F[_]: Cache: Console: Users:
                  Monad: Items: KafkaClient: 
                  EventsPublisher: HttpClien]: F[Unit] = ???
```

This is an anti-pattern. Implicits should be used to encode tupeclass instances, but business logic algebras should always be explicit:

```scala

```

Now we have KafkaClient and HttpClient. Those are Recources (most of the time), so they should be passes explicitly since creating a resource is an effectful operations (should be done somewhere in `make`?). Same goes for cache:

```scala
def program[F[_]: Console: Monad](
                    users: Users[F], 
                    items: Items[F],
                    eventsPublisher: EventsPublisher[F],
                    cache: Cache[F],
                    kafka: KafkaClient[F],
                    client: HttpClient[F]
                  ): F[Unit] = ???

```

Now we have too many dependencies. Usually such programs are at the top of application and they don't actually using all that staff. We need more modularity.

## Modularity

We could group ragless algebras that share higher-level interface. Those are called *modules*:

```scala
package modules

trait Services[F[_]] {
  def users: Users[F]
  def items: Items[F]
}
```

Lets determine common things between our algebras:

```scala
trait Events[F[_]] {
  def manager: EventsManager[F]
  def publisher: EventsPublisher[F]
}

trait Clients[F[_]] {
  def kafka: KafkaClient[F]
  def http: HttpClient[F]
}

trait Database[F[_]] {
  def cache: Cache[F]
}
```

To build such modules we could use smart constructor which will call smart constructors of all inside modules.

Final version:

```scala
def program[F[_]: Console: Monad](
  services: Services[F],
  events: Events[F],
  cache: Cache[F],
  clients: Clients[F]
): F[Unit] = ???
```

## Implicit convenience

Some things are passed as implicits: `ContextShift`, `Clock`, `Timer`, etc (in CE2). Those are **common effects**, they do not hold business logic while not being a typeclasses.

It is done mostly for convenience to be sure the same instances are used as given from `IOApp` as environment.

Also this allows us to have different instances for testing.

```scala
def program[F[_]: Console: Monad]
```

Here Console is not a typeclass but more a common effect that is needed more than once in app.

### Capability traits

What we called common effect is also *capability traits*. We could think about constraints also like about capabilities. 



