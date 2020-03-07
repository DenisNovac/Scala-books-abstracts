import cats.data.Reader
import cats.syntax.all._

case class Db(
    usernames: Map[Int, String],
    passwords: Map[String, String]
)

type DbReader[T] = Reader[Db, T]

/** Методы, которые генерируют ридеры для поиска логина и пароля */

  def findUsername(userId: Int): DbReader[Option[String]] =
    Reader { db =>
      db.usernames.find(user => user._1 == userId).map(user => user._2)
    }

def checkPassword(
      username: String,
      password: String
  ): DbReader[Boolean] =
    Reader { db =>
      db.passwords.exists(user => user._1 == username & user._2 == password)
    }

def checkLogin(userId: Int, password: String): DbReader[Boolean] =
    for {
      user <- findUsername(userId)
      passwordOk <- user
                     .map { username =>
                       checkPassword(username, password)
                     }
                     .getOrElse(false.pure[DbReader])
    } yield passwordOk


val users = Map(
  1 -> "dude",
  2 -> "kate"
)

val passwords = Map(
  "dude" -> "123",
  "kate" -> "iloveyou"
)

val db = Db(users, passwords)
checkLogin(1, "123").run(db)


