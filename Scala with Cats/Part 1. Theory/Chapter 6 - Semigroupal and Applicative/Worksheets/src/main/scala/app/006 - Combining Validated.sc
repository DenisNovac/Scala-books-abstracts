/** Combining Validated */

import cats.data.NonEmptyVector
import cats.syntax.validated._
import cats.instances.vector._
import cats.syntax.apply._

/** Лучший вариант для накопления ошибок - какая-нибудь коллекция */
(
  Vector(404).invalid[Int],
  Vector(500).invalid[Int]
).tupled
// res0: cats.data.Validated[scala.collection.immutable.Vector[Int],(Int, Int)] = Invalid(Vector(404, 500))

/** Специальные NonEmptyList и NonEmptyVector используются, чтобы не выдать ошибку на пустом листе */
(
  NonEmptyVector.of("Error 1").invalid[Int],
  NonEmptyVector.of("Error 2").invalid[Int]
).tupled
// res1: cats.data.Validated[cats.data.NonEmptyVector[String],(Int, Int)] = Invalid(NonEmptyVector(Error 1, Error 2))





