package environment.examples

import zio._

object ZioEnv1 extends App {
  val logic = (for {
    _ <- console.putStrLn(s"I'm running!")
  } yield 0)
  private val program = logic
  //.catchAll(e => console.putStrLn(s"Application run failed $e").as(1))

  override def run(args: List[String]) = program.exitCode
}

object ZioEnv2 extends App {
  val logic = (for {
    _ <- console.putStrLn(s"I'm running!")
    _ <- IO.fail("Failing")
  } yield 0)
    .catchAll(e => console.putStrLn(s"Application run failed with error: $e") as (1))
  private val program = logic

  override def run(args: List[String]) = program.exitCode
}