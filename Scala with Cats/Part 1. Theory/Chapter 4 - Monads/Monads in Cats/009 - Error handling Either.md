# Обработка ошибок через монаду Either

Either обычно используется для имплементации **fail-fast** обработки ошибок. Это подход, при котором приложение прекращает работу и сообщает об ошибке. 

Мы делаем вычисления последовательно через `flatMap`. Если что-то не сработало - оставшийся код не работает:

```scala
for {
    a <- 1.asRight[String]
    b <- 0.asRight[String]

    c <- if (b == 0) "DIV0".asLeft[Int]
        else (a / b).asRight[String]
  } yield c * 100
// res0: scala.util.Either[String,Int] = Left(DIV0)
```

Когда мы используем `Either` - нам нужно определить, какой тип представляет ошибки. Мы можем воспользоваться `Throwable`: 

```scala
type Result[A] = Either[Throwable, A]
```

Это позволит нам пользоваться семантикой, похожей на `scala.util.Try`. Проблема в том, что `Throwable` - слишком широкое понятие.

Другой подход - определить алгебраический тип данных для отображения ошибок:

```scala
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

result1.fold(handleError, println)  // User(dave,password)
result2.fold(handleError, println)  // User not found dave
```

Такой подход даёт конечный набор ожидаемых ошибок. Ну и Pattern Matching помогать будет.
