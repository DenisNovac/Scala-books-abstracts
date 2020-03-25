

/** Тайпкласс состоит из своего определения, инстансов типов и интерфейсных методов */

/** ОПРЕДЕЛЕНИЕ ТАЙПКЛАССА */
abstract class CoolerNum[A: Numeric] {
  def makeCool(value: A): A
}

/** ИНСТАНСЫ ТАЙПКЛАССА */
object CoolerNumInstances {

  implicit val intCool: CoolerNum[Int] = new CoolerNum[Int] {
    override def makeCool(value: Int): Int = value + 42
  }

  implicit val doubleCool: CoolerNum[Double] = new CoolerNum[Double] {
    override def makeCool(value: Double): Double = value + 42.0
  }

  // не работает
  /*implicit val strCool: CoolerNum[String] = new CoolerNum[String] {
    override def makeCool(value: String) = "Does not work"
  }*/
}

/** ИНТЕРФЕЙСЫ ТАЙПКЛАССА */

// Интерфейсные объекты
// Требуют инстанс и явный вызов
object CoolMaker {
  def toCool[A: Numeric](value: A)(implicit c: CoolerNum[A]): A = c.makeCool(value)
}

import CoolerNumInstances.intCool
import CoolMaker.toCool  // всё равно явно вызывается снаружи объекта
CoolMaker.toCool(0)  // res0: Int = 42
toCool(0)  // res1: Int = 42

// Интерфейсный синтаксис
// Требует инстанс и можно вызывать неявно, когда внутренности импортированы в скоп
// Выглядит так, будто метод является частью объекта
object CoolSyntax {
  implicit class CoolMakerOps[A: Numeric](value: A) {
    def toCool(implicit c: CoolerNum[A]): A = c.makeCool(value)
  }
}


import CoolSyntax._
import CoolerNumInstances.doubleCool
3.2.toCool // res2: Double = 45.2

