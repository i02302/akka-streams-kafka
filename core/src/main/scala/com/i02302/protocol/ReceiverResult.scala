package com.i02302.protocol

import com.i02302.param.{JobStatus, SystemType}

case class ReceiverResult(system: SystemType = SystemType.Receiver,
                          status: JobStatus,
                          count: Option[Int] = None,
                          error: Option[String] = None,
                          benchmark: Option[Long] = None,
                          lapTime: Option[Long] = None) extends SystemResult
