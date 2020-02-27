import cats.syntax.either._

for {
    a <- 1.asRight[String]
    b <- 0.asRight[String]

    c <- if (b == 0) "DIV0".asLeft[Int]
        else (a / b).asRight[String]
  } yield c * 100

type Result[A] = Either[Throwable, A]

/** Алгебраические типы для снижения непоняток при выбрасывании ошибки */
sealed trait LoginError extends Product with Serializable

final case class UserNotFound(username: String) extends LoginError
final case class PasswordIncorrect(username: String) extends LoginError
case object UnexpectedError extends LoginError

case class User(username: String, password: String)

type LoginResult = Either[LoginError, User]

def handleError(error: LoginError): Unit =
    error match {
      case UserNotFound(u)      => println(s"User not found $u")
      case PasswordIncorrect(u) => println(s"Password incorrect $u")
      case UnexpectedError      => println("Unexpected error")
    }

val result1: LoginResult = User("dave", "password").asRight
val result2: LoginResult = UserNotFound("dave").asLeft

result1.fold(handleError, println)
result2.fold(handleError, println)
