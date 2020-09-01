package example

import zio._
import zio.console.Console

object ZioEnv1 extends App {
  override def run(args: List[String]) = program.fold( _ =>0, _ => 1)

  val logic = (for {
    _ <- console.putStrLn(s"I'm running!")
  } yield 0)

  private val program = logic
}