# Параметрический полиморфизм

Иногда (особенно для функций высшего порядка) полезно, чтобы функция работала для любого типа. Такие функции называются полиморфными функциями.

Мы можем вывести полиморфные функции, когда обнаруживаем, что несколько мономорфных функций имеют одну структуру. Например, функция `findFirstString` для поиска первого вхождения в массив строк:

```scala
def findFirstString(ss: Array[String], key: String): Int = {

  @tailrec
  def loop(n: Int): Int = 
    if (n >= ss.length) -1
    else if (ss(n) == key) n
    else loop(n + 1)

  loop(0)
}
```

Код будет выглядеть одинаково для любого типа, поэтому его можно обобщить:

```scala
def findFirst[T](ss: Array[T], key: T): Int = {

  @tailrec
  def loop(n: Int): Int = 
    if (n >= ss.length) -1
    else if (ss(n) == key) n
    else loop(n + 1)

  loop(0)
}
```

Ещё можно обобщить метод определения первого вхождения (например, чтобы потом брать не "равенство", а длину):

```scala
def findFirst[T](ss: Array[T], f: T => Boolean): Int = {

  @tailrec
  def loop(n: Int): Int =
    if (n >= ss.length) -1
    else if (f(ss(n))) n
    else loop(n + 1)

  loop(0)
}

findFirst(Array("Hello", "world"), ((x: String) => x == "world"))  // 1

findFirst(Array("Hello", "world!"), ((x: String) => x.length == 6)) // 1
```

Функция вида `(x: String) => x.length == 6)` называется *анонимной* функцией. Это синтаксический сахар для конструкции:

```scala
new Function1[String, Boolean] {
  override def apply(v1: String): Boolean = v1.length == 6
}
```

Обобщить коллекцию будет труднее, так как элемент берётся по индексу (так умеют не все коллекции).

Так как мы используем один параметр T, он должен быть одинаков для каждого места, где он используется. Нельзя передать массив `Int`, а функцию `String => Boolean`, так как T в этом случае уже ожидается как `Int`.

## Упражнение

Написать метод, проверяющий сортировку массива:

```scala
def isSorted[T](ss: Array[T], ordered: (T,T) => Boolean): Boolean = {
  
  @tailrec
  def loop(prev: Int, thiss: Int): Boolean = 
    if (thiss >= ss.length) true  // дошли до конца
    else if (ordered(ss(prev), ss(thiss))) loop(thiss, thiss + 1)
    else false

  if (ss.length == 1) true // если элемент один - он отсортирован
  else if (ss.isEmpty) false  
  else loop(0, 1)

}


// Сортировка по возростанию 
isSorted(Array(1, 2, 3), ( (i1: Int, i2: Int) => i1 < i2) )  // true

isSorted(Array(1, 2, 3, 1), ( (i1: Int, i2: Int) => i1 < i2) )  // false

// По убыванию
isSorted(Array(3, 2, 1), ( (i1: Int, i2: Int) => i2 < i1) )  // true
```

