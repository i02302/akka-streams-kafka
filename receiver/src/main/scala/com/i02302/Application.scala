package com.i02302

import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.kafka.scaladsl.Consumer.DrainingControl
import akka.kafka.scaladsl.{Committer, Consumer}
import akka.kafka.{CommitterSettings, ConsumerSettings, Subscriptions}
import akka.pattern.ask
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Keep, Sink}
import akka.util.Timeout
import com.i02302.actor.Receiver
import com.i02302.actor.kafka.KafkaConnInfo
import com.i02302.log.Logging
import com.i02302.param.SystemType
import com.i02302.protocol.{JsonMessage, ReceiverRequest, ReceiverResponse}
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.util.control.Exception._

object Application extends App with Service with Logging {

  val systemType = SystemType.Receiver

  override implicit val system = ActorSystem(systemType.code)
  override implicit val executor = system.dispatcher
  override implicit val materializer = ActorMaterializer()

  private[this] val config = AppConf.getConfig(systemType.code)

  override implicit val timeout = Timeout(config.getInt("timeout") millisecond)

  val kafkaConnInfo = KafkaConnInfo.of(config)

  val consumerSettings =
    ConsumerSettings(system.settings.config.getConfig("akka.kafka.consumer"), new StringDeserializer, new StringDeserializer)
      .withBootstrapServers(kafkaConnInfo.consumerBrokers)
      .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, kafkaConnInfo.consumerAutoOffsetRest)
      .withProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, kafkaConnInfo.consumerEnableAutoCommit)
      .withProperty(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, kafkaConnInfo.consumerAutoOffsetIntervalMs)
      .withGroupId(kafkaConnInfo.consumerGroupId)

  val committerSettings = CommitterSettings.create(system.settings.config.getConfig("akka.kafka.committer"))

  val receiver = system.actorOf(Props[Receiver])

  val control =
    Consumer
      .committableSource(consumerSettings, Subscriptions.topics(systemType.code))
      .mapAsync(1) { msg =>
        business(msg.record.key, msg.record.value).map(_ => msg.committableOffset)
      }
      .via(Committer.flow(committerSettings.withMaxBatch(1)))
      .toMat(Sink.seq)(Keep.both)
      .mapMaterializedValue(DrainingControl.apply)
      .run()

  def business(key: String, value: String): Future[ReceiverResponse] =
    (receiver ? JsonMessage.Builder.build[ReceiverRequest](value)).mapTo[ReceiverResponse]

  allCatch withApply { t =>

    log.error(s"${t.getMessage}", t)
    control.shutdown()
    system.terminate()
    sys.exit(1)

  } apply {

    val host = config.getString("host")
    val port = config.getInt("port")
    log.info(s"service active (host = $host, port = $port)")
    Http().bindAndHandle(routes, host, port)
    log.info("receiver consume start.")

  }

}
