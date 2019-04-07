package com.i02302.actor

import akka.pattern.ask
import akka.actor.Props
import com.i02302.actor.kafka.{KafkaConnInfo, Producer}
import com.i02302.param.JobStatus
import com.i02302.protocol.{ReceiverRequest, ReceiverResponse, ReceiverResult}

import scala.concurrent.Future

class JobRunner extends Responder[ReceiverRequest, ReceiverResponse] {

  import system.dispatcher

  lazy val producer = system.actorOf(Props(classOf[Producer], KafkaConnInfo.of(config)))

  override def run(input: ReceiverRequest): Future[ReceiverResponse] =
    (producer ? Seq(input)).map(_ => ReceiverResponse(request = input, result = ReceiverResult(status = JobStatus.Pending)))

}
