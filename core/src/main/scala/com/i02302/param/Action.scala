package com.i02302.param

sealed trait Action extends Param[Int]

object Action extends Param.Builder[Action, Int] {

  override val name: String = "Action"
  override val description: Option[String] = Some("Action")

  /**
    * 実行
    */
  case object Run extends Action {
    override val id = 1
    override val code: String = "run"
    override val description: Option[String] = Some("実行")
  }

  /**
    * チェック
    */
  case object Check extends Action {
    override val id = 2
    override val code: String = "check"
    override val description: Option[String] = Some("チェック")
  }

  override val code2param: Map[String, Action] = Map(
    Run.code -> Run,
    Check.code -> Check
  )

  override val id2param: Map[Int, Action] = Map(
    Run.id -> Run,
    Check.id -> Check
  )
}
