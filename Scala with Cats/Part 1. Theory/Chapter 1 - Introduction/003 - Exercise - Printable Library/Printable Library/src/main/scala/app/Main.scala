package app

import PrintableInstances._
import PrintableSyntax._

object Main extends App {

  val a = "Test String"

  /** Эти вызовы не нуждаются в PrintableSyntax */
  println(Printable.myformat(a)) // Test String <3
  Printable.myprint(a)
  Printable.myprint(123)

  println()

  /** А вот эти нуждаются как в Instances, так и в Syntax */
  println(a.syntaxformat)  // Test String <3 from Syntax
  println(1234.syntaxformat)
  a.syntaxprint
  12345.syntaxprint

  println()
  Printable.myprint(Cat("Berta", 23, "Black"))
  Cat("Berta", 23, "Black").syntaxprint
}
