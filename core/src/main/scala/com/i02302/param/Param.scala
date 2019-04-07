package com.i02302.param

import com.i02302.exception.SystemException
import org.json4s
import org.json4s.{CustomKeySerializer, CustomSerializer, JsonFormat}
import org.json4s.JsonAST.{JBool, JInt, JString}

trait Param[T <: Any] extends AnyRef with Serializable with Product {
  val id: T
  val code: String
  val description: Option[String]
  val outputId: Boolean = false

  override def toString = code
}

object Param {

  trait Builder[P <: Param[T], T <: Any] {

    val name: String

    val description: Option[String]

    val code2param: Map[String, P]

    val id2param: Map[T, P]

    def of(id: Any): Option[P] =
      id match {
        case param: P => Some(param)
        case code: String => code2param.get(code)
        case id: T => id2param.get(id)
        case x => None
      }

    case class Serializer()(implicit m: Manifest[P]) extends CustomSerializer[P](format => ( {
      case JString(code: String) => of(code).get
      case JBool(id: Boolean) => of(id).get
      case JInt(id: BigInt) => of(id.toInt).get
      case x => throw InvalidParamException(code = s"$x")
    }, {
      case obj: P =>
        if (obj.outputId) {
          obj.id match {
            case x: String => JString(x)
            case x: Int => JInt(x)
            case x: Boolean => JBool(x)
            case _ => JString(obj.code)
          }
        } else {
          JString(obj.code)
        }
    }))

    case class KeySerializer()(implicit m: Manifest[P]) extends CustomKeySerializer[P](format => ( {
      case code: String => of(code).getOrElse(throw InvalidParamException(code = code))
      case x => throw InvalidParamException(code = s"$x")
    }, {
      case x: P => x.code
    }))

    implicit val format = new JsonFormat[Option[P]] {
      override def write(obj: Option[P]): JString = obj match {
        case Some(p) => p.id match {
          case x: String => JString(x)
          case _ => JString(p.code)
        }
        case _ => JString("")
      }

      override def read(value: json4s.JValue): Option[P] = value match {
        case JString(x: String) => of(x)
        case JBool(x: Boolean) => of(x)
        case JInt(x: BigInt) => of(x.toInt)
        case _ => None
      }
    }

    case class InvalidParamException(code: String = "Unknown", message: String = "This code doesn't exist in the parameter . (code = %s)") extends SystemException(message.format(code))

  }

}
