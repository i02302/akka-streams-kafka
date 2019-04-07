package com.i02302.actor

import akka.actor.Status.{Failure, Status, Success}
import akka.actor.{Actor, ActorLogging}
import akka.util.Timeout
import com.typesafe.config.ConfigFactory

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._
import scala.concurrent.duration._

trait Behavior[I <: Any, O <: Any] extends Actor with ActorLogging {

  protected lazy val config = ConfigFactory.load.getConfig(context.system.name)
  protected lazy val systemId = context.self.path.address.system
  protected lazy val uid = context.self.path.toSerializationFormat.split("#")(1)

  implicit lazy val system = context.system
  implicit lazy val timeout = Timeout(config.getInt("timeout") millisecond)

  def exec(input: I): Future[Status] =
    setUp(input).flatMap(run).map(x => tearDown(input, Right(x))) recover { case x => tearDown(input, Left(x)) }

  def setUp(input: I): Future[I] = Future(input)

  def run(input: I): Future[O]

  def tearDown(input: I, output: Either[Throwable, O]): Status =
    output match {

      case Right(messages) =>
        Success(messages)

      case Left(exception: ExecutionException) =>
        tearDown(input, Left(exception.getCause))

      case Left(exception) =>
        log.error(exception, exception.getMessage)
        Failure(exception)

    }

}
