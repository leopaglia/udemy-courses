package part1recap

import scala.concurrent.Future

object AdvancedRecap {

  // partial function
  // operates only on a sub domain
  val partialFunction: PartialFunction[Int, Int] = {
    case 1 => 42
    case 2 => 65
    case 5 => 999
  }

  // syntax sugar for partial function
  val pf: Int => Int = { // anonymous pattern matching syntax sugar
    case 1 => 42
    case 2 => 65
    case 5 => 999
  }

  val modList2: List[Int] = List(1, 2, 3).map { // anonymous function
    case 1 => 42
    case _ => 0
  }

  // turns partialFunction into a total function of type Int => Option[Int]
  // if it can handle the value, it will return a Some(value)
  // if it cannot handle the value, it will return a None and not throw an exception
  val lifted: Int => Option[Int] = partialFunction.lift

  val modList: List[Int] = List(1, 2, 3) map pf // will throw an exception on runtime, because 3 can't be handled by pf

  val safeModList: List[Option[Int]] = List(1, 2, 3) map lifted // will return a list of options

  // extension of original partial function
  val pfChain: PartialFunction[Int, Int] = partialFunction.orElse[Int, Int] {
    case 60 => 9000
  }

  pfChain(5) // 999 per original
  pfChain(6) // 9000 per extension
  pfChain(42) // still throws

  // type alias
  type ReceiveFunction = PartialFunction[Any, Unit]

  def receive: ReceiveFunction = {
    case 1 => println("hi there")
    case 2 => println("bye you")
  }

  // implicit value
  implicit val timeout: Int = 3000

  def setTimeout(f: () => Unit)(implicit timeout: Int): Unit = f() // implementation does not matter

  // timeout param can be omitted, and the compiler will use the one defined above
  setTimeout(() => println("print me pls"))

  case class Person(name: String) {
    def greet = s"Hi there, I'm $name"
  }

  // implicit convertions:

  // 1) implicit methods
  implicit def fromStringToPerson(string: String): Person = Person(string)

  "Peter".greet // compiler does: fromStringToPerson("Peter").greet

  // 2) implicit classes
  implicit class Dog(name: String) {
    def bark(): Unit = println("bark")
  }

  "Lassie".bark() // compilelr does: new Dog("Lassie").bark()

  // implicit organization priorities

  // 1 - local scope

  implicit val inverseOrdering: Ordering[Int] = Ordering.fromLessThan(_ > _)

  List(1, 2, 3).sorted // List(3, 2, 1) - Lists have the _ < _ ordering implicit by default

  // 2 - imported scope

  import scala.concurrent.ExecutionContext.Implicits.global

  val future = Future {
    println("hello, future")
  }

  // 3 - companion object of types included
  object Person {
    implicit val personOrdering: Ordering[Person] = Ordering.fromLessThan((a, b) => a.name.compareTo(b.name) > 0)
  }

  List(Person("Bob"), Person("Alice")).sorted // List(Person("Alice"), Person("Bob"))
}
