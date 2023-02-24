import sbt._

object Dependencies {

  object Versions {
    val cats       = "2.9.0"
    val catsEffect = "3.4.8"
    val newtype    = "0.4.4"
    val refined    = "0.10.1"
  }

  val cats = Seq(
    "org.typelevel" %% "cats-core"
  ).map(_ % Versions.cats)

  val catsEffect = Seq(
    "org.typelevel" %% "cats-effect"
  ).map(_ % Versions.catsEffect)

  val newtype = "io.estatico" %% "newtype" % Versions.newtype
  val refined = "eu.timepit"  %% "refined" % Versions.refined
}
