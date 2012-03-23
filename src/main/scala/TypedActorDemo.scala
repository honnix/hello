import akka.actor._
import akka.actor.{TypedActor, TypedProps}

object TypedActorDemo extends App {
  val system = ActorSystem("TypedActorSystem")
  val mySquarer: Squarer =
    TypedActor(system).typedActorOf(TypedProps[SquarerImpl]())

  mySquarer.squareDontCare(10)
  println(mySquarer.squareNowPlease(10))

  system.shutdown
}
