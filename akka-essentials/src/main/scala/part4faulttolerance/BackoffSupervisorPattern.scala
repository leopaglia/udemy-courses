package part4faulttolerance

import java.io.File

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}

import scala.io.Source

object BackoffSupervisorPattern extends App {

  case object ReadFile

  class FileBasedPersistentActor extends Actor with ActorLogging {
    var dataSource: Source = null

    override def preStart(): Unit = log info "Persistent actor starting"

    override def postStop(): Unit = log info "Persistent actor has stopped"

    override def preRestart(reason: Throwable, message: Option[Any]): Unit = log warning "Persistent actor restarting"

    override def receive: Receive = {
      case ReadFile =>
        if (dataSource == null) dataSource = Source.fromFile(new File("src/main/resources/testFiles/important.txt"))
        log info s"I've just read some important data: ${dataSource.getLines.toList}"
    }
  }

  val system = ActorSystem("BackoffSupervisionDemo")
  val actor = system actorOf (Props[FileBasedPersistentActor], "actor")
  actor ! ReadFile

  
}
