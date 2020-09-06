package environment.examples

import zio._

trait XModule {
  val xModule: XModule.Service[Any]
}
object XModule {
  case class X(x: String = "x", y: String = "y", z: String = "z")

  trait Service[R] {
    def x: ZIO[R, Nothing, X]
  }

  trait Live extends XModule {
    val xInstance: X

    val xModule: XModule.Service[Any] = new Service[Any] {
      override def x: ZIO[Any, Nothing, X] = UIO(xInstance)
    }
  }

  object factory extends XModule.Service[XModule] {
    override def x: ZIO[XModule, Nothing, X] = ZIO.environment[XModule].flatMap(_.xModule.x)
  }
}