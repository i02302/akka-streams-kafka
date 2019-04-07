package com.i02302.exception

class SystemException(message: String, cause: Option[Throwable] = None) extends RuntimeException(message, cause.orNull)
