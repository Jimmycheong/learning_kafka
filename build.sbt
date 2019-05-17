name := "learning_kafka"

version := "0.1"

scalaVersion := "2.12.8"

resolvers ++= Seq(
  "confluent" at "https://packages.confluent.io/maven/",
  Resolver.bintrayRepo("ovotech", "maven")
)

libraryDependencies ++= Seq(
  "org.apache.kafka" %% "kafka" % "2.2.0",
  "io.github.embeddedkafka" %% "embedded-kafka" % "2.2.0",
  "io.github.embeddedkafka" %% "embedded-kafka-schema-registry" % "5.2.1",
  "org.glassfish.jersey.bundles.repackaged" % "jersey-guava" % "2.25.1", // Needed
  "org.scalatest" %% "scalatest" % "3.1.0-RC1" % Test
) ++ circeDeps ++ ovoDeps


val circeDeps = Seq (
  "io.circe" %% "circe-core",
  "io.circe" %% "circe-generic",
  "io.circe" %% "circe-parser"
).map (_ % "0.10.0")

val ovoDeps = {
  val kafkaSerializationV = "0.1.23" // see the Maven badge above for the latest version
  Seq(
    "com.ovoenergy" %% "kafka-serialization-core" % kafkaSerializationV,
    "com.ovoenergy" %% "kafka-serialization-avro4s" % kafkaSerializationV  // To provide Avro4s Avro support
  )
}
