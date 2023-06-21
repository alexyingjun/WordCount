package com.synechron.wordcounter.core

import com.synechron.wordcounter.util.Translator
import org.mockito.MockitoSugar
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class WordCounterSpec extends AnyFunSpec with Matchers with MockitoSugar{
  describe("Test Word Counter feature"){
    it("should add valid words"){
      val wordCounter = new WordCounterImpl(new Translator)
      val words = Seq("abc","bbc","Abc","bcb")
      wordCounter.addWords(words: _*)
      wordCounter.addWords("aaa")
      wordCounter.getCountOfWord("abc") shouldBe 2
      wordCounter.getCountOfWord("BBC") shouldBe 1
      wordCounter.getCountOfWord("aaa") shouldBe 1
    }
    it("should add foreign words to the counter"){
      val translator = mock[Translator]
      when(translator.translate("flower")).thenReturn("flower")
      when(translator.translate("flor")).thenReturn("flower")
      when(translator.translate("blume")).thenReturn("flower")

      val wordCounter = new WordCounterImpl(translator)
      wordCounter.addWords(Seq("flower", "flor", "blume"): _*)
      wordCounter.getCountOfWord("flower") shouldBe 3
      wordCounter.getCountOfWord("blume") shouldBe 3
    }
    it("should handle invalid words from translator outcome") {
      val translator = mock[Translator]
      when(translator.translate("aa")).thenReturn("")

      val wordCounter = new WordCounterImpl(translator)
      wordCounter.addWords("aa")
      wordCounter.getCountOfWord("aa") shouldBe 0
    }
    it("should handle exception from translator") {
      val translator = mock[Translator]
      when(translator.translate("aa")).thenThrow(new Exception)

      val wordCounter = new WordCounterImpl(translator)
      wordCounter.addWords("aa")
      wordCounter.getCountOfWord("aa") shouldBe 0
    }
    it("should filter out words with non-alphabetic chars"){
      val wordCounter = new WordCounterImpl(new Translator)
      val words = Seq("a1", "b ", "!c", "", " ", "4.785", null)
      wordCounter.addWords(words: _*)
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
  }
}
