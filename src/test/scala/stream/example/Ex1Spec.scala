package stream.example

import zio.stream.Sink
import zio.test.Assertion._
import zio.test.{DefaultRunnableSpec, _}

object Ex1Spec extends DefaultRunnableSpec {
  val suite1 = suite("timings")(
    testM("first attempt") {
      val stream = Ex1.myStream.take(30)
      val sink = Sink.collectAll[SimpleEvent]
      for {
        runner <- stream.run(sink)
      } yield assert(runner.size)(equalTo(30))
    }
  )

  def spec = suite("All tests")(suite1)
}
