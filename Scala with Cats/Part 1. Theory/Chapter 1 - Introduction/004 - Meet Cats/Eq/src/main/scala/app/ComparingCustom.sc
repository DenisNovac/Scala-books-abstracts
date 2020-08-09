/**
  * Можно определять инстансы для кастомных типов через Eq.instance, который принимает
  * (A,A) => Boolean и возвращает Eq[A]
  * */

import java.util.Date

import cats.Eq
import cats.instances.long._
import cats.syntax.eq._

implicit val dateEq: Eq[Date] =
  Eq.instance[Date] {
    (date1, date2) =>
      date1.getTime === date2.getTime
  }

val x = new Date()  // сейчас
val y = new Date()  // попозже

x === x
x === y

