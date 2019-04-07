package com.i02302.protocol

trait SystemResponse extends JsonMessage {

  val request: SystemRequest

  val result: SystemResult

}
