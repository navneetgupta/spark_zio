

ThisBuild / scalaVersion := "2.13.2"
ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / organization := "com.example"
ThisBuild / organizationName := "example"

lazy val root = (project in file("."))
  .settings(
    name := "spark_cassandra",
  )

libraryDependencies := Seq(
  // https://mvnrepository.com/artifact/org.apache.spark/spark-core
  //  "org.apache.spark" %% "spark-core" % "3.0.0",
  //  "com.datastax.spark" %% "spark-cassandra-connector" % "2.5.1",
  "dev.zio" %% "zio" % "1.0.1",
  "dev.zio" %% "zio-streams" % "1.0.1",
  "dev.zio" %% "zio-logging" % "0.4.0",
  "dev.zio" %% "zio-test" % "1.0.1" % "test",
  "dev.zio" %% "zio-test-sbt" % "1.0.1" % "test",
  "org.scalatest" %% "scalatest" % "3.1.0" % "test",
  "dev.zio" %% "zio-test-magnolia" % "1.0.1" % "test", // optional
)

// See https://www.scala-sbt.org/1.x/docs/Using-Sonatype.html for instructions on how to publish to Sonatype.
scalacOptions ++= Seq(
  "-deprecation",
  "-feature",
  "-Ywarn-value-discard",
  "-Xfatal-warnings"
)

testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")