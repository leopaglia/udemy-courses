package part2actors

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import com.typesafe.config.ConfigFactory

object IntroAkkaConfig extends App {

  class SimpleLoggingActor extends Actor with ActorLogging {
    override def receive: Receive = {
      case message => log.info(message.toString)
    }
  }

  /**
    * 1 - Inline configuration
    */

  val configString =
    """
      | akka {
      |   loglevel = "ERROR"
      | }
    """.stripMargin

  val config = ConfigFactory.parseString(configString)
  val system = ActorSystem("configDemo", ConfigFactory.load(config))
  val actor = system.actorOf(Props[SimpleLoggingActor])

  actor ! "A message to remember"

  /**
    * 2 - src/main/resources/application.conf is default conf path
    */

  val defaultConfigFileSystem = ActorSystem("defaultConfigFileDemo")
  val defaultConfigActor = defaultConfigFileSystem.actorOf(Props[SimpleLoggingActor])

  defaultConfigActor ! "Remember me"

  /**
    * 3 - Separated configurations in the same file
    */

  val specialConfigSystem = ActorSystem("specialConfigDemo", ConfigFactory.load().getConfig("mySpecialConfig"))
  val specialConfigActor = specialConfigSystem.actorOf(Props[SimpleLoggingActor])

  specialConfigActor ! "Remember me, I'm special"

  /**
    * 4 - Separated configurations in another file
    */

  val separatedConfigSystem = ActorSystem("separatedConfigDemo", ConfigFactory.load("secretFolder/secretConfig.conf"))
  val separatedConfigActor = separatedConfigSystem.actorOf(Props[SimpleLoggingActor])

  separatedConfigActor ! "Remember me, I'm separated"

  /**
    * 5 - Different file formats (json, properties)
    */
  val jsonConfig = ConfigFactory.load("json/jsonConfig.json")
  println(s"json config: ${jsonConfig.getString("aJsonProp")}")
  println(s"json config: ${jsonConfig.getString("akka.loglevel")}")

  val propsConfig = ConfigFactory.load("props/propConfig.properties")
  println(s"props config: ${propsConfig.getString("my.simpleProperty")}")
  println(s"props config: ${propsConfig.getString("akka.loglevel")}")
}
