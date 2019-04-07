package com.i02302.param

sealed trait JobType extends Param[Int]

object JobType extends Param.Builder[JobType, Int] {
  override val name: String = "JobType"
  override val description: Option[String] = Some("ジョブ種別")

  /**
    * テスト
    */
  case object Test extends JobType {
    override val id = 1
    override val code: String = "test"
    override val description: Option[String] = Some("テスト")
  }

  override val code2param: Map[String, JobType] = Map(
    Test.code -> Test
  )

  override val id2param: Map[Int, JobType] = Map(
    Test.id -> Test
  )
}
