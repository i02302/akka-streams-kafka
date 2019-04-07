package com.i02302.util

import scala.util.control.Exception.allCatch

trait Closable {

  def using[A, R <: {def close()}](r: R)(f: R => A): A =
    allCatch withApply { t =>
      r close()
      throw t
    } apply {
      f(r)
    }

  def deletable[A, R <: {def close(); def delete(): Boolean; def exists(): Boolean}](r: R)(f: R => A): A =
    allCatch withApply { t =>
      r.close()
      if (r.exists()) r.delete()
      throw t
    } apply {
      f(r)
    }

}
