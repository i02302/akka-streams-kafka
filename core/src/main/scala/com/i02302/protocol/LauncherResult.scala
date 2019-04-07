package com.i02302.protocol

import com.i02302.param.{JobStatus, SystemType}

case class LauncherResult(system: SystemType = SystemType.Launcher,
                          status: JobStatus,
                          result: Option[Any] = None,
                          error: Option[String] = None,
                          benchmark: Option[Long] = None,
                          lapTime: Option[Long] = None) extends SystemResult
