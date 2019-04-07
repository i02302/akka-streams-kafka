package com.i02302.param

sealed trait PartitionType extends Param[Int]

object PartitionType extends Param.Builder[PartitionType, Int] {

  override val name: String = "PartitionType"
  override val description: Option[String] = Some("分割種別")

  /**
    * 数分割
    */
  case object Number extends PartitionType {
    override val id = 1
    override val code: String = "number"
    override val description: Option[String] = Some("数でジョブを分割")
  }

  override val code2param: Map[String, PartitionType] = Map(
    Number.code -> Number
  )

  override val id2param: Map[Int, PartitionType] = Map(
    Number.id -> Number
  )

}
