import cats.Semigroupal
import cats.instances.option._

Semigroupal[Option].product(Some(123), Some("456")) // res0: Option[(Int, String)] = Some((123,456))
Semigroupal[Option].product(Some(123), None) // res1: Option[(Int, Nothing)] = None


