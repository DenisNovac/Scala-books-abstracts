package app
import PrintableInstances._

final case class Cat(name: String, age: Int, color: String)

object Cat {
  implicit val catFormatter = new Printable[Cat] {
    override def myformat(cat: Cat): String = {
      val name = Printable.myformat(cat.name)
      val age = Printable.myformat(cat.age)
      val color = Printable.myformat(cat.color)
      s"$name is a $age year-old $color cat."
    }
  }
}