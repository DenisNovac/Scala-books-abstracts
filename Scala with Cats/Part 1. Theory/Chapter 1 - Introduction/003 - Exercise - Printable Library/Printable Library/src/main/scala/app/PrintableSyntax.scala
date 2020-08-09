package app

/**
  * Подключаемый синтаксис является интерфейсом, но реализован иначе и вписывается красивее
  */
object PrintableSyntax {
  implicit class PrintableOps[A](value: A) {
    def syntaxformat(implicit p: Printable[A]): String =
      p.myformat(value)

    def syntaxprint(implicit p: Printable[A]): Unit =
      println(syntaxformat(p))
  }
}
