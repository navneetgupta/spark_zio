package integration.postgres

import com.dimafeng.testcontainers.PostgreSQLContainer
import zio.{Has, ZManaged, blocking}
import zio.blocking._

/**
 * ZIO’s ZManaged to wrap the creation and disposal of the container,
 * wrapping the actual creation and shutdown in effectBlocking to signal
 * to ZIO that this should be done on the blocking thread pool.
 * This makes our ZLayer require the Blocking service.
 * On shutdown, we stop the container.
 * If creating or stopping fails for any reason, we’d like our test to terminate,
 * which we do with .orDie, making any potential failures to be treated as defects,
 * causing ZIO to shut down. Finally, we turn the ZManaged into a ZLayer by calling
 * toLayer on it.
 *
 * We now have a Postgres layer to plug into the ZIO tests.
 * */
object TestContainer {

  type Postgres = Has[PostgreSQLContainer]

  def postgres(imageName: Option[String] = Some("postgres")) =
    ZManaged.make {
      effectBlocking {
        val container = new PostgreSQLContainer(
          dockerImageNameOverride = imageName)
        container.start
        container
      }.orDie
    }(container => effectBlocking(container.stop()).orDie).toLayer
}
