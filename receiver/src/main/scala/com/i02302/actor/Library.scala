package com.i02302.actor

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.routing.RoundRobinPool
import com.i02302.exception.SystemException
import com.i02302.log.Logging
import com.i02302.param.Action.{Check, Run}
import com.i02302.param.Category.{Health, Job}
import com.i02302.param.{Action, Category}
import com.typesafe.config.ConfigFactory

case class Library(implicit system: ActorSystem) extends Logging {

  def actorOf(action: Action, category: Category): ActorRef = {
    index(category).get(action) match {
      case Some(actor: ActorRef) => actor
      case _ => throw new SystemException(s"routes is not found. (action = $action, category = $category)")
    }
  }

  private[this] val pool = ConfigFactory.load.getConfig(system.name).getInt("pool")
  private[this] val index: Map[Category, Map[Action, ActorRef]] = Map(
    Job -> Map(
      Run -> system.actorOf(RoundRobinPool(pool).props(Props[JobRunner]))
    ),
    Health -> Map(
      Check -> system.actorOf(RoundRobinPool(pool).props(Props[HealthChecker]))
    )
  )

}
