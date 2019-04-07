package com.i02302.param

sealed trait SystemType extends Param[Int]

object SystemType extends Param.Builder[SystemType, Int] {
  override val name: String = "SystemType"
  override val description: Option[String] = Some("システム区分")

  /**
    * Receiver
    */
  case object Receiver extends SystemType {
    override val id = 1
    override val code: String = "receiver"
    override val description: Option[String] = Some("Receiver")
  }

  /**
    * Launcher
    */
  case object Launcher extends SystemType {
    override val id = 2
    override val code: String = "launcher"
    override val description: Option[String] = Some("Launcher")
  }

  override val code2param: Map[String, SystemType] = Map(
    Receiver.code -> Receiver,
    Launcher.code -> Launcher
  )

  override val id2param: Map[Int, SystemType] = Map(
    Receiver.id -> Receiver,
    Launcher.id -> Launcher
  )
}
