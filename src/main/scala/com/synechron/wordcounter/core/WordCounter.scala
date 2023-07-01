package com.synechron.wordcounter.core

import com.synechron.wordcounter.core.flow.WordCounterFlow

/**
 * Abstract class to add words and get count for a given word, features open for sub classes
 * @param processFlow
 *    The WordCounterFlow class in which the add and get flows are defined
 */
abstract class WordCounter(processFlow: WordCounterFlow) extends Producer[String] with Getter[String, Long]{
  /**
   * get count of given word
   * @param word
   *    the word to find the count for
   * @return
   *    how many times of the word appeared
   */
  override def get(word: String): Long = processFlow.getFlow(word)

  /**
   * add words
   * @param words
   *    the words to be added
   */
  override def add(words: String*): Unit = processFlow.addFlow(words)
}
