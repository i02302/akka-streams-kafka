package com.i02302.actor

import scala.util.{Failure, Success}

trait Passer[I <: Any, O <: Any] extends Behavior[I, O] {

  import context.dispatcher

  def receive = {

    case input: I => exec(input) onComplete {
      case Success(_) =>
      case Failure(error) => log.error(error, error.getMessage)
    }

    case x => log.warning(s"Undefined input: $x")

  }

}
