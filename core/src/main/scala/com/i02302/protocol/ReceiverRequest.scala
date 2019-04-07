package com.i02302.protocol

import java.util.UUID

import com.i02302.param.{JobType, OptionKey, PartitionType, SystemType}

case class ReceiverRequest(system: SystemType = SystemType.Receiver,
                           procTime: Long = System.currentTimeMillis(),
                           parentId: Option[String] = None,
                           id: String = UUID.randomUUID.toString,
                           jobType: JobType,
                           options: Map[OptionKey, Any] = Map(),
                           partitions: Seq[PartitionType] = Seq(),
                           retry: Int = 0,
                           retryLimit: Int = 0,
                           operator: Int) extends SystemRequest {

  def update(id: Option[Int] = None,
             jobType: Option[JobType] = None,
             options: Option[Map[OptionKey, Any]] = None,
             partitions: Option[Seq[PartitionType]] = None,
             retry: Option[Int] = None,
             retryLimit: Option[Int] = None,
             operator: Option[Int] = None,
             procTime: Option[Long] = None): ReceiverRequest = ReceiverRequest(

    procTime = procTime.getOrElse(System.currentTimeMillis()),
    id = id.map(i => "%s-%08d".format(parentId.getOrElse("none"), i)).getOrElse(this.id),
    parentId = this.parentId,
    jobType = jobType.getOrElse(this.jobType),
    options = options.getOrElse(this.options),
    partitions = partitions.getOrElse(this.partitions),
    retry = retry.getOrElse(this.retry),
    retryLimit = retryLimit.getOrElse(this.retryLimit),
    operator = operator.getOrElse(this.operator)
  )

  def toRetry = update(retry = Some(this.retry + 1), procTime = Some(this.procTime))

}
