package com.i02302.actor.partitioner

import com.i02302.file.TextFile
import com.i02302.file.line.LauncherRequestLine
import com.i02302.param.OptionKey

import scala.concurrent.{ExecutionContext, Future}

class NumberPartitioner extends Partitioner {

  import system.dispatcher

  override def run(input: TextFile[LauncherRequestLine]): Future[TextFile[LauncherRequestLine]] = {
    val dest = createDest
    dest.writer.open
    Future.sequence(
      for {
        line <- input.reader.open
        id <- 1 to line.message.options(OptionKey.PartitionNumber).asInstanceOf[Number].intValue()
      } yield Future(dest.writer.println(LauncherRequestLine(message = line.message.update(id = Some(id)))))
    ) map { x => dest }

  }

}
