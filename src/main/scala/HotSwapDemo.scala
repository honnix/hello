import akka.actor._

class HotSwapActor extends Actor {
  import context._
  def angry: Receive = {
    case "foo" ⇒ sender ! "I am already angry?"
    case "bar" ⇒ become(happy)
  }
  
  def happy: Receive = {
    case "bar" ⇒ sender ! "I am already happy :-)"
    case "foo" ⇒ become(angry)
  }
  
  def receive = {
    case "foo" ⇒ become(angry)
    case "bar" ⇒ become(happy)
  }
}

case object Swap
class Swapper extends Actor {
  import context._
  import akka.event.Logging
  val log = Logging(system, this)
  
  def receive = {
    case Swap ⇒
      log.info("Hi")
      become {
        case Swap ⇒
          log.info("Ho")
          unbecome() // resets the latest 'become' (just for fun)
      }
  }
}

object SwapperApp extends App {
  val system = ActorSystem("SwapperSystem")
  val swap = system.actorOf(Props[Swapper], name = "swapper")
  swap ! Swap // logs Hi
  swap ! Swap // logs Ho
  swap ! Swap // logs Hi
  swap ! Swap // logs Ho
  swap ! Swap // logs Hi
  swap ! Swap // logs Ho
}

