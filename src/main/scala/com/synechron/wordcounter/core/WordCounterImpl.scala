package com.synechron.wordcounter.core

import com.synechron.wordcounter.cache.{LocalCache, WordCountCache}
import com.synechron.wordcounter.util.Translator
import com.synechron.wordcounter.validator.{Validator, WordValidator}

class WordCounterImpl(translator: Translator) extends WordCounter(translator) {
  override protected def validator: Validator = WordValidator
  override protected def localCache: LocalCache = WordCountCache
}
