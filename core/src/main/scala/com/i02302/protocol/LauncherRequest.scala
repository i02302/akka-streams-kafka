package com.i02302.protocol

import com.i02302.param.{JobType, OptionKey, SystemType}

case class LauncherRequest(system: SystemType = SystemType.Launcher,
                           procTime: Long = System.currentTimeMillis(),
                           parentId: Option[String],
                           id: String,
                           jobType: JobType,
                           options: Map[OptionKey, Any] = Map(),
                           retry: Int = 0,
                           retryLimit: Int = 0,
                           operator: Int) extends SystemRequest {

  def update(id: Option[Int] = None,
             jobType: Option[JobType] = None,
             options: Option[Map[OptionKey, Any]] = None,
             retry: Option[Int] = None,
             retryLimit: Option[Int] = None,
             operator: Option[Int] = None,
             procTime: Option[Long] = None): LauncherRequest = LauncherRequest(

    procTime = procTime.getOrElse(this.procTime),
    id = id.map(i => "%s-%08d".format(parentId.getOrElse("none"), i)).getOrElse(this.id),
    parentId = this.parentId,
    jobType = jobType.getOrElse(this.jobType),
    options = options.getOrElse(this.options),
    retry = retry.getOrElse(this.retry),
    retryLimit = retryLimit.getOrElse(this.retryLimit),
    operator = operator.getOrElse(this.operator)
  )

  def toRetry = update(retry = Some(this.retry + 1), procTime = Some(this.procTime))

}

object LauncherRequest {

  def of(n: Int, parent: ReceiverRequest, options: Map[OptionKey, Any] = Map()): LauncherRequest =
    LauncherRequest(
      procTime = parent.procTime,
      id = "%s-%08d".format(parent.id, n),
      parentId = Some(parent.id),
      jobType = parent.jobType,
      options = if (options.isEmpty) parent.options else options,
      retryLimit = parent.retryLimit,
      operator = parent.operator
    )

}
