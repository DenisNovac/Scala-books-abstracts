import cats.Semigroupal
import cats.instances.option._

Semigroupal[Option].product(Some(123), Some("456")) // res0: Option[(Int, String)] = Some((123,456))
Semigroupal[Option].product(Some(123), None) // res1: Option[(Int, Nothing)] = None

Semigroupal.tuple3(Option(1), Option(2), Option(3))  // res2: Option[(Int, Int, Int)] = Some((1,2,3))
Semigroupal.tuple3(Option(1), Option(2), Option.empty[Int])  // res3: Option[(Int, Int, Int)] = None



Semigroupal.map3(Option(1), Option(2), Option(3))(_ + _ + _)  // res4: Option[Int] = Some(6)

