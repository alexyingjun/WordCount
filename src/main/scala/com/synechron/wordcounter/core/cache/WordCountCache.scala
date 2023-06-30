package com.synechron.wordcounter.core.cache

import com.synechron.wordcounter.core.flow.validator.{Validator, WordValidator}
import com.synechron.wordcounter.util.Translator
import java.util.concurrent.{ConcurrentHashMap}

object WordCountCache {
  private val wordCountMap: ConcurrentHashMap[String, ValueItem] = new ConcurrentHashMap[String, ValueItem]()
  var translator:Translator = new Translator
}
class WordCountCache extends LocalCache {
  private val validator: Validator = new WordValidator
  private val wordCountMap = WordCountCache.wordCountMap
  def setTranslator(translator: Translator):Unit = WordCountCache.translator = translator

  /**
   * Get count for a given word from the cache
   *
   * @param word
   * given word to retrieve the count
   * @return
   * count of the given word, return 0 if not presented
   */
  override def getCount(word: String): Long = {
    println("Get count for "+word)
    println("Current map => "+wordCountMap)
    if(wordCountMap.containsKey(word)) println("Current map has count for "+word)
    else println("Current map has no count for "+word)
    val count = wordCountMap.getOrDefault(word, ValueItem(0)).value
    println("Got count "+count)
    count
  }

  private def translateAndAdd(word: String, count: Long): Unit = {
    try {
      val englishWord = WordCountCache.translator.translate(word)
      println("Translated "+word+" to "+englishWord)
      if(validator.validate(englishWord)) {
        println("Translated word "+word+" is valid")
        if (!englishWord.equalsIgnoreCase(word)) { //not a english word, not exist in map
          val countItem = wordCountMap.getOrDefault(englishWord.toLowerCase, ValueItem(0))
          wordCountMap.putIfAbsent(englishWord.toLowerCase, countItem)
          wordCountMap.putIfAbsent(word.toLowerCase,countItem)
        }
        doAddToMap(englishWord.toLowerCase, count)
      }else{
        println("Translated word "+word+" is invalid")
      }
    } catch {
      case _: Throwable => println("Error in add while translating word " + word)
    }
  }

  private def doAddToMap(word: String, count: Long) = {
    wordCountMap.merge(word, ValueItem(count), (a, b) => {
      a.value += b.value
      a
    })
  }

  override def addToCache(map: Map[String, Long]): Unit = {
    val groupedMap = map.groupBy(e => wordCountMap.containsKey(e._1))
    groupedMap.get(true) match {
      case Some(map) => {
        println("Add without translate ==> " + map)
        map.foreach(e=> doAddToMap(e._1,e._2))
      }
      case None =>
    }
    groupedMap.get(false) match {
      case Some(map) => {
        println("Add with translate ==> " + map);
        map.foreach(e => translateAndAdd(e._1,e._2))
      }
      case None =>
    }
    println(wordCountMap)
  }

  override def getMap(): ConcurrentHashMap[String, ValueItem] = wordCountMap
}
