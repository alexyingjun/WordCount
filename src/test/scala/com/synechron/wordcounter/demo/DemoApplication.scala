package com.synechron.wordcounter.demo

import com.synechron.wordcounter.core.WordCounterImpl

object DemoApplication {
  def main(args: Array[String]): Unit = {
    val wordCounter = new WordCounterImpl
    val words = Seq("abc", "bbc", "Abc", "bcb")
    wordCounter.add(words: _*)
    val words2 = Seq("aaa", "abc")
    wordCounter.add(words2: _*)
    val count = wordCounter.get("abc")
    println(s"Count for word abc is $count")
  }
}
