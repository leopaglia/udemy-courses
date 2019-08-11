package part2actors

import akka.actor.{Actor, ActorSystem, Props}

object ActorsIntro extends App {
  // part 1 - create actor system
  val actorSystem = ActorSystem("firstActorSystem")
  println(actorSystem.name)

  // part 2 - create actor
  class WordCountActor extends Actor {
    // internal data
    var totalWords = 0

    // behavior
    def receive: PartialFunction[Any, Unit] = {
      case message: String =>
        println(s"[word counter]: I have received: $message")
        totalWords += message.split(" ").length
      case m => println(s"[word counter]: I cannot understand ${m.toString}")
    }
  }

  // part 3 - instantiate actor
  val wordCounter = actorSystem.actorOf(Props[WordCountActor], "wordCounter")
  val anotherWordCounter = actorSystem.actorOf(Props[WordCountActor], "anotherWordCounter")

  // part 4 - communicate
  wordCounter ! "I am learning Akka"
  anotherWordCounter ! "Another message"

  class Person(name: String) extends Actor {
    override def receive: Receive = {
      case "hi" => println(s"[person]: hi there, my name is $name")
      case _ =>
    }
  }

  // instantiation only works inside Props, still not good practice
  val person = actorSystem.actorOf(Props(new Person("Bob")))

  person ! "hi"

  // best practice for creating an actor with constructor arguments:
  // factory companion object
  object Person {
    def props(name: String) = Props(new Person(name))
  }

  val person2 = actorSystem.actorOf(Person.props("Dan"))

  person2 ! "hi"
}