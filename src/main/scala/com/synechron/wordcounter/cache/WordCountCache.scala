package com.synechron.wordcounter.cache

import java.util.concurrent.ConcurrentHashMap

object WordCountCache extends LocalCache {
  private val wordCountMap: ConcurrentHashMap[String, Long] = new ConcurrentHashMap[String, Long]()

  /**
   * Add a word to the cache
   *
   * @param word
   * word to be added to the cache
   * @return
   * count of the word after added
   */
  override def addToCache(word: String): Long = {
    wordCountMap.merge(word, 1, (a,b)=>a+b)
  }

  /**
   * Get count for a given word from the cache
   *
   * @param word
   * given word to retrieve the count
   * @return
   * count of the given word, return 0 if not presented
   */
  override def getCount(word: String): Long = wordCountMap.getOrDefault(word, 0)
}
