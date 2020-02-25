package app

/**
  * Тайпкласс
  */
trait Printable[A] {
  def myformat(value: A): String
}


/**
  * Интерфейсы в объекте-компаньоне тайпкласса
  */
object Printable {
  def myformat[A](value: A)(implicit p: Printable[A]): String =
    p.myformat(value)

  def myprint[A](value: A)(implicit p: Printable[A]): Unit =
    println(myformat(value))
}