package com.i02302.actor.worker

import akka.actor.{ActorContext, ActorRef, Props}
import akka.routing.RoundRobinPool
import com.i02302.param.JobType
import com.typesafe.config.ConfigFactory

case class Library(implicit val context: ActorContext) {

  private val pool = ConfigFactory.load.getConfig(context.system.name).getInt("pool")

  val index: Map[JobType, ActorRef] = Map(

    JobType.Test -> context.actorOf(RoundRobinPool(pool).props(Props[Tester]))

  )

}
