scalaVersion := "2.13.8"
organization := "ch.epfl.scala"
version      := "0.1"

lazy val root = (project in file("."))
  .settings(
    name := "code",
    libraryDependencies ++= Seq(
      compilerPlugin(
        ("org.typelevel" %% "kind-projector" % "0.13.2").cross(CrossVersion.full)
      ),
      Dependencies.newtype,
      Dependencies.refined
    ),
    libraryDependencies ++= Dependencies.cats,
    libraryDependencies ++= Dependencies.catsEffect,
    scalacOptions ++= Seq(
      "-Ymacro-annotations",
      "-Wconf:cat=unused:info"
    )
  )
