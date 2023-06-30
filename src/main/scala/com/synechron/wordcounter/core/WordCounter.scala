package com.synechron.wordcounter.core

import com.synechron.wordcounter.core.flow.WordCounterFlow

abstract class WordCounter(flow: WordCounterFlow) extends Producer[String] with Getter[String, Long]{
  /**
   * get count of given word
   * @param word
   *    the word to find the count for
   * @return
   *    how many times of the word appeared
   */
  override def get(word: String): Long = {
    flow.getFlow(word)
  }

  override def add(words: String*): Unit = {
    println("Add words: " + words)
    flow.addFlow(words)
  }
}
