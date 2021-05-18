package integration.postgres

import zio.blocking.Blocking
import zio.{Has, Layer, RIO, UIO, ZIO, ZLayer, blocking}

package object customer {
  case class User(id: Option[Long], username: String, password: String)

  type Customer = Has[Customer.Service]

  object Customer extends Serializable {
    trait Service extends Serializable {
      def create(username: String, password: String): RIO[Blocking, Long]
      def find(userId: Long): UIO[Option[User]]
    }
    object Service {
      val live: Service = new Service {

        var map:Map[Long, User] = Map()
        var count:Long = 0L

        override def create(username: String, password: String): RIO[Blocking, Long] = blocking.effectBlocking {
          count = count+1L;
          map updated (count, User(Some(count), username, password))
          count
        }

        override def find(userId: Long): UIO[Option[User]] = ZIO.succeed(map.get(userId))
      }
    }
    val any: ZLayer[Customer, Nothing, Customer] =
      ZLayer.requires[Customer]

    val live: Layer[Nothing, Customer] =
      ZLayer.succeed(Service.live)
  }

  def create(username: => String, password: => String) : RIO[Blocking with Customer, Long] =
    ZIO.accessM(_.get.effectBlocking{create(username,password)}).flatten

  def find(userId: => Long): RIO[Customer, Option[User]] =
    ZIO.accessM(_.get.find(userId))
}
