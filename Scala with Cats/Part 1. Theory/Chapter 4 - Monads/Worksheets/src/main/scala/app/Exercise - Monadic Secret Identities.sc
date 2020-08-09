import cats.Id

def pure[A](value: A): Id[A] = value

def map[A, B](initial: Id[A])(func: A => B): Id[B] = func(initial)

def flatMap[A, B](initial: Id[A])(func: A => Id[B]): Id[B] = func(initial)


val p = pure(1)
val m = map(p)(value => value+123)
val f = flatMap(m)(value => 23)

