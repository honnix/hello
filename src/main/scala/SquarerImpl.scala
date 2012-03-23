import akka.dispatch.{Promise, Future, Await}
import akka.util.duration._
import akka.actor.{ActorContext, TypedActor, TypedProps}
 
class SquarerImpl(val name: String) extends Squarer {
 
  def this() = this("default")
  import TypedActor.dispatcher //So we can create Promises
 
  def squareDontCare(i: Int): Unit = i * i //Nobody cares :(
 
  def square(i: Int): Future[Int] = Promise successful i * i
 
  def squareNowPlease(i: Int): Option[Int] = Some(i * i)
 
  def squareNow(i: Int): Int = i * i
}
