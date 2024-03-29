package part3testing

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit, TestProbe}
import org.scalatest.{BeforeAndAfterAll, WordSpecLike}

class TestProbeSpec extends TestKit(ActorSystem("TestProbeSpec"))
  with ImplicitSender
  with WordSpecLike
  with BeforeAndAfterAll {

  override def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }

  import TestProbeSpec._

  "A master actor" should {
    "register a slave" in {
      val master = system.actorOf(Props[Master])
      val slave = TestProbe("slave") // like a mock

      master ! Register(slave.ref)

      expectMsg(RegistrationAck)
    }

    "send the work to the slave actor" in {
      val master = system.actorOf(Props[Master])
      val slave = TestProbe("slave")
      master ! Register(slave.ref)
      expectMsg(RegistrationAck) // expectMsg works in order, need to expect what comes

      val workload = "I love Akka"
      master ! Work(workload)

      slave.expectMsg(SlaveWork(workload, testActor)) // testActor is the implicit sender
      slave.reply(WorkCompleted(3, testActor)) // executing a reply to last sender

      expectMsg(Report(3))
    }

    "aggregate data correrctly" in {
      val master = system.actorOf(Props[Master])
      val slave = TestProbe("slave")
      master ! Register(slave.ref)
      expectMsg(RegistrationAck)

      val workload = "I love Akka"
      master ! Work(workload)
      master ! Work(workload)

      slave.receiveWhile() { // mock a reply for the probe, also asserts (if it does not receive slaveWork, it fails)
        case SlaveWork(`workload`, `testActor`) => slave.reply(WorkCompleted(3, testActor)) // backticks for exact match
      }

      expectMsg(Report(3))
      expectMsg(Report(6))
    }
  }
}

object TestProbeSpec {
  /*
    word counting actor hierarchy master-slave

    send some work to the master
    - master sends the slave the piece of work
    - slave processes the work and replies to master
    - master aggregates the result

    master sends the total count to the original requester
   */

  case class Work(text: String)
  case class Report(totalCount: Int)
  case class Register(slaveRef: ActorRef)
  case object RegistrationAck
  case class SlaveWork(text: String, originalRequester: ActorRef)
  case class WorkCompleted(count: Int, originalRequester: ActorRef)

  class Master extends Actor {
    override def receive: Receive = {
      case Register(slaveRef) =>
        context become online(slaveRef, 0)
        sender() ! RegistrationAck
      case _ => // ignore
    }

    def online(slaveRef: ActorRef, totalWordCount: Int): Receive = {
      case Work(text) => slaveRef ! SlaveWork(text, sender())
      case WorkCompleted(count, originalRequester) =>
        val newTotalWordCount = totalWordCount + count
        originalRequester ! Report(newTotalWordCount)
        context become online(slaveRef, newTotalWordCount)
    }
  }

  // class Slave extends Actor ...
}
