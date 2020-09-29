import scala.annotation.tailrec

def factorial(n: Int): Int = {
  @tailrec
  def go(n: Int, acc: Int): Int =
    if (n <= 0) acc
    else go(n - 1, n * acc)

  go(n, 1)
}

def fibNum(n: Int): Int = {

  @tailrec
  def loop(n: Int, prev1: Int, prev2: Int): Int =
    if (n <= 0) prev2
    else loop(n - 1, prev2, prev1 + prev2)

  loop(n, 0, 1)
}

factorial(10)

fibNum(2)

def formatResult(name: String, n: Int, f: Int => Int) =
  s"The $name of $n is ${f(n)}"

formatResult("factorial", 5, factorial)
formatResult("fibNum", 6, fibNum)

def findFirst[T](ss: Array[T], f: T => Boolean): Int = {

  @tailrec
  def loop(n: Int): Int =
    if (n >= ss.length) -1
    else if (f(ss(n))) n
    else loop(n + 1)

  loop(0)
}

findFirst(Array("Hello", "world!"), ((x: String) => x.length == 6))


val equals = new Function1[String, Boolean] {

  override def apply(v1: String): Boolean = v1.length == 6

}


def isSorted[T](ss: Array[T], ordered: (T,T) => Boolean): Boolean = {
  
  @tailrec
  def loop(prev: Int, thiss: Int): Boolean = 
    if (thiss >= ss.length) true  // дошли до конца
    else if (ordered(ss(prev), ss(thiss))) loop(thiss, thiss + 1)
    else false

  if (ss.length == 1) true // если элемент один - он отсортирован
  else if (ss.isEmpty) false  
  else loop(0, 1)

}


// Сортировка по возростанию 
isSorted(Array(1, 2, 3), ( (i1: Int, i2: Int) => i1 < i2) )

isSorted(Array(1, 2, 3, 1), ( (i1: Int, i2: Int) => i1 < i2) )

// По убыванию
isSorted(Array(3, 2, 1), ( (i1: Int, i2: Int) => i2 < i1) )