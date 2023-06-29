package com.synechron.wordcounter.core

import com.synechron.wordcounter.cache.{LocalCache, WordCountCache}
import com.synechron.wordcounter.validator.{Validator, WordValidator}

class WordCounterImpl extends WordCounter {
  override protected def validator: Validator = WordValidator
  override protected def localCache: LocalCache = WordCountCache
}
