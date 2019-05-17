package com.jimmy.personal

import org.apache.kafka.clients.producer.{Producer,KafkaProducer}
import java.util.Properties

case class Notification(sender: String, message: String)

object NotificationProducer {
  def apply[K,V] (topicName: String, bootstrapServers: String): KafkaProducer[K,V]= {

    val properties = new Properties()
    properties.put("bootstrap.servers", bootstrapServers)
    properties.put("acks", "all")
    properties.put("retries", 0.toString)
    properties.put("batch.size", 16384.toString)
    properties.put("linger.ms", 1.toString)
    properties.put("buffer.memory", 33554432.toString)
    properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")

    new KafkaProducer[K,V](properties)
  }

}
