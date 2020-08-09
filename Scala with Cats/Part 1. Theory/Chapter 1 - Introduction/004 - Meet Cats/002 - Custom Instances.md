# Кастомные инстансы

Можно определять кастомные имплисивные инстансы напрямую руками:

```scala
import cats.Show
import java.util.Date
import cats.syntax.all._

implicit val dateShow: Show[Date] =
  new Show[Date] {
    def show(date: Date): String =
      s"${date.getTime} ms since the epoch."
  }

val d = new Date()
d.setTime(132342344L)
dateShow.show(d) // res1: String = 132342344 ms since the epoch.
d.show // res2: String = 132342344 ms since the epoch.
```

Но Cats позволяет и упростить процесс. Есть два метода для конструирования объектов-компаньонов Show:

```scala
object Show {

// Convert a function to a `Show` instance:
def show[A](f: A => String): Show[A] =
  ???

// Create a `Show` instance from a `toString` method:
def fromToString[A]: Show[A] =
  ???
}
```

Как видно, `show` принимает функцию, что очень удобно для нашей задачи:

```scala
import cats.Show
import java.util.Date
import cats.syntax.all._

implicit val dateShow: Show[Date] =
  Show.show(date => s"${date.getTime} ms since the epoch.")

val d = new Date
d.setTime(1232323423L)

dateShow.show(d)  // res1: String = 1232323423 ms since the epoch.
d.show  // res2: String = 1232323423 ms since the epoch.
```

Многие тайпклассы в Cats поддерживают такое создание инстансов.
