package app

/**
  * Инстансы для конкретных типов, маркер <3 для наглядности
  */
object PrintableInstances {
  /*implicit val stringFormatter: Printable[String] =
    new Printable[String] {
      override def format(value: String): String = value + " <3"
    }*/

  // то же что:
  implicit val stringFormatter: Printable[String] =
    (value: String) => value + " <3"

  implicit val intFormatter: Printable[Int] =
    (value: Int) => value.toString + " <3"


}
