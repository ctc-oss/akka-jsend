package com.ctc.jsend

import java.util.UUID

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCodes
import com.ctc.jsend.JSendSpec.{User, _}
import org.scalatest.{Matchers, WordSpec}
import spray.json._


class JSendSpec extends WordSpec with Matchers with DefaultJsonProtocol with SprayJsonSupport {
  val bob = User(1, "Bob", 40)
  val alice = User(2, "Alice", 33)
  val users = List(bob, alice)

  "JSend.success" should {
    "return list of users inside 'data', and 'status' = 'success'" in {
      JSend.success("users" -> users) shouldBe
        JsObject(Success, "data" → JsObject("users" → users.toJson))
    }

    "return JsObject with two keys, and 'status' = 'success'" in {
      JSend.success("user1" -> bob, "user2" -> alice) shouldBe
        JsObject(Success, "data" → Map("user1" → bob, "user2" → alice).toJson)
    }
  }

  "JSend.fail" should {
    "return list of users inside 'data', and 'status' = 'fail'" in {
      JSend.fail("users" -> List(User(1, "Bob", 40))) shouldBe
        JsObject(Failure, "data" → JsObject("users" → JsArray(bob.toJson)))
    }

    "return JsObject for 'data' with two keys, and 'status' = 'fail'" in {
      val (v1, v2) = (random, random)
      JSend.fail("a" -> v1, "b" -> v2) shouldBe
        JsObject(Failure, "data" → Map("a" → v1, "b" → v2).toJson)
    }
  }

  "JSend.error" should {
    "return 'status' = 'error' with a companion message, but contain no data" in {
      val msg = random
      JSend.error(msg) shouldBe
        JsObject(Errored, "message" → JsString(msg))
    }

    "contain an integer code" in {
      val msg = random
      val code = StatusCodes.InternalServerError
      JSend.error(msg, code) shouldBe
        JsObject(Errored, "message" → JsString(msg), "code" → JsNumber(code.intValue))
    }

    "return 'status' = 'error', error message, no integer code but contain some data" in {
      val msg = random
      val (v1, v2) = (random, random)
      JSend.error(msg, "v1" -> v1, "v2" -> v2) shouldBe
        JsObject(Errored, "message" → JsString(msg), "data" → Map("v1" → v1, "v2" → v2).toJson)
    }

    "all the above" in {
      val msg = random
      val (v1, v2) = (random, random)
      val code = StatusCodes.InternalServerError
      JSend.error(msg, code, "a" → v1, "b" → v2) shouldBe
        JsObject(
          Errored,
          "message" → JsString(msg),
          "code" → JsNumber(code.intValue),
          "data" → Map("a" → v1, "b" → v2).toJson
        )
    }
  }
}

object JSendSpec {
  def random = UUID.randomUUID.toString.take(7)

  def Success = "status" → JsString("success")
  def Failure = "status" → JsString("fail")
  def Errored = "status" → JsString("error")

  case class User(id: Long, name: String, age: Int)
  object User extends DefaultJsonProtocol {
    implicit val format: RootJsonFormat[User] = jsonFormat3(User.apply)
  }
}
