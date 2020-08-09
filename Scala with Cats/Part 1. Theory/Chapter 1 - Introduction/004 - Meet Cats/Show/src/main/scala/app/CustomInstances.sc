import cats.Show
import java.util.Date
import cats.syntax.all._

implicit val dateShow: Show[Date] =
  Show.show(date => s"${date.getTime} ms since the epoch.")

val d = new Date
d.setTime(1232323423L)

dateShow.show(d)
d.show
