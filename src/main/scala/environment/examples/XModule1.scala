package environment.examples

import zio._

trait XModule1 {
  val xModule: XModule1.Service[Any]
}

object XModule1 {

  trait Service[R] {
    def x: ZIO[R, Nothing, X]
  }

  trait Live extends XModule1 {
    val xInstance: X

    val xModule: XModule1.Service[Any] = new Service[Any] {
      override def x: ZIO[Any, Nothing, X] = UIO(xInstance)
    }
  }

  case class X(x: String = "x", y: String = "y", z: String = "z")

  object factory extends XModule1.Service[XModule1] {
    override def x: ZIO[XModule1, Nothing, X] = ZIO.environment[XModule1].flatMap(_.xModule.x)
  }

}