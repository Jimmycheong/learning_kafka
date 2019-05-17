package com.jimmy.personal

import java.time.Duration
import java.util

import com.ovoenergy.kafka.serialization.avro4s._
import com.sksamuel.avro4s.{ToRecord, _}
import net.manub.embeddedkafka.Consumers
import net.manub.embeddedkafka.ops.ProducerOps
import net.manub.embeddedkafka.schemaregistry.{EmbeddedKafka, EmbeddedKafkaConfig, EmbeddedKafkaConfigImpl}
import org.apache.kafka.clients.consumer.{ConsumerRecords, KafkaConsumer}
import org.apache.kafka.common.serialization.{Deserializer, Serializer}
import org.scalatest.FlatSpec


case class Person(name: String, height: Int)

class KafkaAvroSpec extends FlatSpec with EmbeddedKafka with ProducerOps[EmbeddedKafkaConfig] with Consumers {

  "Something" should "" in {

    val topicName = "someTopic"
    val kafkaPort = 12345
    val schemaRegistryPort = 23456
    val schemaRegistryLocation = s"http://localhost:$schemaRegistryPort"

    implicit val PersonToRecord: ToRecord[Person] = ToRecord[Person]
    implicit val PersonFromRecord: FromRecord[Person] = FromRecord[Person]
    implicit val avroSerializer: Serializer[Person] = avroBinarySchemaIdSerializer(schemaRegistryLocation, isKey = false)
    implicit val avroDeserializer: Deserializer[Person] =  avroBinarySchemaIdDeserializer(schemaRegistryLocation, isKey = false)

    implicit val config: EmbeddedKafkaConfig = EmbeddedKafkaConfig()

    withRunningKafka {
      val message = Person("jimmy", 180)
      publishToKafka(topicName, message)

      withConsumer { consumer: KafkaConsumer[Person, Person] =>
        consumer.subscribe(util.Arrays.asList(topicName))

        val duration = Duration.ofMillis(4000)
        val records: ConsumerRecords[Person, Person] = consumer.poll(duration)

        records.forEach { record =>
          val recordValue = record.value
          println(s"name: ${recordValue.name}; height: ${recordValue.height}")
        }

      }


    }

  }

}
