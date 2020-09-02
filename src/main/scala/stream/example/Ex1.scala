package stream.example

import java.time.Instant

import zio.{Schedule, ZIO}
import zio.clock._
import zio.stream.ZStream
import zio.duration._
import zio.console._

import scala.concurrent.duration.Duration

object Ex1 {
  def myStream =
    ZStream.repeatEffect(
      for {
        at <- ZIO.accessM[Clock](_.get.currentDateTime)
        evt = SimpleEvent(at.toInstant)
        _ <- putStrLn(s"at $evt")
      } yield evt)
      //.schedule(Schedule.spaced(10.second))
}
case class SimpleEvent(at: Instant)