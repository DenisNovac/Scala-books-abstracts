# Semigroupal

`cats.Semigroupal` - это тайпкласс для комбинирования контекстов. Если у нас есть два объекта `F[A]` и `F[B]` - `Semigroupal[B]` позволяет составить `F[(A,B)]`. 

Упрощённое определение:

```scala
trait Semigroupal[F[_]] {
  def product[A,B](fa: F[A], fb: F[B]): F[(A,B)]
}
```

При этом параметры fa и fb независимы, их можно вычислять в любом порядке перед передачей в `product`. 

## Соединение контекстов

`Semigroup` - позволяет складывать значения. `Semigroupal` - позволяет соединять контексты. Например:

```scala
import cats.Semigroupal
import cats.instances.option._

Semigroupal[Option].product(Some(123), Some("456"))  // res0: Option[(Int, String)] = Some((123,456))
Semigroupal[Option].product(Some(123), None) // res1: Option[(Int, Nothing)] = None
```

Если какой-то из параметров None - весь Semigroupal будет равен None.

## Соединение трёх или более контекстов

```scala
Semigroupal.tuple3(Option(1), Option(2), Option(3))  
// res2: Option[(Int, Int, Int)] = Some((1,2,3))
Semigroupal.tuple3(Option(1), Option(2), Option.empty[Int])  
// res3: Option[(Int, Int, Int)] = None

Semigroupal.map3(Option(1), Option(2), Option(3))(_ + _ + _)  
// res4: Option[Int] = Some(6)
```

tuple, map, contramap и imap существуют в вариациях от 2 до 22.


