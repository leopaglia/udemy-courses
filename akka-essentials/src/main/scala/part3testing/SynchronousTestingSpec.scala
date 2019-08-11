package part3testing

import akka.actor.{Actor, ActorSystem, Props}
import akka.testkit.{CallingThreadDispatcher, TestActorRef, TestProbe}
import org.scalatest.{BeforeAndAfterAll, WordSpecLike}
import scala.concurrent.duration._

// without testkit for sync testing
class SynchronousTestingSpec extends WordSpecLike with BeforeAndAfterAll {
  implicit val system: ActorSystem = ActorSystem("SynchronousTestingSpec")

  override def afterAll(): Unit = {
    system.terminate()
  }

  import SynchronousTestingSpec._

  "A counter" should {
    "synchronously increase its counter" in {
      // testing with TestActorRef does messaging on the calling thread
      val counter = TestActorRef[Counter](Props[Counter]) // needs an implicit actor system
      counter ! Increment // counter has ALREADY received the message

      assert(counter.underlyingActor.count == 1)
    }

    // TestActorRef can inspect properties or execute the receive method itself
    "synchronously increase its counter at the call of the receive function" in {
      val counter = TestActorRef[Counter](Props[Counter])
      counter.receive(Increment)

      assert(counter.underlyingActor.count == 1)
    }

    "work on the calling thread dispatcher" in {
      // configuration to make actors work on the calling thread
      val counter = system.actorOf(Props[Counter].withDispatcher(CallingThreadDispatcher.Id))
      val probe = TestProbe()

      probe.send(counter, Read)
      probe.expectMsg(Duration.Zero, 0) // won't work without timeout 0 because it's synchronous
    }
  }
}

object SynchronousTestingSpec {
  case object Increment
  case object Read

  class Counter extends Actor {
    var count = 0
    override def receive: Receive = {
      case Increment => count += 1
      case Read => sender() ! count
    }
  }
}