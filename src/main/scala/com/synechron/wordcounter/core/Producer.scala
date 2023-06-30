package com.synechron.wordcounter.core

trait Producer[T] {
  def add(item:T*): Unit

}
