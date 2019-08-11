package part1recap

import scala.concurrent.Future

object ThreadModelLimitations extends App {
  // 1: OOP encapsulation is only valid in the single threaded model

  class BankAccount(private var amount: Int) {
    override def toString: String = "" + amount
    def withdraw(amount: Int): Unit = this.amount -= amount
    def deposit(amount: Int): Unit = this.amount += amount
  }

  val account = new BankAccount(3000)

  for (_ <- 1 to 1000) { // 1000 threads withdrawing
    new Thread(() => account.withdraw(1)).start()
  }

  for (_ <- 1 to 1000) { // 1000 threads depositing
    new Thread(() => account.deposit(1)).start()
  }

  println(account) // probably not 2000

  // synchronization and locks create deadlocks and livelocks on big structures

  //-------------------------------------------------------------------------------------------------

  // 2: delegating something to a thread is a pain

  // manual way
  var task: Runnable = null

  val runningThread: Thread = new Thread(() => {
    while (true) {
      while (task == null) {
        runningThread.synchronized {
          println("[background]: waiting for a task...")
          runningThread.wait()
        }
      }

      task.synchronized {
        println("[background]: I have a task!!!1!")
        task.run()
        task = null
      }
    }
  })

  def runInBackground(r: Runnable): Unit = {
    if(task == null) task = r

    runningThread.synchronized {
      runningThread.notify()
    }
  }

  runningThread.start()

  Thread.sleep(1000)
  runInBackground(() => println(42))
  Thread.sleep(1000)
  runInBackground(() => println("this should run in background"))

  // is too limited
  // how to run multiple tasks?
  // how to send more that one signal?
  // what if it crashes?

  //-------------------------------------------------------------------------------------------------

  // 3: tracing and dealing with errors in a multithreaded environment is a pain

  // 1M numbers in between 10 threads

  import scala.concurrent.ExecutionContext.Implicits.global

  val futures = (0 to 9)
    .map(i => 100000 * i until 100000 * (i + 1)) // 0 - 99999, 100000 - 199999, ...
    .map(range => Future {
      if(range.contains(546735)) throw new RuntimeException("invalid number") // weird error done by a bad team member
      range.sum
    }
  )

  val sumFuture = Future.reduceLeft(futures)(_ + _) // Future with the sum of all numbers

  sumFuture.onComplete(println) // Failure(java.lang.RunimeException: invalid number) - wtf??? where? how? why?
}
