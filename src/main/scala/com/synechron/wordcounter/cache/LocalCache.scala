package com.synechron.wordcounter.cache

trait LocalCache {

  /**
   * Add a word to the cache
   * @param word
   *    word to be added to the cache
   * @return
   *    count of the word after added
   */
  def addToCache(word: String): Long

  /**
   * Get count for a given word from the cache
   * @param word
   *    given word to retrieve the count
   * @return
   *    count of the given word, return 0 if not presented
   */
  def getCount(word: String): Long
}
