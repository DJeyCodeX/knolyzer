package com.knoldus

object LogProducer {
  import java.util.Properties

  import org.apache.kafka.clients.producer._

  val  props = new Properties()
  props.put("bootstrap.servers", "localhost:9092")

  props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")

  val producer = new KafkaProducer[String, String](props)

  def publishLog(topic : String , logEntry : LogEntryNew) = {

    println("""###############################################""")
    producer.send(new ProducerRecord(topic, logEntry.ip, logEntry.req))
  }

}
