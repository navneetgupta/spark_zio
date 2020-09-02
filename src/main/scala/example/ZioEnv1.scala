package example

import zio._
import zio.console.Console

object ZioEnv1 extends App {
  override def run(args: List[String]) = program.exitCode

  val logic = (for {
    _ <- console.putStrLn(s"I'm running!")
  } yield 0)
    //.catchAll(e => console.putStrLn(s"Application run failed $e").as(1))

  private val program = logic
}