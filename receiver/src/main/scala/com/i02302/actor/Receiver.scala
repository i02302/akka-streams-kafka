package com.i02302.actor

import akka.actor.Props
import akka.pattern.{AskTimeoutException, ask}
import akka.routing.RoundRobinPool
import com.i02302.actor.kafka.{KafkaConnInfo, Producer}
import com.i02302.actor.partitioner.{Library => PartitionLibrary}
import com.i02302.file.TextFile
import com.i02302.file.line.LauncherRequestLine
import com.i02302.param.{JobStatus, PartitionType}
import com.i02302.protocol.{LauncherRequest, ReceiverRequest, ReceiverResponse, ReceiverResult}
import com.i02302.util.Closable

import scala.annotation.tailrec
import scala.concurrent.{ExecutionContext, Future}

class Receiver extends Responder[ReceiverRequest, ReceiverResponse] with Closable {

  import context.dispatcher

  lazy val partitionLibrary = PartitionLibrary()
  lazy val producer = system.actorOf(RoundRobinPool(config.getInt("producer-pool")).props(Props(classOf[Producer], KafkaConnInfo.of(config))))

  override def run(input: ReceiverRequest): Future[ReceiverResponse] = {
    val start = System.currentTimeMillis()

    using(TextFile[LauncherRequestLine](filePath(input))) { file => // Receiver-ID名のファイル生成
      file.writer.open.println(LauncherRequestLine(message = LauncherRequest.of(n = 1, parent = input))).close
      partition(input.partitions, Future(file)) // 分割処理
    } flatMap { file =>
      file.reader.open
      produceMessage(file)
    } map { count =>
      val end = System.currentTimeMillis()
      val result = ReceiverResult(status = JobStatus.Success, count = Some(count), benchmark = Some(end - start), lapTime = Some(end - input.procTime))
      ReceiverResponse(request = input, result = result)
    }
  } recover {

  case t: AskTimeoutException =>

  if (input.retryLimit > input.retry) {

  // リトライ上限に達していない場合、リトライログ出力し、kafkaに再投入

  // Kafka 再投入
  producer ! Seq(input.toRetry)
  ReceiverResponse(request = input, result = ReceiverResult(status = JobStatus.Retrying, error = Some(t.getMessage)))

} else {

  // リトライ上限に達している場合、失敗ログ出力し、終了
  log.error(t, t.getMessage)
  ReceiverResponse(request = input, result = ReceiverResult(status = JobStatus.Failure, error = Some(t.getMessage)))

}

  case t: Throwable =>

  // 失敗ログ出力し、終了
  log.error(t, t.getMessage)
  ReceiverResponse(request = input, result = ReceiverResult(status = JobStatus.Failure, error = Some(t.getMessage)))

}

  private[this] def filePath(input: ReceiverRequest) = s"${config.getString("dir.tmp")}/${input.id}.dat"

  @tailrec
  private[this] def produceMessage(file: TextFile[LauncherRequestLine], bundleCount: Int = 1000, sum: Future[Int] = Future(0)): Future[Int] = {
    import com.i02302.pimp.IteratorManager._
    log.info("fugafuga")
    val lines = file.reader.next(bundleCount)
    if (lines.isEmpty) {
      sum
    } else {
      produceMessage(file, bundleCount = bundleCount, sum map { s =>
        producer ? (lines map { line =>
          log.info(line.message.toJson)
          line.message
        })
        s + lines.size
      })
    }
  }

  @tailrec
  private[this] def partition(partitions: Seq[PartitionType], partitioned: Future[TextFile[LauncherRequestLine]]): Future[TextFile[LauncherRequestLine]] =

    if (partitions.isEmpty) {

      partitioned

    } else {

      // 指定された名前からPartitioner取得
      val partitioner = partitionLibrary.index(partitions.head)

      // 分割処理
      partition(partitions.drop(1), partitioned flatMap { file => (partitioner ? file).mapTo[TextFile[LauncherRequestLine]] })

    }

}
