package part1recap

import scala.concurrent.Future
import scala.util.{Failure, Success}

object MultithreadingRecap extends App {

  // creating a thread
  val myThread = new Thread(new Runnable {
    override def run(): Unit = println("I'm running in paralel")
  })

  // thread creation syntax sugar
  val myThread2 = new Thread(() => println("me too"))

  myThread.start() // start the execution
  myThread.join() // wait for it to finish

  val threadHello = new Thread(() => (1 to 1000).foreach(_ => println("hello")))
  val threadGoodbye = new Thread(() => (1 to 1000).foreach(_ => println("goodbye")))

  // different runs produce different results
  threadHello.start()
  threadGoodbye.start()

  class BankAccount(private var amount: Int) { // volatile annotation may be used: (@volatile private var amount: Int)
    override def toString: String = "" + amount

    // unsafe
    def withdraw(amount: Int): Unit = this.amount -= amount

    // safe
    def safeWithdraw(amount: Int): Unit = this.synchronized { // mutex
      this.amount -= amount
    }
  }

  /*
      BA(10000)

      T1 -> withdraw 1000
      T2 -> withdraw 2000

      T1 -> -1000 = 9000
      T2 -> -2000 = 8000

      result may be 9000

      because this.amount -= amount is NOT atomic (not thread safe)
   */

  // wait-notify JVM
  import scala.concurrent.ExecutionContext.Implicits.global

  val future = Future {
    // long computation - will run on a different thread
    42
  }

  // callbacks
  future.onComplete{
    case Success(42) => println("I found the meaning of life")
    case Success(_) => println("I found something else")
    case Failure(_) => println("I failed finding the meaning of life")
  }

  // futures are functional constructs
  val aProcessedFuture = future.map(_ + 1) // 42

  val aFlatFuture = future.flatMap {
    value => Future(value + 2)
  } // 44

  val filteredFuture = future.filter(_ % 2 == 0) // NoSuchElementException

  // for comprehensions
  val aNonsenseFuture = for {
    meaningOfLife <- future
    filteredMeaning <- filteredFuture
  } yield meaningOfLife + filteredMeaning

  // andThen, recover, recoverWith

  // Promises
}
