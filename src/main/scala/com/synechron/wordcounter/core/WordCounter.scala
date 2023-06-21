package com.synechron.wordcounter.core

import com.synechron.wordcounter.cache.LocalCache
import com.synechron.wordcounter.util.Translator
import com.synechron.wordcounter.validator.Validator

abstract class WordCounter (translator: Translator){

  protected def validator: Validator
  protected def localCache: LocalCache
  /**
   * add 1 or more words
   * @param words
   *    add 1 or more words to process
   */
  def addWords(words: String*): Unit = words.filter(validator.validate).foreach( word => {
    try{
      addTranslatedWord(translator.translate(word))
    }catch {
      case _: Throwable => println("Error in add while translating word "+word)
    }
  })

  /**
   * add a single translated word
   * @param word
   *    single translated word to be added
   */
  protected def addTranslatedWord(word: String): Unit = {
    if(validator.validate(word)) localCache.addToCache(word.toLowerCase)
  }

  /**
   * get count of given word
   * @param word
   *    the word to find the count for
   * @return
   *    how many times of the word appeared
   */
  def getCountOfWord(word: String): Long = {
    if(!validator.validate(word)) return 0
    var translatedWord = ""
    try {
      translatedWord = translator.translate(word).toLowerCase()
    } catch {
      case _: Throwable => println("Error in get while translating word " + word)
    }
    localCache.getCount(translatedWord)
  }
}
