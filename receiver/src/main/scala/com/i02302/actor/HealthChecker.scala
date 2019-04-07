package com.i02302.actor

import com.i02302.protocol.{Ping, Pong}

import scala.concurrent.Future

class HealthChecker extends Responder[Ping, Pong] {

  override def run(input: Ping): Future[Pong] = Future.successful(Pong.reply(input))

}
