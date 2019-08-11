package part4faulttolerance

import akka.actor.{Actor, ActorLogging, ActorSystem, PoisonPill, Props}

object ActorLifecycle extends App {

//  case object StartChild
//
//  class LifecycleActor extends Actor with ActorLogging {
//
//    override def preStart: Unit = log info "I'm about to start"
//
//    override def postStop: Unit = log info "I've stopped"
//
//    override def receive: Receive = {
//      case StartChild => context actorOf(Props[LifecycleActor], "child")
//    }
//  }
//
  val system = ActorSystem("LifecycleDemo")
//  val parent = system.actorOf(Props[LifecycleActor], "parent")
//
//  parent ! StartChild
//  parent ! PoisonPill

  case object Fail
  case object FailChild
  case object Check
  case object CheckChild

  class Parent extends Actor {
    private val child = context actorOf(Props[ChildActor], "supervisedChild")

    override def receive: Receive = {
      case FailChild => child ! Fail
      case CheckChild => child ! Check
    }
  }

  class ChildActor extends Actor with ActorLogging {

    override def preStart: Unit = log info "supervised child about to start"

    override def postStop: Unit = log info "supervised child stopped"

    override def preRestart(reason: Throwable, message: Option[Any]): Unit =
      log info s"supervised child restarting because of ${reason.getMessage}"

    override def postRestart(reason: Throwable): Unit =
      log info "supervised child restarted"

    override def receive: Receive = {
      case Fail =>
        log warning "child gonna fail now"
        throw new RuntimeException("k imma fail")

      case Check => log info "im ok"
    }
  }

  // default supervision strategy:
  //   if a child throws an exception when processing a message, that message is
  //   removed from the mailbox and the child is then restarted

  val supervisor = system actorOf(Props[Parent], "supervisor")

  supervisor ! FailChild
  supervisor ! CheckChild
}
