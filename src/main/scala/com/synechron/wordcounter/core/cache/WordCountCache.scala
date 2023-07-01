package com.synechron.wordcounter.core.cache

import com.synechron.wordcounter.core.cache.WordCountCache.{translator, wordCountMap}
import com.synechron.wordcounter.core.flow.regulator.validator.{Validator, WordValidator}
import com.synechron.wordcounter.util.Translator

import java.util.concurrent.ConcurrentHashMap

/**
 * WordCount object to hold hashmap as a cache/storage and default translator
 */
object WordCountCache {
  private val wordCountMap: ConcurrentHashMap[String, ValueItem] = new ConcurrentHashMap[String, ValueItem]()
  var translator:Translator = new Translator
}

/**
 * WordCount object's companion class
 */
class WordCountCache extends LocalCache {
  private val validator: Validator = new WordValidator

  /**
   * Set to a designated translator, mainly for testing
   * @param translator
   */
  def setTranslator(translator: Translator):Unit = WordCountCache.translator = translator

  /**
   * Get count for a given word from the cache
   *
   * @param word
   * given word to retrieve the count
   * @return
   * count of the given word, return 0 if not presented
   */
  override def getCount(word: String): Long = wordCountMap.getOrDefault(word, ValueItem(0)).value

  /**
   * private method to translate the word and add to map
   * @param word
   *    lowercase input word
   * @param count
   *    count of the word in the input
   */
  private def translateAndAdd(word: String, count: Long): Unit = {
    try {
      val englishWord = translator.translate(word)
      if(validator.validate(englishWord)) {   // need to validate the translated word
        if (!englishWord.equalsIgnoreCase(word)) { //not a english word, not exist in the word count map
          val countItem = wordCountMap.getOrDefault(englishWord.toLowerCase, ValueItem(0))
          wordCountMap.putIfAbsent(englishWord.toLowerCase, countItem)
          wordCountMap.putIfAbsent(word,countItem)
        }
        doAddToMap(englishWord.toLowerCase, count)
      }else{
        println("Translated word ["+englishWord+"] from "+word+ " is invalid")
      }
    } catch {
      case _: Throwable => println("Error in add while translating word " + word)
    }
  }

  /**
   * private method actually add the input map(word,count) to the word count map
   * @param word
   *    input word
   * @param count
   *    count of the input word
   * @return
   *    ValueItem wrapper class after merge
   */
  private def doAddToMap(word: String, count: Long) = {
    wordCountMap.merge(word, ValueItem(count), (a, b) => {
      a.value += b.value
      a
    })
  }

  /**
   * override method to add the input map(word,count) to the word count map
   * @param map
   *    input map(word,count)
   */
  override def addToCache(map: Map[String, Long]): Unit = {
    val groupedMap = map.groupBy(e => wordCountMap.containsKey(e._1))   //group input map to 2 maps
    groupedMap.get(true) match {  // the map contains all words which already exist in the word count map
      case Some(map) => map.foreach(e=> doAddToMap(e._1,e._2))  // straightly add the map to the word count map, no need to translate
      case None =>
    }
    groupedMap.get(false) match { // the map contains all words which are new to the word count map
      case Some(map) => map.foreach(e => translateAndAdd(e._1,e._2))  // need to translate the words first, then add to the word count map
      case None =>
    }
  }
}
