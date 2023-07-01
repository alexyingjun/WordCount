package com.synechron.wordcounter.core.cache

/**
 * Wrapper class of the count. In this way, different hashmap keys can point to the same object
 * @param value
 *    count of a word
 */
case class ValueItem(var value:Long)

