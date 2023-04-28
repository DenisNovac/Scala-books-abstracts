import io.circe.Codec
import io.circe.syntax.*
import cats.*
import cats.derived.*
import cats.implicits.*

object Derivators {

  case class Person(
      name: String,
      age: Int
  ) derives Eq,
        Order,
        Show,
        Codec.AsObject // .AsObject needs to be here to derive

}

import Derivators.*

val p = Person("Denis", 26)

p.show

p.asJson.spaces2

// offtop

val green    = 1835523
val red      = 2316 * 10
val shuffles = 46856 * 10 + 20828 * 10 + green + red

100 * (green - 0.5 * red) / shuffles
