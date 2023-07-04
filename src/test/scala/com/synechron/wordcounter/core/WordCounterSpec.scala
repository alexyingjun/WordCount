package com.synechron.wordcounter.core

import com.synechron.wordcounter.core.cache.WordCountCache
import com.synechron.wordcounter.core.flow.WordCounterFlow
import com.synechron.wordcounter.util.Translator
import org.mockito.MockitoSugar
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

import java.util.concurrent.CountDownLatch

class WordCounterSpec extends AnyFunSpec with Matchers with MockitoSugar{
  describe("Test Word Counter feature"){
    val processFlow = new WordCounterFlow()
    val wordCounter = WordCounterImpl(processFlow)
    val wordCounter2 = WordCounterImpl(processFlow)
    val wordCountCache = new WordCountCache
    it("should add valid words") {
      val words = Seq("abc", "bbc", "Abc", "bcb")
      wordCounter.add(words: _*)
      val words2 = Seq("zZz", "abc")
      wordCounter.add(words2: _*)
      wordCounter.get("abc") shouldBe 3
      wordCounter.get("BBC") shouldBe 1
      wordCounter.get("zzz") shouldBe 1
    }
    it("should add foreign words to the counter and filter illegal translated words") {
      val translator = mock[Translator]
      wordCountCache.setTranslator(translator)
      when(translator.translate("flower")).thenReturn("flower")
      when(translator.translate("flor")).thenReturn("flower")
      when(translator.translate("blume")).thenReturn("flower")
      when(translator.translate("bla")).thenReturn("a3")
      wordCounter.add(Seq("bla", "blume"): _*)
      wordCounter2.add(Seq("blume", "flor", "flower"): _*)

      wordCounter.get("flower") shouldBe 4
      wordCounter2.get("blume") shouldBe 4
    }
    it("should handle invalid words from translator outcome") {
      val translator = mock[Translator]
      wordCountCache.setTranslator(translator)
      when(translator.translate("bb")).thenReturn("")

      wordCounter.add("bb")
      wordCounter.get("bb") shouldBe 0
    }
    it("should handle exception from translator") {
      val translator = mock[Translator]
      wordCountCache.setTranslator(translator)
      when(translator.translate("aa")).thenThrow(new Exception)

      wordCounter.add("aa")
      wordCounter.get("aa") shouldBe 0
    }
    it("should filter out words with non-alphabetic chars"){
      val words = Seq("a1", "b ", "!c", "", " ", "4.785", null)
      wordCounter.add(words: _*)
      wordCounter.get("a1") shouldBe 0
      wordCounter.get("b ") shouldBe 0
      wordCounter.get("!c") shouldBe 0
      wordCounter.get("") shouldBe 0
      wordCounter.get("b") shouldBe 0
      wordCounter.get(" ") shouldBe 0
      wordCounter.get(" a") shouldBe 0
      wordCounter.get("4.785") shouldBe 0
      wordCounter.get(null) shouldBe 0
    }
    it("should count words correctly in multi-threading situation"){
      wordCountCache.setTranslator(new Translator)
      val cl = new CountDownLatch(10)
      for(_<-0 until 10) new Thread(new WordCounterRunner(cl)).start()
      cl.await()
      wordCounter.get("aaa") shouldBe 20000
    }
    class WordCounterRunner(cl: CountDownLatch) extends Runnable {
      override def run(): Unit = {
        val wordCounter = WordCounterImpl(processFlow)
        val words = Seq("aaA", "BBB", "Ccc", "AAA")
        for (_ <- 0 until 1000) wordCounter.add(words: _*)
        cl.countDown()
      }
    }
  }
}
