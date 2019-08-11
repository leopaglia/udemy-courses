package part4faulttolerance

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Kill, PoisonPill, Props, Terminated}

object StartingStoppingActors extends App {

  val system = ActorSystem("StoppingActorsDemo")

  object Parent {
    case class StartChild(name: String)
    case class StopChild(name: String)
    case object Stop
  }

  class Parent extends Actor with ActorLogging {
    import Parent._
    override def receive: Receive = withChildren(Map())

    def withChildren(children: Map[String, ActorRef]): Receive = {
      case StartChild(name) =>
        log.info(s"Starting child $name")
        context become withChildren(children + (name -> context.actorOf(Props[Child], name)))

      case StopChild(name) =>
        log.info(s"Stopping child $name")
        val childOption = children.get(name)
        childOption.foreach(childRef => context stop childRef) // stop is non blocking, async

      case Stop =>
        log.info("Stopping myself")
        context stop self // stops every child first, then self

      case msg => log.info(msg.toString)
    }
  }

  class Child extends Actor with ActorLogging {
    override def receive: Receive = {
      case msg => log.info(msg.toString)
    }
  }


  /**
    * method #1 - using context.stop
    */
//   import Parent._
//
//   val parent = system.actorOf(Props[Parent], "parent")
//   parent ! StartChild("child1")
//
//   val child = system.actorSelection("/user/parent/child1")
//   child ! "Hi kid!"
//
//   parent ! StopChild("child1")
//   for(_ <- 1 to 50) child ! "u still there??" // testing asynchronism of context.stop
//
//   parent ! StartChild("child2")
//
//   val child2 = system.actorSelection("/user/parent/child2")
//   child2 ! "Hi second boi"
//
//   parent ! Stop
//   for(_ <- 1 to 100) parent ! "parent r u ded??"
//   for(i <- 1 to 100) child2 ! s"[$i] kid2 r u ded??!1?"

  /**
    * method #2 - using special messages
    */
//  val looseActor = system.actorOf(Props[Child])
//  looseActor ! "hello, loose actor"
//  looseActor ! PoisonPill // lol akka pls... wtf
//  looseActor ! "loose actor u ded?"
//
//  val abruplyTerminatedActor = system.actorOf(Props[Child])
//  abruplyTerminatedActor ! "you are about to be terminated"
//  abruplyTerminatedActor ! Kill // this is heavier than poison pill, throws exception
//  abruplyTerminatedActor ! "you have been terminated"

  /**
    * death watch
    */
  class Watcher extends Actor with ActorLogging {
    import Parent._

    override def receive: Receive = {
      case StartChild(name) =>
        val child = context actorOf (Props[Child], name)
        log.info(s"Started and watching child $name")
        context watch child

      case Terminated(ref) =>
        log.info(s"the reference that I'm watching $ref has been stopped")
    }
  }

  import Parent._

  val watcher = system.actorOf(Props[Watcher], "watcher")
  watcher ! StartChild("watchedChild")

  val watchedChild = system.actorSelection("/user/watcher/watchedChild")
  Thread.sleep(500) // wait for the actor for being created (test purposes only)

  watchedChild ! PoisonPill
}
