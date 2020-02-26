import cats.Show
import cats.instances.int._
import cats.instances.string._

val showInt = Show.apply[Int]
val showStr = Show.apply[String]

showInt.show(123)
showStr.show("abc")


import cats.syntax.show._

123.show
"abc".show


