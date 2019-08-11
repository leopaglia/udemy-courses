package part2actors

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.event.Logging

/**
  * Log levels:
  *
  * 1 - DEBUG
  * 2 - INFO
  * 3 - WARN
  * 4 - ERROR
  */
object ActorLoggingDemo extends App {

  class SimpleActorWithExplicitLogger extends Actor {
    val logger = Logging(context.system, this)

    override def receive: Receive = {
      case message => logger.info(message.toString)
    }
  }

  class ActorWithLogging extends Actor with ActorLogging {
    override def receive: Receive = {
      case (a, b) => log.info(s"Two things: {} and {}", a, b) // interpolating parameters
      case message => log.info(message.toString)
    }
  }

  val system = ActorSystem("loggingDemo")
  val simpleActorWithExplicitLogger = system.actorOf(Props[SimpleActorWithExplicitLogger], "simpleActor")
  val actorWithLoggerTrait = system.actorOf(Props[ActorWithLogging], "simpleActor2")


  simpleActorWithExplicitLogger ! "logging a simple message with explicit logger"
  actorWithLoggerTrait ! "logging a simple message extending logging trait"
  actorWithLoggerTrait ! ("one", "another")
}
