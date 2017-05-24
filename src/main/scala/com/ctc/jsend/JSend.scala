package com.ctc.jsend

import akka.http.scaladsl.model.StatusCode
import spray.json.{JsNumber, JsObject, JsString, JsValue, JsonWriter}


object JSend {
  def success[T](objs: (String, T)*)(implicit tjs: JsonWriter[T]): JsValue =
    statusToJson("success") ++ seqToJson(objs)

  def fail[T](objs: (String, T)*)(implicit tjs: JsonWriter[T]): JsValue =
    statusToJson("fail") ++ seqToJson(objs)

  def error(message: String): JsValue =
    errorAux(message)

  def error(message: String, code: StatusCode): JsValue =
    errorAux(message) ++ JsObject("code" → JsNumber(code.intValue))

  def error[T](message: String, objs: (String, T)*)(implicit tjs: JsonWriter[T]): JsValue =
    errorAux(message) ++ seqToJson(objs)

  def error[T](message: String, code: StatusCode, objs: (String, T)*)(implicit tjs: JsonWriter[T]): JsValue =
    errorAux(message) ++ JsObject("code" → JsNumber(code.intValue)) ++ seqToJson(objs)


  /*
   *
   * Internals
   *
   */

  private def statusToJson(status: String): JsObject = JsObject("status" → JsString(status))

  private def seqToJson[T](objs: Seq[(String, T)])(implicit tjs: JsonWriter[T]): JsObject = {
    import spray.json._

    JsObject("data" →
      objs.map { o => JsObject(o._1 → o._2.toJson) }
      .foldLeft(JsObject()) {
        (a, b) ⇒ a ++ JsObject(b.fields.keys.head → b.fields.values.head)
      })
  }

  private def errorAux(message: String): JsObject =
    statusToJson("error") ++ JsObject("message" → JsString(message))

  private implicit class MergableJsObject(o: JsObject) {
    def ++(other: JsObject) = JsObject(o.fields ++ other.fields)
  }
}
