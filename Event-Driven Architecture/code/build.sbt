import Dependencies._

val scala3Version = "3.2.2"

lazy val root = project
  .in(file("."))
  .settings(
    name         := "code",
    version      := "0.1.0-SNAPSHOT",
    scalaVersion := scala3Version
  )
  .settings(
    libraryDependencies ++= Seq(
      cats,
      catsEffect,
      kittens
    ) ++ Seq(
      circe,
      fs2
    ).flatten
  )
