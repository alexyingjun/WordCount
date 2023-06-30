package com.synechron.wordcounter.core.cache

import java.util.concurrent.ConcurrentHashMap

trait LocalCache {

  /**
   * Get count for a given word from the cache
   * @param word
   *    given word to retrieve the count
   * @return
   *    count of the given word, return 0 if not presented
   */
  def getCount(word: String): Long

  /**
   * Add a grouped map with word and its count to the cache
   * @param map
   * @param translator
   */
  def addToCache(map: Map[String,Long]): Unit

  def getMap(): ConcurrentHashMap[String,ValueItem]
}
