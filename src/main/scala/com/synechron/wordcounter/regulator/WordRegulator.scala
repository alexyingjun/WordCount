package com.synechron.wordcounter.regulator

import com.synechron.wordcounter.validator.Validator

case class WordRegulator(validator:Validator) {
  def filter(words: String*) = words.filter(validator.validate).map(_.toLowerCase)
}
