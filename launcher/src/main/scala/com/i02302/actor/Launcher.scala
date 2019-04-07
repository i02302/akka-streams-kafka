package com.i02302.actor

import akka.actor.Props
import akka.pattern.{AskTimeoutException, ask}
import com.i02302.actor.kafka.{KafkaConnInfo, Producer}
import com.i02302.actor.worker.Library
import com.i02302.param.JobStatus
import com.i02302.protocol.{LauncherRequest, LauncherResponse, LauncherResult}

import scala.concurrent.Future

class Launcher extends Responder[LauncherRequest, LauncherResponse] {

  import context.dispatcher

  lazy val library = Library()
  lazy val producer = system.actorOf(Props(classOf[Producer], KafkaConnInfo.of(config)))

  override def run(input: LauncherRequest): Future[LauncherResponse] = {
    val start = System.currentTimeMillis()

    // 実行ログ出力
    log.info(input.toJson)
    (library.index(input.jobType) ? input) map { answer =>

      val end = System.currentTimeMillis()
      val result = LauncherResult(status = JobStatus.Success, result = Some(answer), benchmark = Some(end - start), lapTime = Some(end - input.procTime))

      // 成功ログ出力
      val response = LauncherResponse(request = input, result = result)
      log.info(response.toJson)
      response

    } recoverWith {

      case t: AskTimeoutException =>

        if (input.retryLimit > input.retry) {

          // リトライ上限に達していない場合、リトライログ出力
          val response = LauncherResponse(request = input, result = LauncherResult(status = JobStatus.Retrying, error = Some(t.getMessage)))
          log.info(response.toJson)
          // Kafka 再投入
          (producer ? Seq(input.toRetry)).map {
            _ => response
          }

        } else {

          // リトライ上限に達している場合、失敗ログ出力
          log.error(t, t.getMessage)
          Future(LauncherResponse(request = input, result = LauncherResult(status = JobStatus.Failure, error = Some(t.getMessage))))

        }

      case t: Throwable =>

        // 失敗ログ出力し、終了
        log.error(t, t.getMessage)
        Future(LauncherResponse(request = input, result = LauncherResult(status = JobStatus.Failure, error = Some(t.getMessage))))

    }

  }

}
