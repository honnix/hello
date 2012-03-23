import akka.actor._
import akka.routing.RoundRobinRouter
import akka.util.Duration
import akka.util.duration._

object Pi extends App {
  sealed trait PiMessage
  case object Calculate extends PiMessage
  case class Work(start: Int, nrOfElements: Int) extends PiMessage
  case class Result(value: Double) extends PiMessage
  case class PiApproximation(pi: Double, duration: Duration)

  class Worker extends Actor {
    private def calculatePiFor(start: Int, nrOfElements: Int): Double = {
      Range(start, start + nrOfElements).foldLeft(0.0) { (acc, i) =>
        acc + (4.0 * (1 - (i % 2) * 2) / (2 * i + 1))
      }
    }

    override def preStart() = {
      println(Thread.currentThread.getName)
    }

    def receive = {
      case Work(start, nrOfElements) =>
        sender ! Result(calculatePiFor(start, nrOfElements))
    }
  }

  class Master(nrOfWorkers: Int, nrOfMessages: Int, nrOfElements: Int, listener: ActorRef)
  extends Actor {
    var pi: Double = _
    var nrOfResults: Int = _
    val start: Long = System.currentTimeMillis

    val workerRouter = context.actorOf (
      Props[Worker].withRouter(RoundRobinRouter(nrOfWorkers)),
      name = "workerRouter"
    )

    def receive = {
      case Calculate =>
        Range(0, nrOfMessages) foreach { i =>
          workerRouter ! Work(i * nrOfElements, nrOfElements)
        }
      case Result(value) =>
        pi += value
        nrOfResults += 1
        if (nrOfResults == nrOfMessages) {
          listener ! PiApproximation(pi, duration = (System.currentTimeMillis - start).millis)
          context.stop(self)
        }
    }
  }

  class Listener extends Actor {
    def receive = {
      case PiApproximation(pi, duration) =>
        println("\n\tPi approximation: \t\t%s\n\tCalculation time: \t%s"
                .format(pi, duration))
        context.system.shutdown
    }
  }

  private def calculate(nrOfWorkers: Int, nrOfElements: Int, nrOfMessages: Int) {
    val system = ActorSystem("PiSystem")
    val listener = system.actorOf(Props[Listener], name = "listener")
    val master = system.actorOf(Props(new Master(
      nrOfWorkers, nrOfMessages, nrOfElements, listener)),
                                name = "master")

    master ! Calculate
  }

  calculate(nrOfWorkers = 4, nrOfElements = 10000, nrOfMessages = 10000)
}
