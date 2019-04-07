package com.i02302.file.line

import com.i02302.protocol.{JsonMessage, LauncherRequest}

case class LauncherRequestLine(message: LauncherRequest) extends Line {

  override def toString: String = message.toJson

  override def isEmpty: Boolean = message.id.isEmpty

}

object LauncherRequestLine {

  implicit def LineConverter(line: String): LauncherRequestLine = LauncherRequestLine(message = JsonMessage.Builder.build[LauncherRequest](line))

}
