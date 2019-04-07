package com.i02302.protocol

case class Pong(message: String = "pong") extends JsonMessage

object Pong {

  def reply(ping: Ping) = ping.message match {
    case "ping" => Pong()
    case x => Pong(x)
  }

}
