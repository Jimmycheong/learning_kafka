package com.jimmy.personal

import java.time.Duration
import java.util

import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._
import net.manub.embeddedkafka.ops.ProducerOps
import net.manub.embeddedkafka.{Consumers, EmbeddedKafka, EmbeddedKafkaConfig}
import org.apache.kafka.clients.consumer.{ConsumerRecords, KafkaConsumer}
import org.apache.kafka.common.serialization.{StringDeserializer, StringSerializer}
import org.scalatest.FlatSpec


class NotificationProducerSpec extends FlatSpec with EmbeddedKafka with ProducerOps[EmbeddedKafkaConfig] with Consumers {

  "NotificationProducer" should "write messages to Kafka" in {

    val embeddedKafkaConfig = EmbeddedKafkaConfig(12345)
    val topicName = "someTopic"

    withRunningKafkaOnFoundPort(embeddedKafkaConfig) { implicit embeddedKafkaConfig: EmbeddedKafkaConfig => // the config is implicit here
      implicit val serializer: StringSerializer = new StringSerializer

      println("Step 1: Pushing case classes")
      val notifications = (1 to 10).map { i =>
        Notification("numberOfEmails", s"$i email in inbox").asJson.noSpaces
      }

      notifications.foreach { notification =>
        publishToKafka(topicName, notification)
      }

      implicit val stringDeserializer: StringDeserializer = new StringDeserializer
      withConsumer { consumer: KafkaConsumer[String, String] =>

        consumer.subscribe(util.Arrays.asList(topicName))

        val duration = Duration.ofMillis(10000)
        val records: ConsumerRecords[String, String] = consumer.poll(duration)

        records.forEach { record =>

          decode[Notification](record.value) match {
            case Right(value) => println(s"Sender: ${value.sender}, Message: ${value.message}")
          }
        }
      }
      println("Consumption complete")


    }

  }

}
