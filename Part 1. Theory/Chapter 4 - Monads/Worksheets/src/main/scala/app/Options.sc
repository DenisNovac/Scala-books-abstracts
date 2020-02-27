
/** Оба этих метода могут вернуть None */
def parseInt(str: String): Option[Int] =
  scala.util.Try(str.toInt).toOption

def divide(a: Int, b: Int): Option[Int] =
  if (b == 0) None else Some(a / b)

/** FlatMap позволяет игнорировать это при последовательных вычислениях */

def stringDivideBy(aStr: String, bStr: String): Option[Int] =
  for {
    aNum <- parseInt(aStr)
    bNum <- parseInt(bStr)
    ans <- divide(aNum, bNum)
  } yield ans


stringDivideBy("1", "2")
stringDivideBy("1", "0")
stringDivideBy("3", "2")