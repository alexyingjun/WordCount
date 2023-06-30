package com.synechron.wordcounter.core.flow.regulator

import com.synechron.wordcounter.core.flow.validator.Validator

case class WordRegulator(validator:Validator) {
  def filterWords(words: String*) = words.filter(validator.validate).map(_.toLowerCase)
  def filterWord(word: String) = {
    if(validator.validate(word)) word.toLowerCase
    else ""
  }
}
