package com.i02302.param

sealed trait Category extends Param[Int]

object Category extends Param.Builder[Category, Int] {

  override val name: String = "Category"
  override val description: Option[String] = Some("Category")

  /**
    * ジョブ
    */
  case object Job extends Category {
    override val id = 1
    override val code: String = "job"
    override val description: Option[String] = Some("ジョブ")
  }

  /**
    * ヘルス
    */
  case object Health extends Category {
    override val id = 2
    override val code: String = "health"
    override val description: Option[String] = Some("ヘルス")
  }

  override val code2param: Map[String, Category] = Map(
    Job.code -> Job,
    Health.code -> Health
  )

  override val id2param: Map[Int, Category] = Map(
    Job.id -> Job,
    Health.id -> Health
  )
}
