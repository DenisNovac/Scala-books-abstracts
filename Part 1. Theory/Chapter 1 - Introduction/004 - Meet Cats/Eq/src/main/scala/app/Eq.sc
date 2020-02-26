List(1, 2, 3).map(Option(_)).filter(item => item == 1) // всегда пусто, Option не Int

1 == "b" // false
1 == "1" // false

/** Какой вообще смысл сравнивать разные типы? Это всегда false.
  * Обычно компилятор об этом предупреждает.
  * Eq позволяет определить типобезопасное равенство.
  * */
/** Интерфейсный синтаксис cats.syntax.eq имеет операторы === и =!=, равнозначные
  * операторам == и !=, но типобезопасные.
  * */
import cats.Eq
import cats.instances.all._

val intEq = Eq[Int]
intEq.eqv(1, 4) // без синтаксиса

import cats.syntax.eq._

// 1 === "1"  // type mismatch
1 === 1
2 === 1
2 =!= 1

// Сравнение опций

// Some(1) === None // value === is not a member of Some[Int]

/** Ошибка связана с тем, что типы не совпали. У нас есть инстансы только для int и option[int],
  * но мы сравниваем Some[Int]. Как исправить:
  * */

(Some(1): Option[Int]) === (None: Option[Int])

// Или через методы apply и empty

Option(1) === Option.empty[Int]

// или через сахар котов

import cats.syntax.option._

1.some === none[Int]
1.some =!= none[Int]














