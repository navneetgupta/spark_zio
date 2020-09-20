

ThisBuild / scalaVersion := "2.13.2"
ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / organization := "com.example"
ThisBuild / organizationName := "example"

lazy val root = (project in file("."))
  .settings(
    name := "spark_cassandra",
  )

val flywayVersion = "6.5.6"
val logbackVersion = "1.2.3"
val zioVersion = "1.0.1"
val zioLoggingVersion = "0.4.0"
val testContainersVersion = "0.38.3"

libraryDependencies := Seq(
  // https://mvnrepository.com/artifact/org.apache.spark/spark-core
  //"org.apache.spark" %% "spark-core" % "3.0.0",
  //"com.datastax.spark" %% "spark-cassandra-connector" % "2.5.1",
  "dev.zio" %% "zio" % zioVersion,
  "dev.zio" %% "zio-streams" % zioVersion,
  "dev.zio" %% "zio-logging" % zioLoggingVersion,
  "dev.zio" %% "zio-test" % zioVersion % "test",
  "dev.zio" %% "zio-test-sbt" % zioVersion % "test",
  "org.scalatest" %% "scalatest" % "3.1.0" % "test",
  "dev.zio" %% "zio-test-magnolia" % zioVersion % "test", // optional
  "org.flywaydb"   % "flyway-core"                      % flywayVersion,
  "ch.qos.logback" % "logback-classic"                  % logbackVersion,
  "com.dimafeng"   %% "testcontainers-scala-scalatest"  % testContainersVersion % "test",
  "com.dimafeng"   %% "testcontainers-scala-postgresql" % testContainersVersion % "test",
)

// See https://www.scala-sbt.org/1.x/docs/Using-Sonatype.html for instructions on how to publish to Sonatype.
scalacOptions ++= Seq(
  "-deprecation",
  "-feature",
  "-Ywarn-value-discard",
  "-Xfatal-warnings"
)

testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")