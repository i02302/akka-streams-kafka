package com.i02302.protocol

import com.i02302.param.{JobType, OptionKey, SystemType}

trait SystemRequest extends JsonMessage {

  val procTime: Long

  val system: SystemType

  val id: String

  val parentId: Option[String]

  val jobType: JobType

  val options: Map[OptionKey, Any]

  val operator: Int

  val retry: Int

  val retryLimit: Int

}
