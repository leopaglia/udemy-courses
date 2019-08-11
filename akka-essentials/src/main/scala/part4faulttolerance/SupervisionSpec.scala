package part4faulttolerance

import akka.actor.SupervisorStrategy.{Escalate, Restart, Resume, Stop}
import akka.actor.{Actor, ActorRef, ActorSystem, AllForOneStrategy, OneForOneStrategy, Props, SupervisorStrategy, Terminated}
import akka.testkit.{EventFilter, ImplicitSender, TestKit}
import org.scalatest.{BeforeAndAfterAll, WordSpecLike}

class SupervisionSpec extends TestKit(ActorSystem("SupervisionSpec"))
  with ImplicitSender
  with WordSpecLike
  with BeforeAndAfterAll {

  override def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }

  import SupervisionSpec._

  "A supervisor" should {
    "resume its child in case of a minor fault" in {
      val supervisor = system actorOf Props[Supervisor]
      supervisor ! Props[FussyWordCounter]
      val child = expectMsgType[ActorRef]

      child ! "I love Akka"
      child ! Report
      expectMsg(3)

      child ! "Akka is awesome because I am learning to think in a whole new way"
      child ! Report
      expectMsg(3) // keeps state because it resumes (is this a good way to test it? :|)
    }

    "restart its child in case of an empty sentence" in {
      val supervisor = system actorOf Props[Supervisor]
      supervisor ! Props[FussyWordCounter]
      val child = expectMsgType[ActorRef]

      child ! "I love Akka"
      child ! Report
      expectMsg(3)

      child ! ""
      child ! Report
      expectMsg(0) // resets state because it restarts (it does not seem like is the best way at all)
    }

    "terminate the child in case of a major error" in {
      val supervisor = system actorOf Props[Supervisor]
      supervisor ! Props[FussyWordCounter]
      val child = expectMsgType[ActorRef]

      watch(child) // register testactor as watcher
      child ! "akka is nice"
      val terminatedMessage = expectMsgType[Terminated]

      assert(terminatedMessage.actor == child)
    }

    "escalate an error when it does not know what to do" in {
      val supervisor = system actorOf Props[Supervisor]
      supervisor ! Props[FussyWordCounter]
      val child = expectMsgType[ActorRef]

      watch(child)
      child ! 43
      val terminatedMessage = expectMsgType[Terminated]
      assert(terminatedMessage.actor == child) // same exception is thrown by the supervisor this time
    }
  }

  "A kinder supervisor" should {
    "not kill children in case it's restarted or escalates failures" in {
      val supervisor = system actorOf Props[NoDeathOnRestartSupervisor]
      supervisor ! Props[FussyWordCounter]
      val child = expectMsgType[ActorRef]

      child ! "Akka is cool"
      child ! Report
      expectMsg(3)

      child ! 42
      child ! Report
      expectMsg(0) // wont kill the child because of preRestart being overriden
    }
  }

  "An all-for-one supervisor" should {
    "apply the all-for-one strategy" in {
      val supervisor = system actorOf (Props[AllForOneSupervisor], "allForOneSupervisor")

      supervisor ! Props[FussyWordCounter]
      val child = expectMsgType[ActorRef]

      supervisor ! Props[FussyWordCounter]
      val secondChild = expectMsgType[ActorRef]

      secondChild ! "Testing supervisor"
      secondChild ! Report
      expectMsg(2)

      EventFilter[NullPointerException]() intercept { // asserting child throws
        child ! ""
      }

      Thread sleep 500 // bad practice, just for educational purposes :)

      secondChild ! Report
      expectMsg(0) // secondChild was restarted because child failed with NPE
    }
  }
}

object SupervisionSpec {

  class Supervisor extends Actor {
    // 1 for 1 applies the strat on the exact actor that failed
    override val supervisorStrategy: SupervisorStrategy = OneForOneStrategy() {
      case _: NullPointerException => Restart
      case _: IllegalArgumentException => Stop
      case _: RuntimeException => Resume
      case _: Exception => Escalate
    }

    override def receive: Receive = {
      case props: Props =>
        val childRef = context actorOf props
        sender() ! childRef
    }
  }

  class NoDeathOnRestartSupervisor extends Supervisor {
    override def preRestart(reason: Throwable, message: Option[Any]): Unit = ()
  }

  class AllForOneSupervisor extends Supervisor {
    // all for 1 applies the strat on every supervised actor
    override val supervisorStrategy: SupervisorStrategy = AllForOneStrategy() {
      case _: NullPointerException => Restart
      case _: IllegalArgumentException => Stop
      case _: RuntimeException => Resume
      case _: Exception => Escalate
    }
  }

  case object Report

  class FussyWordCounter extends Actor {
    var words = 0

    override def receive: Receive = {
      case Report => sender() ! words
      case "" => throw new NullPointerException("sentence is empty")
      case sentence: String =>
        if (sentence.length > 20) throw new RuntimeException("sentence too big")
        else if (!Character.isUpperCase(sentence(0))) throw new IllegalArgumentException("sentence must start with uppercase")
        else words += (sentence split " ").length
      case _ => throw new Exception("can only receive strings")
    }
  }
}
