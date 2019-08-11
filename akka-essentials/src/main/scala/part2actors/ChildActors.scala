package part2actors

import akka.actor.{Actor, ActorRef, ActorSystem, Props}

object ChildActors extends App {

  object Parent {
    case class CreateChild(name: String)
    case class TellChild(message: String)
  }

  class Parent extends Actor {
    import Parent._

    override def receive: Receive = {
      case CreateChild(name) =>
        println(s"${self.path} - creating child")
        val child = context.actorOf(Props[Child], name)
        context.become(withChild(child))
    }

    def withChild(child: ActorRef): Receive = {
      case TellChild(message) => child forward message
    }
  }

  class Child extends Actor {
    override def receive: Receive = {
      case message => println(s"[${self.path}] - I got message: $message")
    }
  }

  import Parent._

  val system = ActorSystem("childActors")
  val parent = system.actorOf(Props[Parent], "bigBob")

  parent ! CreateChild("lilBob")
  parent ! TellChild("hey kid!")

  // actor hierarchies
  // parent -> child  -> grandchild
  //        -> child2 -> grandchild

  // guardian actors (top level)
  //   - /system - system guardian
  //   - /user - user-level guardian
  //   - / - root guardian

  // actor selection
  val childSelection = system.actorSelection("/user/bigBob/lilBob")
  childSelection ! "I've found you!"

  /**
    * Danger:
    *
    * NEVER PASS MUTABLE ACTOR STATE OR THE 'this' REFERENCE TO A CHILD ACTOR
    *
    * NEVER
    */

  object NaiveBankAccount {
    case class Withdraw(amount: Int)
    case class Deposit(amount: Int)
    case object InitializeAccount
  }

  class NaiveBankAccount extends Actor {
    import NaiveBankAccount._
    import CreditCard._

    var amount = 0

    override def receive: Receive = {
      case InitializeAccount =>
        val creditCardRef = context.actorOf(Props[CreditCard], "card")
        creditCardRef ! AttachToAccount(this) // !!!!!!
      case Deposit(funds) =>
        println(s"${self.path}: depositing $funds on top of $amount")
        deposit(funds)
      case Withdraw(funds) =>
        println(s"${self.path}: withdrawing $funds from $amount")
        withdraw(funds)
    }

    def deposit(funds: Int): Unit = amount += funds // !!
    def withdraw(funds: Int): Unit = amount -= funds // !!
  }

  object CreditCard {
    case class AttachToAccount(bankAccount: NaiveBankAccount) // !!!!!!
    case object CheckStatus
  }

  class CreditCard extends Actor {
    import CreditCard._

    override def receive: Receive = {
      case AttachToAccount(account) => context.become(attachedTo(account))
    }

    def attachedTo(account: NaiveBankAccount): Receive = {
      case CheckStatus =>
        println(s"${self.path}: message processed")
        account.withdraw(1) // !!!!!!!!!!!!! bypasses message handler logic!
    }
  }

  import NaiveBankAccount._
  import CreditCard._

  val bankAccountRef = system.actorOf(Props[NaiveBankAccount], "account")
  bankAccountRef ! InitializeAccount
  bankAccountRef ! Deposit(100)

  Thread.sleep(500) // making sure it already has the ref

  val creditCardSelection = system.actorSelection("/user/account/card")
  creditCardSelection ! CheckStatus // breaks encapsulation, withdraws moniezz !!! D:

  bankAccountRef ! Withdraw(20) // withdrawing 20 from 99 wtf

  // two messages, three actions!! D:
}
