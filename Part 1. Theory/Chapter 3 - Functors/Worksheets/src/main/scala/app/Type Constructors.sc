// Declare F using underscores:
  def myMethod[F[_]] = {
    // Reference F without underscores:
    val functor = Functor.apply[F]
    // ...
  }