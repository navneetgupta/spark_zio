package environment.examples

import environment.examples.XModule1._
import zio._
import zio.console._

object Xmodule1Main extends App {
  val logic = (for {
    _ <- console.putStrLn(s"I'm running!")
    x <- XModule1.factory.x
    _ <- console.putStrLn(s"I've got an $x!")
    _ <- IO.fail("Failing")
  } yield 0)
    .catchAll(e => console.putStrLn(s"Application run failed:  $e").as(1))
  private val program = logic.provideSomeLayer[Console](
    new XModule1.Live {
      override val xInstance: X = X()
    }
  )

  def run(args: List[String]) = program.exitCode

}
