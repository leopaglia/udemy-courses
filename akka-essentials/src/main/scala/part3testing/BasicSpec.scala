package part3testing

import akka.actor.{Actor, ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.{BeforeAndAfterAll, WordSpecLike}

import scala.concurrent.duration._
import scala.util.Random

class BasicSpec extends TestKit(ActorSystem("BasicSpec"))
  with ImplicitSender
  with WordSpecLike
  with BeforeAndAfterAll {

  override def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }

  import BasicSpec._

  "A simple actor" should {
    "send back the same message" in {
      val echoActor = system.actorOf(Props[SimpleActor])
      val message = "Hello, test!"
      echoActor ! message

      expectMsg(message) // to configure timeout: akka.test.single-expect-default - 3 seconds default
    }
  }

  "A blackhole actor" should {
    "send back some message" in {
      val blackholeActor = system.actorOf(Props[BlackHole])
      val message = "Hello, test!"
      blackholeActor ! message

      expectNoMessage(1 second span)
    }
  }

  "A labtest actor" should {
    val labTestActor = system.actorOf(Props[LabTestActor])

    "turn a string into uppercase" in {
      labTestActor ! "I love Akka"
      val reply = expectMsgType[String]

      assert(reply == "I LOVE AKKA")
    }

    "reply to a greeting" in {
      labTestActor ! "greeting"
      expectMsgAnyOf("hi", "hello")
    }

    "reply with favorite tech" in {
      labTestActor ! "favoriteTech"
      expectMsgAllOf("Scala", "Akka")

      // val messages = receiveN(2) // Seq[Any] - will fail if receives less than n in timeout seconds

      // expectMsgPF() {
      //   case "Scala" => // only care that the PF is defined
      //   case "Akka" => // only care that the PF is defined
      // }
    }
  }

}

object BasicSpec {
  class SimpleActor extends Actor {
    override def receive: Receive = {
      case message => sender() ! message
    }
  }

  class BlackHole extends Actor {
    override def receive: Receive = Actor.emptyBehavior
  }

  class LabTestActor extends Actor {
    val random = new Random()

    override def receive: Receive = {
      case "greeting" =>
        if(random.nextBoolean()) sender() ! "hi"
        else sender() ! "hello"

      case "favoriteTech" =>
        sender() ! "Scala"
        sender ! "Akka"

      case message: String =>
        sender() ! message.toUpperCase
    }
  }
}