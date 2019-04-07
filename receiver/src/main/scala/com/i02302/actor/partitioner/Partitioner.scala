package com.i02302.actor.partitioner

import java.util.UUID

import com.i02302.actor.Responder
import com.i02302.file.TextFile
import com.i02302.util.Closable
import akka.actor.Status.{Failure, Status, Success}
import com.i02302.file.line.LauncherRequestLine


import scala.concurrent._

trait Partitioner extends Responder[TextFile[LauncherRequestLine], TextFile[LauncherRequestLine]] with Closable {

  import system.dispatcher

  override def tearDown(input: TextFile[LauncherRequestLine], output: Either[Throwable, TextFile[LauncherRequestLine]]): Status =
    output match {
      case Right(dest) => Success(dest.moveTo(input)) // 出力ファイルと入力ファイルを入れ替えて返却
      case Left(exception: ExecutionException) => tearDown(input, Left(exception.getCause))
      case Left(exception) => Failure(exception)
    }

  protected[partitioner] def createDest: TextFile[LauncherRequestLine] =
    TextFile[LauncherRequestLine](s"${config.getString("dir.tmp")}/partitioner-$systemId-$uid-${UUID.randomUUID}.dat")

}
