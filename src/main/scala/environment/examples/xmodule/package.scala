package environment.examples

import environment.examples.XModule1.X
import zio._

package object xmodule {

  type XModule = Has[XModule.Service]

  def x[R <: XModule]: URIO[R, X] =
    ZIO.accessM(_.get.x)

  object XModule extends Serializable {

    val any: ZLayer[XModule, Nothing, XModule] =
      ZLayer.requires[XModule]
    val live: Layer[Nothing, XModule] =
      ZLayer.succeed(Service.live)

    trait Service extends Serializable {
      def x[R]: URIO[R, X]
    }

    object Service {
      val live: Service = new Service {
        override def x[R]: URIO[R, X] = ???
      }
    }
  }
}
