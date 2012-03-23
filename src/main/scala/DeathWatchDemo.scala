import akka.actor._

object DeathWatchDemo extends App {
  class WatchActor extends Actor {
    val child = context.actorOf(Props.empty, "child")
    context.watch(child) // <-- this is the only call needed for registration
    var lastSender = system.deadLetters
    
    def receive = {
      case "kill"              ⇒ context.stop(child); println("kill child"); lastSender = sender
      case Terminated(`child`) ⇒ println("child terminated"); lastSender ! "finished"
    }
  }

  class Master extends Actor {
    def receive = {
      case "start" => 
        val watch = context.actorOf(Props[WatchActor], name = "watch")
        watch ! "kill"
      case "finished" => context.system.shutdown
    }
  }

  val system = ActorSystem("DeathWatchDemoSystem")
  system.actorOf(Props[Master], name = "master")
  
  val master = system.actorFor("/user/master")
  master ! "start"
}
