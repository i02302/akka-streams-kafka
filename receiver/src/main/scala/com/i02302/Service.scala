package com.i02302

import akka.actor.ActorSystem
import akka.http.scaladsl.model.ContentTypes._
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._
import akka.pattern.ask
import akka.stream.ActorMaterializer
import akka.util.Timeout
import com.i02302.actor.Library
import com.i02302.config.ApplicationConfig
import com.i02302.log.Logging
import com.i02302.param.{Action, Category}
import com.i02302.protocol._

import scala.concurrent.ExecutionContextExecutor
import scala.language.postfixOps
import scala.util.control.Exception._
import scala.util.{Failure, Success, Try}

trait Service extends ApplicationConfig with Logging {

  implicit def system: ActorSystem

  implicit def executor: ExecutionContextExecutor

  implicit def materializer: ActorMaterializer

  implicit def timeout: Timeout

  private[this] lazy val library = Library()

  protected[this] val routes = {
    flow[Ping, Pong](Action.Check, Category.Health, get) ~
      flow[ReceiverRequest, ReceiverResponse](Action.Run, Category.Job, post)
  }

  private[this] def flow[I <: JsonMessage, O <: JsonMessage](action: Action, category: Category, method: Directive0 = post)(implicit mfi: Manifest[I], mfo: Manifest[O]) =
    (uri(action, category) & method) {
      entity(as[String]) { request =>
        onComplete {
          (library.actorOf(action, category) ? (allCatch opt JsonMessage.Builder.build[I](request)).getOrElse(request)).mapTo[O]
        } {
          response
        }
      }
    }

  private[this] def uri(action: Action, category: Category) = path(s"$action" / s"$category")

  private[this] def response(result: Try[Any]): Route = result match {

    case Success(response: JsonMessage) =>
      log.info(response.toJson)
      complete(StatusCodes.OK, HttpEntity(contentType = `application/json`, string = response.toJson))

    case Success(x) =>
      complete(StatusCodes.OK, "OK")

    case Failure(e: Throwable) =>
      log.error(e.getMessage, e)
      complete(StatusCodes.InternalServerError, e.getMessage)

  }

}
