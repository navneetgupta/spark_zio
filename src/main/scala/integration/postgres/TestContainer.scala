package integration.postgres

import zio._
import com.dimafeng.testcontainers.PostgreSQLContainer

object TestContainer {
  type Postgres = Has[PostgreSQLContainer]
}
