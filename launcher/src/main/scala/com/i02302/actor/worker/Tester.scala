package com.i02302.actor.worker
import com.i02302.param.OptionKey
import com.i02302.protocol.LauncherRequest

import scala.concurrent.{ExecutionContext, Future}

class Tester extends Worker[String] {

  import context.dispatcher

  override def run(input: LauncherRequest): Future[String] = Future {
    input.options.get(OptionKey.Error) match {
      case Some(x) => throw new RuntimeException("異常系")
      case _ => "正常系"
    }
  }
}
