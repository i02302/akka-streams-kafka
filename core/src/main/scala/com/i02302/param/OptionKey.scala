package com.i02302.param

sealed trait OptionKey extends Param[Int]

object OptionKey extends Param.Builder[OptionKey, Int] {

  override val name: String = "OptionKey"
  override val description: Option[String] = Some("オプションキー")

  /**
    * 分割数
    */
  case object PartitionNumber extends OptionKey {
    override val id = 1
    override val code: String = "partition-number"
    override val description: Option[String] = Some("分割数")
  }

  /**
    * エラー
    */
  case object Error extends OptionKey {
    override val id = 2
    override val code: String = "error"
    override val description: Option[String] = Some("指定された場合エラーを発生させる(テスト用オプション)")
  }

  override val code2param: Map[String, OptionKey] = Map(
    PartitionNumber.code -> PartitionNumber,
    Error.code -> Error
  )

  override val id2param: Map[Int, OptionKey] = Map(
    PartitionNumber.id -> PartitionNumber,
    Error.id -> Error
  )

}
