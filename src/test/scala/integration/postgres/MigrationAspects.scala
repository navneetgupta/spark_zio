package integration.postgres

import com.dimafeng.testcontainers.PostgreSQLContainer
import org.flywaydb.core.Flyway
import zio.ZIO
import zio.blocking._

/**
 * Once our container has started, we want to populate the database with our schema,
 * before the tests are run. For this, ZIO Tests provides a mechanism called Test Aspects,
 * which allows, among other things, to execute an action before executing the tests.
 * We can create a Test Aspect that runs the migration:
 * */
object MigrationAspects {
  def migrate(schema: String, paths: String*) = {
    val migrations =
      for {
        pg <- ZIO.service[PostgreSQLContainer]
        _ <- runMigration(pg.jdbcUrl, pg.username, pg.password, schema, paths: _*)
      } yield ()
  }

  private def runMigration(
                          url: String,
                          username: String,
                          password: String,
                          schema: String,
                          locations: String*
                          ) = {
    //  Again, because Flyway is a Java API, we wrap it with effectBlocking to ensure ZIO runs it on the blocking thread pool.
    effectBlocking {
      Flyway
        .configure()
        .dataSource(url, username, password)
        .schemas(schema)
        .locations(locations: _*)
        .load()
        .migrate()
    }
  }
}
