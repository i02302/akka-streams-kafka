package com.i02302.protocol

import java.io.{ByteArrayOutputStream, ObjectOutputStream}
import java.nio.ByteBuffer

import com.i02302.log.Logging
import com.i02302.param.JsonSerializer
import com.i02302.param.JsonSerializer._
import org.json4s._
import com.i02302.util.Closable
import org.json4s.native.{JsonMethods, Serialization}

import scala.util.control.Exception.allCatch

trait JsonMessage {

  lazy val toJson = Serialization.write(this)

  override def toString: String = toJson

}

object JsonMessage extends Logging {

  implicit val formats = JsonSerializer.formats

  object Builder extends Closable {

    def getByte[T <: JsonMessage](message: T): Array[Byte] =
      using(new ByteArrayOutputStream(1024)) { baos =>
        using(new ObjectOutputStream(baos)) { oos =>
          oos.writeObject(message.toJson)
          baos.toByteArray
        }
      }

    def build[T <: JsonMessage](json: String)(implicit mf: Manifest[T]): T =
      allCatch withApply { t =>
        log.error(s"invalid json message. (json = $json)", t)
        throw t
      } apply {
        JsonMethods.parse(json).extract[T]
      }

    def build[T <: JsonMessage](bytes: ByteBuffer)(implicit mf: Manifest[T]): T = build[T](new String(bytes.array))

  }

}
