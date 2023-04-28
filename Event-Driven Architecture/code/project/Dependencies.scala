import sbt._

object Dependencies {

  object Versions {
    val cats       = "2.9.0"
    val catsEffect = "3.4.9"
    val circe      = "0.14.5"

    val fs2 = "3.6.1"

    val kittens = "3.0.0"

  }

  val cats       = "org.typelevel" %% "cats-core"   % Versions.cats
  val catsEffect = "org.typelevel" %% "cats-effect" % Versions.catsEffect

  val circe = Seq(
    "io.circe" %% "circe-core",
    "io.circe" %% "circe-generic",
    "io.circe" %% "circe-parser"
  ).map(_ % Versions.circe)

  val fs2 = Seq(
    "co.fs2" %% "fs2-core"
  ).map(_ % Versions.fs2)

  val kittens = "org.typelevel" %% "kittens" % Versions.kittens

}
