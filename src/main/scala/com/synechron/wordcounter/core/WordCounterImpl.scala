package com.synechron.wordcounter.core

import com.synechron.wordcounter.core.flow.WordCounterFlow

/**
 * Simple WordCounter class implementation
 * @param processFlow
 *
 */
case class WordCounterImpl(processFlow: WordCounterFlow) extends WordCounter(processFlow: WordCounterFlow) {
}
