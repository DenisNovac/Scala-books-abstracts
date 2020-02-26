# Monoid

Мы посмотрели несколько сценариев сложения выше, каждый с методом бинарного (в том смысле что работает для двух элементов одного типа) ассоциативного сложения и нейтральным элементом (identity). Это и есть моноид.

**Моноид** для типа A - это:
- Операция `combine` типа `(A, A) => A`;
- Элемент `empty` типа `A`.

Определение моноида (упрощённое):

```scala
trait Monoid[A] {
  def combine(x: A, y: A): A
  def empty: A
}
```
Кроме реализации этих операций, моноиды подчиняются формальным законам. Для всех значений x, y и z в A, `combine` должна быть ассцоциативна и `empty` должен быть пустым.

```scala
def associativeLaw[A](x: A, y: A, z: A)(implicit m: Monoid[A]): Boolean =
    m.combine(x, m.combine(y, z)) ==
      m.combine(m.combine(x, y), z)

def identityLaw[A](x: A)(implicit m: Monoid[A]): Boolean =
    (m.combine(x, m.empty) == x) &&
      (m.combine(m.empty, x) == x)
```

Вычитание чисел, например, это не моноид - оно не ассоциативно.



