# Функции высшего порядка

В Scala функция - это такое же значение, как Int или String. Поэтому его можно передавать в другии функции как аргументы. Обозначение такого аргмента следующее: `f: Int => Int`, что означает, что функция принимает Int и отдаёт Int. 

```scala
def formatResult(name: String, n: Int, f: Int => Int) = {

  s"The $name of $n is ${f(n)}"
}

formatResult("factorial", 5, factorial) // res2: String = "The factorial of 5 is 120"
formatResult("fibNum", 6, fibNum) // res3: String = "The fibNum of 6 is 13"
```

