import akka.dispatch.{Promise, Future, Await}
import akka.util.duration._
import akka.actor.{ActorContext, TypedActor, TypedProps}

trait Squarer {
  def squareDontCare(i: Int): Unit //fire-forget
 
  def square(i: Int): Future[Int] //non-blocking send-request-reply
 
  def squareNowPlease(i: Int): Option[Int] //blocking send-request-reply
 
  def squareNow(i: Int): Int //blocking send-request-reply
}
