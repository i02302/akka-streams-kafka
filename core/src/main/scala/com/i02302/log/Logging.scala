package com.i02302.log

import com.typesafe.scalalogging.Logger
import org.slf4j.LoggerFactory

trait Logging {
  val log = Logger(LoggerFactory.getLogger(this.getClass))
}
