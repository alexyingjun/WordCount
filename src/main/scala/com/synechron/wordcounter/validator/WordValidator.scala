package com.synechron.wordcounter.validator

object WordValidator extends Validator {
  /**
   * Validate a single word
   *
   * @param word
   * word to be validated
   * @return
   * true if the word is valid, otherwise false
   */
  override def validate(word: String): Boolean = word != null && !word.isBlank && word.forall(_.isLetter)
}
