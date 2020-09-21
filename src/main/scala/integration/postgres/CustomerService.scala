package integration.postgres

import zio.Has

object Customer {
  type Customer = Has[Customer.Service]

  object Service {

  }
}
