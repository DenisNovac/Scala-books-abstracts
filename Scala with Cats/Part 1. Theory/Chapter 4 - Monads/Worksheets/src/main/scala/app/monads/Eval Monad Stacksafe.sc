import cats.Eval

/*def factorial(n: BigInt): BigInt =
  if(n == 1) n else n * factorial(n - 1)



factorial(50000) // java.lang.StackOverflowError
*/

/*def factorial(n: BigInt): Eval[BigInt] =
  if (n == 1) {
    Eval.now(n)
  } else {
    factorial(n - 1).map(_ * n)
  }

factorial(50000)  // java.lang.StackOverflowError
*/


def factorial(n: BigInt): Eval[BigInt] =
  if (n == 1) {
    Eval.now(n)
  } else {
    Eval.defer(factorial(n - 1).map(_ * n))
  }

factorial(50000).value

