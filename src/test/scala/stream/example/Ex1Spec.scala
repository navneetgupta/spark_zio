package stream.example

import zio.stream.{Sink, ZSink}
import zio.test.DefaultRunnableSpec
import zio.test._
import Assertion._

object Ex1Spec extends DefaultRunnableSpec {
  val suite1 = suite("timings")(
    testM("first attempt"){
      val stream = Ex1.myStream.take(30)
      val sink = Sink.collectAll[SimpleEvent]
      for {
        runner <- stream.run(sink)
      } yield assert(runner.size)(equalTo(30))
    }
  )

  def spec = suite("All tests")(suite1)
}
