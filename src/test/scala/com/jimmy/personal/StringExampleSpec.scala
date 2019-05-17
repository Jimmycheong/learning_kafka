package com.jimmy.personal

import java.time.Duration
import java.util

import net.manub.embeddedkafka.{Consumers, EmbeddedKafka, EmbeddedKafkaConfig}
import org.apache.kafka.clients.consumer.{ConsumerRecords, KafkaConsumer}
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import org.apache.kafka.common.serialization.{Deserializer, StringDeserializer, StringSerializer}
import org.scalatest.FlatSpec

class StringExampleSpec extends FlatSpec with EmbeddedKafka with Consumers {

  "This test" should "write and read string messages to Kafka" in {

    val kafkaPort = 12345
    val topicName = "someTopic"
    val embeddedKafkaConfig = EmbeddedKafkaConfig(kafkaPort)
    val bootstrapServers = s"localhost:${embeddedKafkaConfig.kafkaPort}"

    withRunningKafkaOnFoundPort(embeddedKafkaConfig) { implicit embeddedKafkaConfig => // the config is implicit here

      println("Step 1: Preparing the producer and data to send")

      val notificationProducer: KafkaProducer[String, String] = NotificationProducer(topicName, bootstrapServers)
      val records = (1 to 10).map(currentNumber =>
        new ProducerRecord[String, String](topicName, currentNumber.toString, s"value_$currentNumber")
      )

      println("Step 2: Sending of ProducerRecords")

      records.foreach { record =>
        println(s"Record: ${record.value()} sent")
        notificationProducer.send(record)
      }
      notificationProducer.close()

      println("Completed producer operations ")

      println("Step 3: Ready to consume")

      implicit val stringDeserializer = new StringDeserializer

      withConsumer { consumer: KafkaConsumer[String, String] =>

        consumer.subscribe(util.Arrays.asList(topicName))

        val duration = Duration.ofMillis(10000)
        val records: ConsumerRecords[String, String] = consumer.poll(duration)

        records.forEach { record => println(s"Key: ${record.key}, Value: ${record.value}") }
      }
      println("Consumption complete")
    }

  }

}
