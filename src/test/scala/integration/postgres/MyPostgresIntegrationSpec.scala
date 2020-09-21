package integration.postgres

import zio.test.{DefaultRunnableSpec, suite, testM}
import zio.blocking.Blocking

object MyPostgresIntegrationSpec extends DefaultRunnableSpec {
  val postgresLayer = Blocking.live >>> TestContainer.postgres()
  val testEnv = zio.test.environment.testEnvironment ++ postgresLayer


  override def spec = suite("Postgres integration") {
    testM("Can create and fetch a customer") {
      for {
        userId <- CustomerService.create("testUser", "testPassword")
        result <- CustomerService.find(userId)
      } yield assert(result.id)(equalTo(userId)) &&
        assert(result.username)(equalTo("testUser"))
    }
  }.provideCustomLayer(testEnv) @@ migrate("customers", "filesystem:src/customers/resources/db/migration")
}
