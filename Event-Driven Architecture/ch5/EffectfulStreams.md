# Effectful streams

Fs2 streams. Our distributed system will be streams-powered.

## Finite state machine

Abstract machine that can be eactly one of finite number of states at a moment of time. Can change state in response to items (transition). Defined by list of states, initial states and inputs.

```scala
import cats.syntax.all.*
import cats.{Functor, Id}

case class FSM[F[_], S, I, O](run: (S, I) => F[(S, O)]):
  def runS(using F: Functor[F]): (S, I) => F[S] =
    (s, i) => run(s, i).map(_._1)

object FSM:
  def id[S, I, O](run: (S, I) => Id[(S, O)]) = FSM(run)

```

`run` takes a state `S` and input `I` and produce new state `S` and output `O`.

`runS` runs state dicarding output, produces only new state.

FSM could be written without any effects (just by ID) and they are easy to test (by feeding initial state and assertions on output state).

### Streams integration

Fs2 provides functions that fit the shape of FSM's run:

```scala
def mapAccumulate[S, O](s: S)(f: (S, I)  => (S, O)): Stream[F, (S, O)]
def evalMapAccumulate[S, O](s: S)(f: (S, I)  => F[(S, O)]): Stream[F, (S, O)]
```

With FSM it coulld be like this:

```scala
val commands: Stream[IO, TradeCommand | SwitchCommand] = ???
commands.evalMapAccumulate(TradeState.empty)(fsm.run)
```

## Lifecycle

Lifecycle of such app could be inside a stream:

```scala
def run: IO[Unit] =
  Stream
    .resource(resources)
    .flatMap { (serverRes, redisRes)  =>
      Stream.eval(serverRes.useForever).concurrently {
        Stream.resource(redisRes).evalMap { redis  =>
          serviceOne(redis)  *> serviceTwo(redis)
        }
} }
    .compile
    .drain
```

## Data pipelines

Different pipelines shoul be trated differently. E.g. pipeline for real-time data should not be used as batch-processing pipeline (batches are slow for real-time, real-time is fast for batching - e.g. writing to database).

*Fast producer/slow consumer* scenario - writing messages to db, will require batching.

In such cases we could just make two consumers of topic - one of real-time and one for batching so they could consume the topic with required speed without affecting each-other.

If batching depends on real-time - we could push real-time results to separate topic (listen-to-yourself).

fs2.data - library for processing known file formats (csv, xml, json...)

For databases streams need to be implemented on client (doobie, skunk)

fs2.io.net - networking, raw tcp and udp
fs2-grpc - grpc

### Parallel run

```scala
def run: IO[Unit] =
  Stream
    .resource(resources)
    .flatMap { (consumer, topic, server)  =>
      val http =
        Stream.eval(server.useForever)
      val subs =
        topic.subscribers.evalMap { n  =>
          Logger[IO].info(s"WS connections: $n")
        }
      val alerts =
        consumer.receive.through(topic.publish)

      // here we run three Stream program independently
      // the whole stream will fail only in case of failure, but not in case of errors of substreams
      Stream(http, subs, alerts).parJoin(3)
    }
    .compile
    .drain
```





