package com.synechron.wordcounter.core.cache

import com.synechron.wordcounter.core.cache.WordCountCache.{translator, wordCountMap}
import com.synechron.wordcounter.core.flow.regulator.validator.{Validator, WordValidator}
import com.synechron.wordcounter.util.Translator
import upickle.core.LinkedHashMap

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
  //LinkedHashMap to store foreign words with limited capacity
  private val foreignWordMap: LinkedHashMap[String, ValueItem] = LinkedHashMap[String, ValueItem]()
  private var fMapCapacity=16

  /**
   * Set to a designated translator, mainly for testing
   * @param translator
   */
  def setTranslator(translator: Translator):Unit = WordCountCache.translator = translator
  def setForeignWordMapCapacity(cap:Int):Unit = fMapCapacity=cap
  /**
   * Get count for a given word from the cache
   *
   * @param word
   * given word to retrieve the count
   * @return
   * count of the given word, return 0 if not presented
   */
  override def getCount(word: String): Long = {
    if(wordCountMap.containsKey(word)){  //English word exists in the cache
      wordCountMap.get(word).value
    }else {
      if (foreignWordMap.contains(word)) {  //foreign word exists in the linked hash map
        val valueItem = foreignWordMap.get(word).get
        foreignWordMap.remove(word)       //remove the word first, then append to end of the map which makes it most recent used word
        foreignWordMap.put(word, valueItem)
        valueItem.value
      } else {  //foreign word does not exist in linked hash map, translated to english and get count
        val englishWord = translateWord(word)
        val count = wordCountMap.getOrDefault(englishWord, ValueItem("", 0))
        if(count.value!=0) addToFMap(word,ValueItem(englishWord.toLowerCase, count.value))
        count.value
      }
    }
  }

  /**
   * private method to translate the word and add to map
   * @param word
   *    lowercase input word
   * @param count
   *    count of the word in the input
   */
  private def translateAndAdd(word: String, count: Long): Unit = {
    val englishWord = translateWord(word)
    if (!validator.validate(englishWord)) {  // need to validate the translated word
      println("Translated word [" + englishWord + "] from " + word + " is invalid")
    }else {
      if (!englishWord.equalsIgnoreCase(word)) { //not a english word, not exist in the word count map
        val countItem = wordCountMap.getOrDefault(englishWord.toLowerCase, ValueItem(englishWord, 0))
        wordCountMap.putIfAbsent(englishWord.toLowerCase, countItem)
        addToFMap(word, countItem)
      }
      doAddToMap(englishWord.toLowerCase, count)
    }
  }

  private def addToFMap(word:String, countItem:ValueItem):Unit={
    foreignWordMap.put(word, countItem)
    if (foreignWordMap.size > fMapCapacity) {   //if exceed space limit, remove the least used word
      foreignWordMap.remove(foreignWordMap.head._1)
    }
  }

  private def translateWord(word:String): String = {
    try {
      translator.translate(word)
    } catch {
      case _: Throwable => println("Error in add while translating word " + word)
        ""
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
    var englishWord = word
    if(foreignWordMap.contains(word)) englishWord = foreignWordMap.get(word).get.engWord
    wordCountMap.merge(englishWord, ValueItem(englishWord, count), (a, b) => {
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
    val groupedMap = map.groupBy(e => wordCountMap.containsKey(e._1)||foreignWordMap.contains(e._1))   //group input map to 2 maps
    groupedMap.get(true) match {  // the map contains all words which already exist in the word count map
      case Some(map) =>
        map.foreach(e=> doAddToMap(e._1,e._2))  // straightly add the map to the word count map, no need to translate
      case None =>
    }
    groupedMap.get(false) match { // the map contains all words which are new to the word count map
      case Some(map) =>
        map.foreach(e => translateAndAdd(e._1,e._2))  // need to translate the words first, then add to the word count map
      case None =>
    }
  }
}
