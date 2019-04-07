package com.i02302.actor

import akka.actor.Status.Failure
import akka.pattern.pipe

import scala.concurrent.ExecutionContext.Implicits.global

trait Responder[I <: Any, O <: Any] extends Behavior[I, O] {

  def receive = {

    case input: I => exec(input) pipeTo sender

    case _ => sender ! Failure(new IllegalArgumentException)

  }

}
