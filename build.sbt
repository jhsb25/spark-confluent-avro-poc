name := "spark-confluent-avro-poc"

version := "0.1"

scalaVersion := "2.12.13"
mainClass in (Compile, run) := Some("poc.ConfluentAvroToStructureStreaming")
resolvers += "confluent" at "https://packages.confluent.io/maven/"

libraryDependencies += "org.apache.spark" %% "spark-core" % "2.4.7"
libraryDependencies += "org.apache.spark" %% "spark-sql-kafka-0-10" % "2.4.7"
libraryDependencies += "org.apache.spark" %% "spark-sql" % "2.4.7"
libraryDependencies += "za.co.absa" %% "abris" % "4.0.1"

dependencyOverrides ++= {
  Seq(
    "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.6.7.1",
    "com.fasterxml.jackson.core" % "jackson-databind" % "2.6.7",
    "com.fasterxml.jackson.core" % "jackson-core" % "2.6.7"
  )
}


