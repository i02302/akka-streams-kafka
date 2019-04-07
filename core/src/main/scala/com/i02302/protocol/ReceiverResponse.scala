package com.i02302.protocol

case class ReceiverResponse(request: ReceiverRequest, result: ReceiverResult) extends SystemResponse
