package com.synechron.wordcounter.controller


import io.undertow.Undertow
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class RestApiControllerSpec extends AnyFunSpec with Matchers {
  def withServer[T](f: String => T): T = {
    val server = Undertow.builder
      .addHttpListener(8080, "localhost")
      .setHandler(RestApiController.defaultHandler)
      .build
    server.start()
    val res =
      try f("http://localhost:8080")
      finally server.stop()
    res
  }
  describe("Word Counter rest api") {
    it("should be able to add words") {
      withServer { host =>
        val res = requests.get(host)
        res.text() shouldBe "Welcome to word counter service"

        val addRes = requests.post(s"$host/add", data = """ { "words": ["aA", "ab","a a"] }""")
        addRes.statusCode shouldBe 200

        val getRes = requests.get(s"$host/count/aa")
        getRes.text() shouldBe "1"
      }
    }
  }
}
