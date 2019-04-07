package com.i02302.actor.partitioner

import akka.actor.{ActorContext, ActorRef, Props}
import akka.routing.RoundRobinPool
import com.i02302.param.PartitionType
import com.typesafe.config.ConfigFactory

case class Library(implicit val context: ActorContext) {

  private val pool = ConfigFactory.load.getConfig(context.system.name).getInt("partitioner-pool")

  val index: Map[PartitionType, ActorRef] = Map(

    PartitionType.Number -> context.actorOf(RoundRobinPool(pool).props(Props[NumberPartitioner]))

  )

}
