# Монадные трансформеры

Монады как буррито - как только прочувствовал вкус - хочется снова и снова. 

Однако, монады могут заполнить код множеством вложенных for-comprehensions.

Представьте работу с БД. Пользовательская запись может существовать или нет, поэтому вернём `Option[User]`. Коммуникация с БД может нарушиться, поэтому этот результат обёрнут в `Either[Error, Option[User]]`. Получится такой код:

```scala
def lookupUserName(id: Long): Either[Error, Option[String]] =
  for {
    optUser <- lookupUser(id)
  } yield {
    for { user <- optUser } yield user.name
  }
```

Это не очень хорошо.

## Упражнение: композиция монад

Возникает вопрос - можно ли скомбинировать в одну монаду две случайных? *Композируемы ли* монады? Мы можем написать такой код:

```scala
import cats.Monad
import cats.syntax.applicative._
import cats.syntax.flatMap._


def compose[M1[_]: Monad, M2[_]: Monad] = {
  type Composed[A] = M1[M2[A]]

  new Monad[Composed] {
    override def pure[A](a: A): Composed[A] =
      a.pure[M2].pure[M1]

    // Как написать flatMap для неизвестной монады? Мы заранее ничего о ней не знаем
    override def flatMap[A, B](fa: Composed[A])(f: A => Composed[B]): Composed[B] =
      ??? 

    override def tailRecM[A, B](a: A)(f: A => Composed[Either[A, B]]) = ???


  }
}
```

Нам нужна информация об одной из композируемых монад. Например, если бы мы заменили M2 на Option, мы бы могли использовать None:

```scala
override def flatMap[A, B](fa: Composed[A])(f: A => Composed[B]): Composed[B] =
  fa.flatMap(_.fold[Composed[B]](None.pure[M1])(f))
```

None - это специфичная для Option концепция. Эта деталь помогает комбинировать Option с любыми другими монадами. В любой монаде есть какие-то вещи, которые помогают писать композированный `flatMap` для них. Эта идея лежит в монадных трансформерах. Cats определяет трансформеры для нескольких монад, каждая из которых предоставляет дополнительные данные, которые помогают композировать их с другими монадами.
