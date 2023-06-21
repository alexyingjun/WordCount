package com.synechron.wordcounter.validator

trait Validator {

  /**
   * Validate a single word
   * @param word
   *    word to be validated
   * @return
   *    true if the word is valid, otherwise false
   */
  def validate(word: String): Boolean

}
