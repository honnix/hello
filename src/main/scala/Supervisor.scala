import akka.actor._
import akka.util.duration._
import akka.actor.SupervisorStrategy._

class Worker extends Actor {
  def receive = {
    case 1 => throw new ArithmeticException
    case 2 => throw new NullPointerException
    case 3 => throw new Exception
  }
}

class Supervisor extends Actor {
  override val supervisorStrategy =
    OneForOneStrategy(maxNrOfRetries = 10, withinTimeRange = 1 minute) {
      case _: ArithmeticException      ⇒ println("resume"); Resume
      case _: NullPointerException     ⇒ println("restart"); Restart
      case _: Exception                ⇒ println("escalate"); Escalate
    }
  
  val worker = context.actorOf(Props[Worker])
  
  def receive = {
    case n: Int => worker forward n
  }
}

object Supervisor extends App {
  val system = ActorSystem("SupervisorDemoSystem")
  val supervisor = system.actorOf(Props[Supervisor], name = "supervisor")

  for (i <- 0 to 100) supervisor ! 2

  for (i <- 0 to 100) supervisor ! 3
}
