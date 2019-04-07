package com.i02302.pimp

import scala.annotation.tailrec

object IteratorManager {

  implicit def iteratable[T](ite: scala.collection.Iterator[T]) = new {

    def next(n: Int): Seq[T] = next(ite, n)

    @tailrec
    private[this] def next(ite: scala.collection.Iterator[T], count: Int = 0, seq: Seq[T] = Seq()): Seq[T] =
      if (!ite.hasNext || count < 1) {
        seq
      } else {
        next(ite, count - 1, Seq(ite.next()) ++ seq)
      }

  }

}
