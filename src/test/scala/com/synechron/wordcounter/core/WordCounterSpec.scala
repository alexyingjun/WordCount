package com.synechron.wordcounter.core

import com.synechron.wordcounter.cache.WordCountCache
import com.synechron.wordcounter.util.Translator
import org.mockito.MockitoSugar
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

import java.lang.Thread.sleep
import java.util.concurrent.CountDownLatch

class WordCounterSpec extends AnyFunSpec with Matchers with MockitoSugar{
  describe("Test Word Counter feature"){
    it("init WordCountCache"){
      WordCountCache.init()
    }
    it("should add valid words") {
      val wordCounter = new WordCounterImpl
      val words = Seq("abc", "bbc", "Abc", "bcb")
      wordCounter.add(words: _*)
      val words2 = Seq("aaa", "abc")
      wordCounter.add(words2: _*)
      wordCounter.getCountOfWord("abc") shouldBe 3
      wordCounter.getCountOfWord("BBC") shouldBe 1
      wordCounter.getCountOfWord("aaa") shouldBe 1
    }
    it("should add foreign words to the counter and filter illegal translated words") {
      val translator = mock[Translator]
      WordCountCache.setTranslator(translator)
      when(translator.translate("flower")).thenReturn("flower")
      when(translator.translate("flor")).thenReturn("flower")
      when(translator.translate("blume")).thenReturn("flower")
      when(translator.translate("bla")).thenReturn("a3")
      val wordCounter = new WordCounterImpl
      val wordCounter2 = new WordCounterImpl
      wordCounter.add(Seq("bla", "blume"): _*)
      wordCounter2.add(Seq("blume", "flor", "flower"): _*)

      wordCounter.getCountOfWord("flower") shouldBe 4
      wordCounter2.getCountOfWord("blume") shouldBe 4
    }
    it("should handle invalid words from translator outcome") {
      val translator = mock[Translator]
      WordCountCache.setTranslator(translator)
      when(translator.translate("bb")).thenReturn("")

      val wordCounter = new WordCounterImpl
      wordCounter.add("bb")
      wordCounter.getCountOfWord("bb") shouldBe 0
    }
    it("should handle exception from translator") {
      val translator = mock[Translator]
      when(translator.translate("aa")).thenThrow(new Exception)

      val wordCounter = new WordCounterImpl
      wordCounter.add("aa")
      wordCounter.getCountOfWord("aa") shouldBe 0
    }
    it("should filter out words with non-alphabetic chars"){
      val wordCounter = new WordCounterImpl
      WordCountCache.setTranslator(new Translator)
      val words = Seq("a1", "b ", "!c", "", " ", "4.785", null)
      wordCounter.add(words: _*)
      wordCounter.getCountOfWord("a1") shouldBe 0
      wordCounter.getCountOfWord("b ") shouldBe 0
      wordCounter.getCountOfWord("!c") shouldBe 0
      wordCounter.getCountOfWord("") shouldBe 0
      wordCounter.getCountOfWord("b") shouldBe 0
      wordCounter.getCountOfWord(" ") shouldBe 0
      wordCounter.getCountOfWord(" a") shouldBe 0
      wordCounter.getCountOfWord("4.785") shouldBe 0
      wordCounter.getCountOfWord(null) shouldBe 0
    }
    it("should finish operation quicker"){
      WordCountCache.setTranslator(new Translator)
      val wordCounter = new WordCounterImpl
      sleep(3000)

      val cl = new CountDownLatch(10)
      val t0 = System.nanoTime()
      for(_<-0 until 10) new Thread(new WordCounterRunner(cl)).start()
      val t1 = System.nanoTime()
      val elapsed = {
        (t1 - t0) / (1 * 1.0e6)
      }
      cl.await()
      println("Elapsed time: " + elapsed + "ms")
      println("Count of abc: " + wordCounter.directGet("abc"))
    }
    //Single thread feed
    //Elapsed time: 6044.292ms with 3 consumer threads and lock
    //Elapsed time: 3372.872299ms with 3 consumer thread but no lock
    //Elapsed time: 3308.5589ms with 10 ""    ""
    //Elapsed time: 751.8632ms without threads

    //Multi threads feed
    //Elapsed time: 6.984199ms with 10 feeds and no consumer threads
  }

  class WordCounterRunner(cl:CountDownLatch) extends Runnable{
    override def run(): Unit = {
      val wordCounter = new WordCounterImpl
      val words = Seq("abc", "bbc", "Abc", "bcb")
      for (_ <- 0 until 1000) wordCounter.add(words: _*)
      cl.countDown()
    }
  }
}
