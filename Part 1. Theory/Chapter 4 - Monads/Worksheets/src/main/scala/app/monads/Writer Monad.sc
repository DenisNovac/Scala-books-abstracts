import cats.data.Writer
import cats.instances.vector._

Writer(
  Vector(
    "It was best of times",
    "It was worst of times"
  ),
  1859
)

import cats.syntax.applicative._

type Logged[A] = Writer[Vector[String], A]

(Vector("123"),123).pure[Logged]



import cats.syntax.writer._

Vector("msg1", "msg2").tell


val a = Writer(Vector("msg1", "msg2"), 123)

a.value
a.written

a.run