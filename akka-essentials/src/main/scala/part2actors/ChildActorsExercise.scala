package part2actors

import akka.actor.{Actor, ActorRef, ActorSystem, Props}

/**
  * create WordCounterMaster
  * send Initialize(10) to WordCounterMaster
  * send "akka is awesome" to WordCounterMaster
  * wcm will sent a WordCountTask("akka is awesome") to one of its children
  * child replies with a WordCountReply(3) to the master
  * wcm replies with 3 to the sender
  *
  * requester -> wcm -> worker
  * requester <- wcm <-
  */

/**
  * Round robin logic:
  *
  * 1, 2, 3, 4, 5 workers with 7 tasks
  *
  * 1, 2, 3, 4, 5, 1, 2 order of workers executing tasks
  */
object ChildActorsExercise extends App {
  // Distributed Word counting

  object WordCounterMaster {
    case class Initialize(nChildren: Int)
    case class WordCountTask(id: Int, text: String)
    case class WordCountReply(id: Int, count: Int)
  }

  class WordCounterMaster extends Actor {
    import WordCounterMaster._

    override def receive: Receive = {
      case Initialize(nChildren) =>
        println(s"[master] initializing with $nChildren children")
        def createWorkers(amount: Int): Seq[ActorRef] =
          (1 to nChildren).map(i => context.actorOf(Props[WordCounterWorker], name = s"worker_$i"))

        context become withWorkers(
          workers = createWorkers(nChildren),
          currentWorkerIndex = 0,
          currentTaskId = 0,
          requestMap = Map()
        )
    }

    def withWorkers(workers: Seq[ActorRef], currentWorkerIndex: Int, currentTaskId: Int, requestMap: Map[Int, ActorRef]): Receive = {
      case text: String =>
        println(s"[master] received '$text', will send to ${workers(currentWorkerIndex).path.name} with id $currentTaskId")

        workers(currentWorkerIndex) ! WordCountTask(currentTaskId, text)

        context become withWorkers(
          workers,
          (currentWorkerIndex + 1) % workers.length,
          currentTaskId + 1,
          requestMap + (currentTaskId -> sender())
        )

      case WordCountReply(id, count) =>
        println(s"[master] received $count as result for task $id")

        requestMap(id) ! count

        context become withWorkers(
          workers,
          currentWorkerIndex,
          currentTaskId,
          requestMap - id
        )

    }
  }

  class WordCounterWorker extends Actor {
    import WordCounterMaster._

    override def receive: Receive = {
      case WordCountTask(id, text) =>
        println(s"[${self.path.name}] received '$text' from master with id $id")
        sender() ! WordCountReply(id, text.split(" ").length)
    }
  }

  class TestActor extends Actor {

    import WordCounterMaster._

    override def receive: Receive = {
      case "go" =>
        val master = system.actorOf(Props[WordCounterMaster])
        master ! Initialize(3)
        master ! "wow I love akka"
        master ! "akka is best"
        master ! "much distributed"
        master ! "wow"
        master ! "so actors"

      case count: Int => println(s"[test] received $count")
    }
  }


  val system = ActorSystem("wordCountSystem")
  val testActor = system.actorOf(Props[TestActor])
  testActor ! "go"
}
