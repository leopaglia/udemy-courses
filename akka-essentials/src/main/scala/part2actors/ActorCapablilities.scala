package part2actors

import akka.actor.{Actor, ActorRef, ActorSystem, Props}

object ActorCapablilities extends App {

  class SimpleActor extends Actor {
    override def receive: Receive = {
      case "Hi!" => sender() ! "Hello there!" // replying
      case message: String => println(s"[${self.path.name}] - I have received: $message")
      case number: Int => println(s"[${self.path.name}] - I have received a number: $number")
      case SpecialMessage(content) => println(s"[${self.path.name}] - I have received a special message: $content")
      case SendMessageToYourself(content) => self ! content
      case SayHiTo(ref) => ref ! "Hi!"
      case WirelessPhoneMessage(content, ref) => ref forward content // keep the original content
    }
  }

  val system = ActorSystem("actorCapabilitiesDemo")

  val simpleActor = system.actorOf(Props[SimpleActor], "simpleActor")

  simpleActor ! "Hello, actor"

  // 1 - messages can be of any type
  //   a - messages must be inmutable
  //   b - messages must be serializable

  // in practice, use case classes and case objects

  simpleActor ! 42

  case class SpecialMessage(contents: String)
  simpleActor ! SpecialMessage("some special content")

  // 2 - actors have information about its context and their own
  //   a - context.self === 'this' in OOP === self

  case class SendMessageToYourself(content: String)
  simpleActor ! SendMessageToYourself("I am an actor and I am proud of it")

  // 3 - actors can reply to messages
  //   a - context.sender() === sender()
  //   b - actors pass themselves (ref) when they send a message

  val alice = system.actorOf(Props[SimpleActor], "alice")
  val bob = system.actorOf(Props[SimpleActor], "bob")

  case class SayHiTo(ref: ActorRef)

  alice ! SayHiTo(bob)

  // 4 - when there is no sender, akka pass the message to the special actor 'deadLetters'

  alice ! "Hi!"

  // 5 - forwarding messages (forward it with the original sender)
  //     ex: D -> A -> B  -  sender is D

  case class WirelessPhoneMessage(content: String, ref: ActorRef)
  alice ! WirelessPhoneMessage("Hi!", bob)
}

/**
  * a Counter actor
  *  - Increment
  *  - Decrement
  *  - Print
  */
object CounterExercise extends App {

  object Counter {
    case object Increment
    case object Decrement
    case object Print
  }

  class Counter extends Actor {

    import Counter._

    var counter = 0

    override def receive: Receive = {
      case Increment => counter += 1
      case Decrement => counter -= 1
      case Print => println(s"[${self.path.name}]: Current counter is $counter")
    }
  }

  val counterSystem = ActorSystem("counterSystem")
  val counter = counterSystem.actorOf(Props[Counter], "counter")

  import Counter._

  counter ! Increment
  counter ! Increment
  counter ! Decrement
  counter ! Print
  counter ! Increment
  counter ! Decrement
  counter ! Decrement
  counter ! Decrement
  counter ! Print
}

/**
  * 2. a Bank account
  *   - Deposit an amount
  *   - Withdraw an amount
  *   - Statement
  *
  *   - Success/Failure reply
  */
object BankAccountExercise extends App {

  object Account {
    trait BankMessage
    case class Deposit(amount: Int) extends BankMessage
    case class Withdraw(amount: Int) extends BankMessage
    case object Statement extends BankMessage
  }

  class Account extends Actor {
    import Account._
    import Client._

    var totalAmount = 0
    var movements: List[String] = List()

    override def receive: Receive = {
      case Deposit(amount) =>
        totalAmount += amount
        movements = movements :+ s"${sender().path.name} deposited $$$amount. Current amount is $$$totalAmount."
        sender() ! Success
      case Withdraw(amount) =>
        if(totalAmount >= amount) {
          totalAmount -= amount
          movements = movements :+ s"${sender().path.name} withdrawn $$$amount. Current amount is $$$totalAmount."
          sender() ! Success
        } else {
          sender() ! Failure
        }
      case Statement =>
        val initiator = s"=== [${self.path.name}] statement ==="
        val separator = "==============================="
        println(s"\n$initiator\n${movements.reduceLeft(_ + "\n" + _)}\n$separator\n")
    }
  }

  object Client {
    import Account._
    case class SendToBank(ref: ActorRef, msg: BankMessage)
    case object Success
    case object Failure
  }

  class Client extends Actor {
    import Client._

    override def receive: Receive = {
      case SendToBank(bankRef, msg) => bankRef ! msg
      case Success => println(s"[${self.path.name}]: Received success response from bank!")
      case Failure => println(s"[${self.path.name}]: Received failure response from bank!")
    }
  }

  val bankSystem = ActorSystem("bankSystem")
  val bank = bankSystem.actorOf(Props[Account], "bankAccount")
  val bob = bankSystem.actorOf(Props[Client], "bob")

  // only one thread has access to the actor at a given time, so actors are single threaded
  // which means no asynchronous issues
  // messages will be received AT MOST once
  // messages between an actor pair will be ordered

  import Client._
  import Account._
  bob ! SendToBank(bank, Deposit(2500))
  bob ! SendToBank(bank, Deposit(7000))
  bob ! SendToBank(bank, Withdraw(9000))
  bob ! SendToBank(bank, Statement)
  bob ! SendToBank(bank, Withdraw(2000))
  bob ! SendToBank(bank, Deposit(3000))
  bob ! SendToBank(bank, Statement)
}

