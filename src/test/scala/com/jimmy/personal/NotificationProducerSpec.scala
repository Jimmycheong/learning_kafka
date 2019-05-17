package com.jimmy.personal

import java.time.Duration
import java.util

import net.manub.embeddedkafka.{Consumers, EmbeddedKafka, EmbeddedKafkaConfig}
import org.apache.kafka.clients.consumer.{ConsumerRecords, KafkaConsumer}
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import org.apache.kafka.common.serialization.{Deserializer, StringDeserializer, StringSerializer}
import org.scalatest.FlatSpec

class NotificationProducerSpec extends FlatSpec with EmbeddedKafka with Consumers {

  "NotificationProducer" should "write messages to Kafka" in {

    val embeddedKafkaConfig = EmbeddedKafkaConfig(12345)

    withRunningKafkaOnFoundPort(embeddedKafkaConfig) { implicit embeddedKafkaConfig =>

      println("Step 1")
      val topicName = "someTopic"

      val bootstrapServers = s"localhost:${embeddedKafkaConfig.kafkaPort}"
      val notificationProducer: KafkaProducer[String, String] = NotificationProducer(topicName, bootstrapServers)

      val records = (1 to 10).map(currentNumber =>
        new ProducerRecord[String, String](topicName, currentNumber.toString, s"value_$currentNumber")
      )

      println("Step 2")
      records.foreach { record =>
        println(s"Record: ${record.value()} sent")
        notificationProducer.send(record)
      }

      notificationProducer.close()
      implicit val stringDeserializer = new StringDeserializer

      println("Step 3")


      withConsumer { consumer: KafkaConsumer[String, String] =>

        consumer.subscribe(util.Arrays.asList(topicName))

        val duration = Duration.ofMillis(10000)
        val records: ConsumerRecords[String, String] = consumer.poll(duration)

        records.forEach { record =>
          println(
            s"""
               | Key: ${record.key}
               | Value: ${record.value}
            """.stripMargin)
        }
      }

    }


  }

}
