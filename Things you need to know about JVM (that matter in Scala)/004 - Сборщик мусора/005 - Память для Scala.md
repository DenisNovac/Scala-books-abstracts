# Память для Scala

Scala предполагает написание иммутабельного кода. Если нам нужно обновить case class - мы создаём его копию. Это соаздёт новый объект, а старый когда-то должен быть собран GC.

То же и с коллекциями и методами типа `.map`, `.flatMap` - каждый из них создаёт копию коллекции (только одну, если использовать `.view`). Функторы позволяют снизить аллокации с `coll.map(f1).map(f2)` до `coll.map(f1 andThen f2)`.

Даже вычисления происходят в структурах данных вроде `cats.effect.IO`. Однако, под капотом такие штуки обычно написаны максимально быстрым путём - через `var`, `null` и `instanceOf`.





