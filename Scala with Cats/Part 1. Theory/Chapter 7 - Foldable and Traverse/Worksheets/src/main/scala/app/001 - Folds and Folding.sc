

def show[A](list: List[A]): String =
    list.foldLeft("nil")((accum, item) => s"$item then $accum")

def showr[A](list: List[A]): String =
    list.foldRight("nil")((accum, item) => s"$item then $accum")

show(List(1, 2, 3))  // res0: String = 3 then 2 then 1 then nil
showr(List(1, 2, 3)) // res1: String = nil then 3 then 2 then 1

// List(1,2,3).foldLeft(List.empty[Int])(_ :: _)  :: not a member of int
List(1, 2, 3).foldRight(List.empty[Int])(_ :: _) // res2: List[Int] = List(1, 2, 3)

def mymap[A, B](list: List[A])(f: A => B): List[B] =
    list.foldRight(List.empty[B])((e, a) => f(e) :: a)

def myflatMap[A, B](list: List[A])(f: A => List[B]): List[B] =
    list.foldRight(List.empty[B])((e, a) => f(e) ++ a)

def myfilter[A](list: List[A])(p: A => Boolean): List[A] =
    list.foldRight(List.empty[A]) {
      case (e, a) if p(e) => e :: a
      case (e, a)         => a
    }

def mysum[A: Numeric](list: List[A]): A =
    list.foldRight(Numeric[A].zero)((e, a) => Numeric[A].plus(e, a))

mymap(List(1, 2, 3))(a => a * 2)
myflatMap(List(1, 2, 3))(a => List(a.toString + "!"))
myfilter(List(1, 2, 3))(a => a > 2)
mysum(List(1, 2, 3))


import cats.Monoid
def monoidSum[A](list: List[A])(implicit monoid: Monoid[A]): A =
  list.foldRight(monoid.empty)(monoid.combine(_,_))
