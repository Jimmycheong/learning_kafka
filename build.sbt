name := "learning_kafka"

version := "0.1"

scalaVersion := "2.12.8"

libraryDependencies ++= Seq(
  "org.apache.kafka" %% "kafka" % "2.2.0",
  "org.scalatest" %% "scalatest" % "3.1.0-RC1" % Test,
  "io.github.embeddedkafka" %% "embedded-kafka" % "2.2.0" % "test"
)