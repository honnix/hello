import akka.actor._
import akka.actor.{TypedActor, TypedProps}
import akka.dispatch.Await
import akka.util.duration._

object TypedActorDemo extends App {
  val system = ActorSystem("TypedActorSystem")
  val mySquarer: Squarer =
    TypedActor(system).typedActorOf(TypedProps[SquarerImpl]())

  mySquarer.squareDontCare(10)
  println(mySquarer.squareNowPlease(10))
  println(mySquarer.squareNow(10))

  val f = mySquarer.square(10)
  println(Await.result(f, 1 second))
  system.shutdown
}
