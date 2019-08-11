package part2actors

import akka.actor.{Actor, ActorRef, ActorSystem, Props}

object ChangingActorBehavior extends App {

  object FussyKid {
    case object KidAccept
    case object KidReject
    val HAPPY = "happy"
    val SAD = "sad"
  }

  class FussyKid extends Actor {
    import FussyKid._
    import Mom._
    var state: String = HAPPY

    override def receive: Receive = {
      case Food(VEGETABLE) => state = SAD
      case Food(CHOCOLATE) => state = HAPPY
      case Ask(_) =>
        if(state == HAPPY) sender() ! KidAccept
        else sender() ! KidReject
    }
  }

  class StatelessFussyKid extends Actor {
    import FussyKid._
    import Mom._

    override def receive: Receive = happyReceive // default value

    def happyReceive: Receive = {
      // passing true to the second parameter of .become discards other message handlers (default)
      // passing false to the second parameter of .become stacks the new handler to the rest
      case Food(VEGETABLE) => context.become(sadReceive) // change receive method to sadReceive
      case Food(CHOCOLATE) => // stay happy
      case Ask(_) => sender() ! KidAccept
    }

    def sadReceive: Receive = {
      case Food(VEGETABLE) => context.become(sadReceive, false) // push a new sad handler to the receive stack (be more sad)
      case Food(CHOCOLATE) => context.unbecome() // pop the top handler (be less sad)
      case Ask(_) => sender() ! KidReject
    }
  }

  object Mom {
    case class MomStart(kid: ActorRef)
    case class Food(food: String)
    case class Ask(message: String)
    val VEGETABLE = "veggies"
    val CHOCOLATE = "chocolate"
  }

  class Mom extends Actor {
    import FussyKid._
    import Mom._

    override def receive: Receive = {
      case MomStart(kidRef) =>
        kidRef ! Food(VEGETABLE)
        kidRef ! Ask("do you want to play?")
      case KidAccept => println("yay ma kid is happy")
      case KidReject => println("oh noes ma kid is sad")
    }
  }

  val system = ActorSystem("changingBehavior")
  val mom = system.actorOf(Props[Mom])
  val kid = system.actorOf(Props[FussyKid])
  val statelessKid = system.actorOf(Props[StatelessFussyKid])

  import Mom.MomStart
  mom ! MomStart(statelessKid)
}


/**
  * 1- Reimplement counter actor without mutable state
  */
object InmutableCounterExercise extends App {

  // to transform a stateful actor into a stateless one, convert the state variables into handler parameters
  // and replace the handlers with new ones with modified params

  object Counter {
    case object Increment
    case object Decrement
    case object Print
  }

  class Counter extends Actor {
    import Counter._

    override def receive: Receive = countReceive(0)

    def countReceive(current: Int): Receive = {
      case Increment => context.become(countReceive(current + 1))
      case Decrement => context.become(countReceive(current - 1))
      case Print => println(s"current count is $current")
    }
  }

  val counterSystem = ActorSystem("counterSystem")
  val counter = counterSystem.actorOf(Props[Counter], "counter")

  import Counter._

  counter ! Increment
  counter ! Increment
  counter ! Increment
  counter ! Decrement
  counter ! Print // 2
  counter ! Increment
  counter ! Decrement
  counter ! Decrement
  counter ! Decrement
  counter ! Decrement
  counter ! Print // -1
}


/**
  * Simplified voting system
  * Each citizen can vote only once
  * Vote aggregator can aggregate citizen votes and print a map Candidate -> votes sum
  */
object VotingExercise extends App {

  object Citizen {
    case class Vote(candidate: String)
    case object VoteStatusRequest
    case class VoteStatusReply(candidate: Option[String])
  }

  class Citizen extends Actor {
    import Citizen._

    override def receive: Receive = {
      case Vote(candidate) => context.become(voted(candidate))
      case VoteStatusRequest => sender() ! VoteStatusReply(None)
    }

    def voted(candidate: String): Receive = {
      case VoteStatusRequest => sender() ! VoteStatusReply(Some(candidate))
    }
  }

  object VoteAggregator {
    case class AggregateVotes(citizens: Set[ActorRef])
  }

  class VoteAggregator extends Actor {
    import VoteAggregator._
    import Citizen._

    override def receive: Receive = {
      case AggregateVotes(citizens) =>
        context.become(countingVotes(Map(), citizens))
        citizens.foreach(citizen => citizen ! VoteStatusRequest)
    }

    def countingVotes(currentCount: Map[String, Int], remaining: Set[ActorRef]): Receive = {
      case VoteStatusReply(None) => sender() ! VoteStatusRequest // may end in a infinite loop if never votes
      case VoteStatusReply(Some(candidate)) =>
        val updatedRemaining = remaining - sender()
        val updatedCount = currentCount + (candidate -> (currentCount.getOrElse(candidate, 0) + 1))
        if (updatedRemaining.isEmpty) {
          println(updatedCount)
          println(s"${updatedCount.max._1} will be president with a total of ${updatedCount.max._2} votes!")
        }
        else
          context.become(countingVotes(updatedCount, remaining - sender()))
    }
  }

  val votingSystem = ActorSystem("votingSystem")

  val alice = votingSystem.actorOf(Props[Citizen], "alice")
  val bob = votingSystem.actorOf(Props[Citizen], "bob")
  val juancho = votingSystem.actorOf(Props[Citizen], "juancho")
  val daniel = votingSystem.actorOf(Props[Citizen], "daniel")

  import Citizen._

  alice ! Vote("Martin")
  alice ! Vote("Martin")
  alice ! Vote("Martin")
  bob ! Vote("Jacinto")
  bob ! Vote("John")
  juancho ! Vote("Robert")
  daniel ! Vote("Robert")

  val aggregator = votingSystem.actorOf(Props[VoteAggregator], "agregeitor")

  import VoteAggregator._

  aggregator ! AggregateVotes(Set(alice, bob, juancho, daniel)) // martin 1, jacinto 1, robert 2
}