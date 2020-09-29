

def partial1[A, B, C](a: A, f: (A, B) => C): B => C = 
  (b: B) => f(a, b)



//  A => (B => C) это то же самое, что A => B => C
def curry[A, B, C](f: (A, B) => C): A => (B => C) = 
  (a: A) => ((b: B) => f(a,b))



def exampleFunc(a: Int, b: Int): Int = a + b

val newFunc = curry(exampleFunc)

val f1 = newFunc(1)
val f2 = f1(2)

// или

newFunc(1)(2)


def uncurry[A, B, C](f: A => B => C): (A, B) => C =
  (a, b) => f(a)(b)


uncurry(newFunc)(1,2)


def compose[A, B, C](f: B => C, g: A => B): A => C = 
  a => f(g(a)) 