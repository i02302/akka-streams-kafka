package com.i02302.actor.kafka

import akka.actor.ActorSystem
import com.typesafe.config.Config

case class KafkaConnInfo(consumerBrokers: String,
                         producerBrokers: String,
                         consumerAutoOffsetRest: String,
                         consumerEnableAutoCommit: String,
                         consumerAutoOffsetIntervalMs: String,
                         consumerGroupId: String)

object KafkaConnInfo {

  def of(config: Config)(implicit system: ActorSystem): KafkaConnInfo =
    KafkaConnInfo(
      consumerBrokers = config.getString("kafka.consumer.brokers"),
      producerBrokers = config.getString("kafka.producer.brokers"),
      consumerAutoOffsetRest = config.getString("kafka.consumer.auto-offset-reset"),
      consumerEnableAutoCommit = config.getString("kafka.consumer.enable-auto-commit"),
      consumerAutoOffsetIntervalMs = config.getString("kafka.consumer.auto-offset-interval-ms"),
      consumerGroupId = config.getString("kafka.consumer.group-id")
    )

}
