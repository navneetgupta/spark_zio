import Dependencies._

ThisBuild / scalaVersion     := "2.13.2"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "com.example"
ThisBuild / organizationName := "example"

lazy val root = (project in file("."))
  .settings(
    name := "spark_cassandra",
    libraryDependencies += scalaTest % Test
  )

libraryDependencies ++=Seq(
  // https://mvnrepository.com/artifact/org.apache.spark/spark-core
//  "org.apache.spark" %% "spark-core" % "3.0.0",
//  "com.datastax.spark" %% "spark-cassandra-connector" % "2.5.1",
  "dev.zio" %% "zio" % "1.0.1",
  "dev.zio" %% "zio-streams" % "1.0.1"
)

// See https://www.scala-sbt.org/1.x/docs/Using-Sonatype.html for instructions on how to publish to Sonatype.
