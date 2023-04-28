import cats.Applicative
import cats.effect.{ExitCode, IO, IOApp, Resource}
import cats.effect.std.Queue
import fs2.Stream

import scala.concurrent.duration.*

trait Producer[F[_], A]:
  def send(a: A): F[Unit]
  def send(a: A, properties: Map[String, String]): F[Unit]

trait Acker[F[_], A]:
  def ack(id: Consumer.MsgId): F[Unit]
  def ack(ids: Set[Consumer.MsgId]): F[Unit]
  def nack(id: Consumer.MsgId): F[Unit]

trait Consumer[F[_], A] extends Acker[F, A]:
  def receiveM: Stream[F, Consumer.Msg[A]]
  def receiveM(id: Consumer.MsgId): Stream[F, Consumer.Msg[A]]
  def receive: Stream[F, A]
  def lastMsgId: F[Option[Consumer.MsgId]]

object Consumer:
  type MsgId      = String
  type Properties = Map[String, String]

  final case class Msg[A](id: MsgId, props: Properties, payload: A)

  def local[F[_]: Applicative, A](
      queue: Queue[F, Option[A]]
  ): Consumer[F, A] = new:
    def receiveM: Stream[F, Msg[A]] =
      receive.map(Msg("N/A", Map.empty, _))

    def receive: Stream[F, A] =
      Stream.fromQueueNoneTerminated(queue)

    def ack(id: Consumer.MsgId): F[Unit] = Applicative[F].unit

    def nack(id: Consumer.MsgId): F[Unit] = Applicative[F].unit

    def receiveM(id: Consumer.MsgId): Stream[F, Consumer.Msg[A]] = ???
    def ack(ids: Set[Consumer.MsgId]): F[Unit]                   = ???
    def lastMsgId: F[Option[Consumer.MsgId]]                     = ???

object Producer:
  def local[F[_]: Applicative, A](
      queue: Queue[F, Option[A]]
  ): Resource[F, Producer[F, A]] =
    Resource.make[F, Producer[F, A]](
      Applicative[F].pure(
        new:
          def send(a: A): F[Unit] = queue.offer(Some(a))

          def send(a: A, properties: Map[String, String]): F[Unit] = send(a)
      )
    )(_ => queue.offer(None))

object Fs2InternalTopics extends IOApp:
  override def run(args: List[String]): IO[ExitCode] =
    runExample.map(_ => ExitCode.Success)

  private def runExample: IO[Unit] =
    Queue.bounded[IO, Option[String]](500).flatMap { q =>
      // q is shared queue - producer will send to it and consumer read
      // something like in-memory message broker

      val consumer = Consumer.local(q)
      val producer = Producer.local(q)

      // consumer stream
      val p1 =
        consumer.receive
          .evalMap(s => IO.println(s"  >>> GOT: $s"))

      // producer stream
      val p2 =
        Stream
          .resource(producer)
          .flatMap { p =>
            Stream
              .sleep[IO](100.millis)
              .as("test")
              .repeatN(3)
              .evalMap(p.send)
          }

      IO.println("  >>> Initializing in-memory demo   <<<") *>
        p1.concurrently(p2).compile.drain
    }
