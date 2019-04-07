package com.i02302.protocol

import com.i02302.param.{JobStatus, SystemType}

trait SystemResult extends JsonMessage {

  val system: SystemType

  val status: JobStatus

  val error: Option[String]

  val benchmark: Option[Long]

}
