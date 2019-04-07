package com.i02302.param

sealed trait JobStatus extends Param[Int]

object JobStatus extends Param.Builder[JobStatus, Int] {
  override val name: String = "JobStatus"
  override val description: Option[String] = Some("ジョブ状態")

  /**
    * 準備中
    */
  case object Pending extends JobStatus {
    override val id = 1
    override val code: String = "pending"
    override val description: Option[String] = Some("準備中")
  }

  /**
    * 実行中
    */
  case object Running extends JobStatus {
    override val id = 2
    override val code: String = "running"
    override val description: Option[String] = Some("実行中")
  }

  /**
    * 再試行
    */
  case object Retrying extends JobStatus {
    override val id = 3
    override val code: String = "retrying"
    override val description: Option[String] = Some("再試行")
  }

  /**
    * 成功
    */
  case object Success extends JobStatus {
    override val id = 4
    override val code: String = "success"
    override val description: Option[String] = Some("成功")
  }

  /**
    * 失敗
    */
  case object Failure extends JobStatus {
    override val id = 5
    override val code: String = "failure"
    override val description: Option[String] = Some("失敗")
  }

  override val code2param: Map[String, JobStatus] = Map(
    Pending.code -> Pending,
    Running.code -> Running,
    Retrying.code -> Retrying,
    Success.code -> Success,
    Failure.code -> Failure
  )

  override val id2param: Map[Int, JobStatus] = Map(
    Pending.id -> Pending,
    Running.id -> Running,
    Retrying.id -> Retrying,
    Success.id -> Success,
    Failure.id -> Failure
  )
}
