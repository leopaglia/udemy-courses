package part3testing

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.testkit.{EventFilter, ImplicitSender, TestKit}
import com.typesafe.config.ConfigFactory
import org.scalatest.{BeforeAndAfterAll, WordSpecLike}

class InterceptingLogsSpec
  extends TestKit(
    ActorSystem(
      "InterceptingLogsSpec",
      ConfigFactory.load().getConfig("interceptingLogMessages")
    ))
    with ImplicitSender
    with WordSpecLike
    with BeforeAndAfterAll {

  override def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }

  import InterceptingLogsSpec._

  // integration test via logs
  "A checkout flow" should {
    "correctly log the dispatch of an order" in {
      val item = "Rock the JVM Akka course"
      val pattern = s"Order [0-9+] for item $item has been dispatched."
      val creditCard = "1234-1234-1234-1234"

      EventFilter.info(pattern = pattern, occurrences = 1) intercept {
        val checkoutRef = system.actorOf(Props[CheckoutActor])
        checkoutRef ! Checkout(item, creditCard)
      }
    }

    "freak out if the payment is denied" in {
      val item = "Rock the JVM Akka course"
      val invalidCreditCard = "0234-1234-1234-1234"

      EventFilter[RuntimeException](occurrences = 1) intercept {
        val checkoutRef = system.actorOf(Props[CheckoutActor])
        checkoutRef ! Checkout(item, invalidCreditCard)
      }
    }
  }
}

object InterceptingLogsSpec {

  case class Checkout(item: String, creditCard: String)

  case class AuthorizeCard(creditCard: String)
  case object PaymentAccepted
  case object PaymentDenied

  case class DispatchOrder(item: String)
  case object OrderConfirmed

  class CheckoutActor extends Actor {
    // no dep injection, so it's hard to inject probes
    // checkout actor does not send any messages or reply, so it's hard to test with messages
    private val paymentManager = context.actorOf(Props[PaymentManager])
    private val fulfillmentManager = context.actorOf(Props[FulfillmentManager])

    override def receive: Receive = awaitingCheckout

    def awaitingCheckout: Receive = {
      case Checkout(item, creditCard) =>
        paymentManager ! AuthorizeCard(creditCard)
        context become pendingPayment(item)
    }

    def pendingPayment(item: String): Receive = {
      case PaymentAccepted =>
        fulfillmentManager ! DispatchOrder(item)
        context become pendingFulfillment(item)
      case PaymentDenied =>
        throw new RuntimeException("OMG I CANT HANDLE DIS ANYMOAR")
    }

    def pendingFulfillment(item: String): Receive = {
      case OrderConfirmed => context become awaitingCheckout
    }
  }

  class PaymentManager extends Actor {
    override def receive: Receive = {
      case AuthorizeCard(creditCard) =>
        if(creditCard startsWith "0") sender() ! PaymentDenied
        else sender() ! PaymentAccepted
    }
  }

  class FulfillmentManager extends Actor with ActorLogging {
    var orderId = 0
    override def receive: Receive = {
      case DispatchOrder(item: String) =>
        orderId += 1
        log.info(s"Order $orderId for item $item has been dispatched.")
        sender() ! OrderConfirmed
    }
  }
}
