package com.synechron.wordcounter.runner

trait Producer[T] {
  def add(item:T*): Unit

}
