package environment.examples

import environment.examples.XModule1.X
import zio.{Has, Layer, URIO, ZIO, ZLayer}

package object xmodule {

  type XModule = Has[XModule.Service]

  object XModule extends Serializable {
    trait Service extends Serializable {
      def x[R]: URIO[R, X]
    }
    object Service {
      val live: Service = new Service {
        override def x[R]: URIO[R, X] = ???
      }
    }

    val any: ZLayer[XModule, Nothing, XModule] =
      ZLayer.requires[XModule]

    val live: Layer[Nothing, XModule] =
      ZLayer.succeed(Service.live)
  }

  def x[R <: XModule]: URIO[R, X] =
    ZIO.accessM(_.get.x)
}
