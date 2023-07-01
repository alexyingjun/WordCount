package com.synechron.wordcounter.core.flow.regulator

import com.synechron.wordcounter.core.flow.regulator.validator.Validator

/**
 * Class to regulate input words for add and get count features
 * @param validator
 *    Validator class in which the input words are validated against rules
 */
case class WordRegulator(validator:Validator) {
  /**
   * This is for add words feature. To filter out invalid input words and change each of them to lowercase
   * @param words
   *    input words to add
   * @return
   *    regulated input words
   */
  def filterWords(words: String*) = words.filter(validator.validate).map(_.toLowerCase)

  /**
   * This is for get count feature. To filter out invalid input words and change each of them to lowercase
   *
   * @param words
   *    input word to get count for
   * @return
   *    lowercase input word if valid, otherwise empty string
   */
  def filterWord(word: String) = {
    if(validator.validate(word)) word.toLowerCase
    else ""
  }
}
