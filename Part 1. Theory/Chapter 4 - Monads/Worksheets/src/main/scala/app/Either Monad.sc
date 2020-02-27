

val either1 = Right(10)
val either2 = Right(32)

for {
  a <- either1
  b <- either2
} yield a + b


import cats.syntax.either._ // for map and flatMap

val a = 10.asRight[String]  // String - тип левой части, без него будет Nothing
val b: Either[String, Int] = 24.asRight  // тут его можно опустить

for {
  first <- a
  second <- b
} yield first+second


Either.catchOnly[NumberFormatException]("foo".toInt)

Either.catchNonFatal(sys.error("Badness"))

Either.fromTry(scala.util.Try("foo".toInt))

Either.fromOption[String, Int](None, "None")
Either.fromOption(Some(12), "None")
