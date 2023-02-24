# Error handling

Typically it is done through MonadThrow

```scala
trait Categories[F[_]] {

  // no error info in type
  def findAll: F[List[Category]]

  // or with error info in type
  def findAllE: F[Either[RandomError, List[Category]]]
}

override def findAll: F[List[Category]] =
  Random[F].nextInt.flatMap {
    case n if n > 100 =>
      List.empty[Category].pure[F]
    case _            =>
      RandomError.raiseError[F, List[Category]]
  }
```

Do we need error in type? It depends on how it is processed afterwards. One of the examples is HTTP Rest service where it might be useful to process error to determine error code (like 400, 404, etc).

But most of the times using Either is really cumbersome. So on the rest side it could be catched and processed by special error handlers.

```scala
recover(
  case BuisinessError => 404
  case _ => 500 // anything else like Throwable, etc...
)
```

