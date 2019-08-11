package part1recap

import scala.util.Try

object GeneralRecap extends App {

  val condition: Boolean = false // <- type not needed, the compiler can infer it

  var variable = 42
  variable = 43

  val ifExpression = if(condition) 42 else 65 // if returns a value, it's not just a syntax constructor

  val codeBlock = {
    println("something")
    46 // code blocks are expressions that return the value of their last line
  }

  // units are the return value type for side-effect functions which return void
  val anUnit: Unit = println("hi")

  def aFunction(x: Int) = x + 1 // the return value type is inferred

  // the recursive case return value is a call to the function, without anything else
  // this way, the compiler can optimize the function to avoid spamming the stack
  def tailRecursiveFactorial(n: Int, acc: Int): Int =
    if (n <= 0) acc
    else tailRecursiveFactorial(n - 1, acc * n)

  class Animal
  class Dog extends Animal
  val aDog: Animal = new Dog // type polymorphism

  // kind of abstract class, but can't be instantiated, and can be used from any class hierarchy
  trait Carnivore {
    def eat(a: Animal): Unit
  }

  class Crocodile extends Animal with Carnivore {
    override def eat(a: Animal): Unit = println("crunch!") // needs to define the abstract method from the trait
  }

  val aCrocodile = new Crocodile
  aCrocodile.eat(aDog) // normal notation
  aCrocodile eat aDog // infix notation

  // providing an anonymous class to the trait to be instantiated
  val aCarnivore = new Carnivore {
    override def eat(a: Animal): Unit = println("roar!")
  }

  aCarnivore eat aDog

  abstract class MyList[+A] // +A is a covariant generic type

  object MyList // companion object for MyList class

  // case classes are classes definition that can be pattern matched
  // and have an apply method already defined, to allow instantiation without the new keyword
  // they are defined with vals, so they are inmutable
  // if 2 case classes hold the same data, they pass the equality comparison
  // they have a copy method defined, which creates a shallow copy (2 classes pointing at the same data)
  case class Person(name: String, age: Int)

  val aPotentialFailure = try { // try expressions
    throw new RuntimeException("oh noes")
  } catch {
    case _: Exception => "caught!" // scala uses pattern matching for caught exceptions
  } finally {
    println("this always happens, catching exception or not")
  }

  // functions are objects, so they are first class citizens in the JVM
  val incrementer = new Function1[Int, Int] {
    override def apply(v1: Int): Int = v1 + 1
  }

  val incremented = incrementer(42) // apply method used

  val anonymousIncrementer = (x: Int) => x + 1 // arrow function syntax sugar

  val mappedList = List(1, 2, 3).map(anonymousIncrementer) // higher order function

  val pairs = for { // for comprehension expression
    num <- List(1, 2, 3, 4)
    char <- List('a', 'b', 'c', 'd')
  } yield num + "-" + char

  // for expression translated by the compiler
  val pairsRewritten = List(1, 2, 3, 4)
    .flatMap(num => List('a', 'b', 'c', 'd')
    .map(char => num + "-" + char))

  val anOption = Some(2)

  val aTry = Try { // try expression, using try apply method
    throw new RuntimeException("oh not again")
  }

  val unknown = 2
  val order = unknown match { // standard pattern matching
    case 1 => "one"
    case 2 => "two"
    case _ => "idk"
  }

  val bob = Person("bob", 22)
  val greeting = bob match { // pattern matching case class destructuring
    case Person(n, _) => s"Hi, my name is $n" // string interpolation
    case _ => "lol idk"
  }
}
