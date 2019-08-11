package part3testing

import akka.actor.{Actor, ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit, TestProbe}
import com.typesafe.config.ConfigFactory
import org.scalatest.{BeforeAndAfterAll, WordSpecLike}

import scala.concurrent.duration._
import scala.util.Random

class TimedAssertionsSpec
  extends TestKit(ActorSystem(
    "TimedAssertionsSpec",
    ConfigFactory.load().getConfig("specialTimedAssertionsConfig")
  ))
  with ImplicitSender
  with WordSpecLike
  with BeforeAndAfterAll {

  override def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }

  import TimedAssertionsSpec._

  "A worker actor" should {
    val worker = system.actorOf(Props[WorkerActor])

    "reply with the meaning of life in a timely manner" in {
      within(500 milliseconds span, 1 second span) {
        worker ! "work"
        expectMsg(WorkResult(42))
      }
    }

    "reply with valid work at a reasonable cadence" in {
      within(1 second span) {
        worker ! "WorkSequence"

        // listen 2 seconds to messages
        // finishes early if 500 ms passes without receiving a message
        // receives up to 10 messages
        // receives only WorkResults
        // returns a Seq with return values from the partial function
        val results = receiveWhile(max = 2 seconds span, idle = 500 milliseconds span, messages = 10) {
          case WorkResult(result) => result
        }

        // within 1 second I should have received at least 5 results
        assert(results.sum > 5)
      }
    }

    "reply to a test probe in a timely manner" in {
      within(1 second span) {
        val probe = TestProbe()
        probe.send(worker, "work")
        probe.expectMsg(WorkResult(42)) // will fail because probes have its own timeout configuration
      }
    }
  }
}

object TimedAssertionsSpec {

  case class WorkResult(result: Int)

  class WorkerActor extends Actor {
    override def receive: Receive = {
      case "work" =>
        // long computation
        Thread.sleep(500)
        sender() ! WorkResult(42)

      case "workSequence" =>
        val r = new Random()
        // many short computations reply
        (1 to 10) foreach { _ =>
          Thread.sleep(r.nextInt(50))
          sender() ! WorkResult(1)
        }
    }
  }

}
