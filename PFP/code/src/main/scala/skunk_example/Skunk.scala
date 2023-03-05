package skunk_example

import cats.effect.IOApp
import cats.effect.{ExitCode, IO}
import cats.effect._
import cats.effect.std.Console
import cats.implicits._
import natchez.Trace.Implicits.noop
import skunk.Session
import skunk.codec.all._
import skunk.implicits._
import skunk.Fragment
import skunk.Void
import skunk.Query

object Skunk extends IOApp {

  type Pool = Resource[IO, Resource[IO, Session[IO]]]

  case class DatabaseConfig(
      host: String = "localhost",
      port: Int = 5432,
      user: String = "postgres",
      password: String = "postgres",
      database: String,
      migrateOnStart: Boolean = true
  )

  private def checkPostgresConnection(
      sessionR: Resource[IO, Session[IO]]
  ) =
    sessionR.use { session =>
      session
        .unique[String](sql"select version();".query(text))
        .flatMap { v =>
          Console[IO].println(s"Skunk connected to the Postgres: $v")
        }
    }

  private val dbConfig = DatabaseConfig(database = "postgres")

  private val sessionPool: Pool =
    Session
      .pooled[IO](
        host = dbConfig.host,
        port = dbConfig.port,
        user = dbConfig.user,
        password = Some(dbConfig.password),
        database = dbConfig.database,
        max = 10
      )
      .evalTap(checkPostgresConnection) // allows to check connection

  // private val insertCmd =
  //   sql"""
  //   INSERT INTO country
  //   VALUES ($int8, $varchar)
  //   """.command

  // private val selectCmd =
  //   sql"""
  //   SELECT * FROM country
  //   """.query(int8 ~ varchar(30))

  // or with codec
  case class Country(id: Long, name: String)
  private val countryC = (int8 ~ varchar(30)).gimap[Country]

  private val insertCmd =
    sql"""
    INSERT INTO country
    VALUES ($countryC)
    """.command

  private val selectFragment: Fragment[Void] =
    sql"""
    SELECT * FROM country
    """

  private val selectQuery =
    sql"""
    SELECT * FROM country
    """.query(countryC)

  private val whereFragment: Fragment[String] =
    sql"""
    WHERE name = $varchar
    """

  private val whereQuery: Query[String, Country] =
    (selectFragment ~> whereFragment)
      .query(countryC)

  private def executeCommands(s: Session[IO]) = for {
    _        <- s.prepare(insertCmd).flatMap { c =>
                  c.execute(Country(1L, "Paris"))
                }
    response <- s.execute(selectQuery)
    _        <- Console[IO].println(response.mkString("\n"))
    _        <- Console[IO].println("")
    where    <- s.execute(whereQuery, "Paris")
    _        <- Console[IO].println(where.mkString("\n"))
  } yield ()

  override def run(args: List[String]): IO[ExitCode] = {
    for {
      pool    <- sessionPool
      session <- pool

      _ <- Resource.eval(executeCommands(session))
    } yield ()
  }.use(_ => IO(ExitCode.Success))

}
