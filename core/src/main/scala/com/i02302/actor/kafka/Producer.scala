package com.i02302.actor.kafka

import com.i02302.actor.Responder
import com.i02302.protocol.SystemRequest
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerConfig, ProducerRecord}
import org.apache.kafka.common.serialization.StringSerializer

import scala.collection.JavaConverters._
import scala.concurrent._

class Producer(kafkaConnInfo: KafkaConnInfo) extends Responder[Seq[SystemRequest], Int] {

  import system.dispatcher

  lazy val configs = Map[String, AnyRef](ProducerConfig.BOOTSTRAP_SERVERS_CONFIG -> kafkaConnInfo.producerBrokers)
  lazy val producer = new KafkaProducer[String, String](configs.asJava, new StringSerializer, new StringSerializer)

  override def run(input: Seq[SystemRequest]): Future[Int] = Future {
    input
      .map(request => new ProducerRecord[String, String](request.system.code, request.id, request.toJson))
      .map(producer.send)
      .count(_.isDone)
  }

}
