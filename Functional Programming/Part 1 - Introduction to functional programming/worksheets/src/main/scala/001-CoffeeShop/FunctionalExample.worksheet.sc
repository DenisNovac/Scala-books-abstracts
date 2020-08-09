case class Coffee() {
  def price: Double = 1.0
}

case class CreditCard()

case class Charge(cc: CreditCard, amount: Double) {

  def combine(other: Charge): Charge =
    if (cc == other.cc)
      Charge(cc, amount + other.amount)
    else
      throw new Exception("Can't combine charges to different cards")
}


object Cafe {

  def buyCoffee(cc: CreditCard): (Coffee, Charge) = {
    val cup = Coffee()
    (cup, Charge(cc, cup.price))
  }

  def buyCoffees(cc: CreditCard, n: Int): (List[Coffee], Charge) = {
    // List.fill(n)(...) - n копий содержимого
    val purchases: List[(Coffee, Charge)]                = List.fill(n)(buyCoffee(cc))
    val (coffees, charges): (List[Coffee], List[Charge]) = purchases.unzip

    // reduce используется чтобы схлопнуть лист путём использования функции combine
    // получается, мы возвращаем набор кофе и только один скомбинированный счёт
    (coffees, charges.reduce((c1, c2) => c1.combine(c2)))
  }

}

Cafe.buyCoffees(CreditCard(), 10)




