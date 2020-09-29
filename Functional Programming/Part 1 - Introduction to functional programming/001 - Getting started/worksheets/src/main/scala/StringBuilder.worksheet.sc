

val x = new StringBuilder("Hello")

val r1 = x.append(", World").toString

// Hello, World

val r2 = x.append(", World").toString

// Hello, World, World




val r3 = new StringBuilder("Hello").append(", World").toString

// Hello, World

val r4 = new StringBuilder("Hello").append(", World").toString

// Hello, World
