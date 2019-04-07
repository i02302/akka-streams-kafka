package com.i02302.file

import java.io._
import java.util.zip.GZIPInputStream

import com.i02302.file.line.Line
import com.i02302.util.Closable

import scala.io.Source

case class TextFile[T <: Line](filePath: String = "", append: Boolean = false)(implicit RSLineConverter: (String) => T)
  extends java.io.File(filePath) with AutoCloseable with Closable {

  val reader = Reader()
  val writer = Writer()

  case class Reader() extends scala.collection.Iterator[T] with AutoCloseable {
    private var source: Source = _
    private var ite: Iterator[String] = _

    def open = {
      close
      source = Source.fromFile(getAbsolutePath)
      ite = source.getLines()
      this
    }

    def openFromGzip = {
      using(new FileInputStream(getAbsolutePath)) { fis =>
        using(new BufferedInputStream(fis)) { bis =>
          using(new GZIPInputStream(bis)) { gis =>
            source = Source.fromInputStream(gis)
            ite = source.getLines()
          }
        }
      }
    }

    override def hasNext: Boolean = (ite ne null) && ite.hasNext

    override def next(): T = {
      var line = RSLineConverter(ite.next())
      while (line.isEmpty && hasNext) {
        line = RSLineConverter(ite.next())
      }
      line
    }

    override def close = {
      if (source ne null) source.close()
    }
  }

  case class Writer() extends AutoCloseable {
    private var printWriter: PrintWriter = _

    def open = {
      if (!exists()) createNewFile
      using(new FileOutputStream(getAbsolutePath)) { fos =>
        printWriter = new PrintWriter(fos, append)
      }
      this
    }

    def flush = {
      if (printWriter ne null) printWriter.flush()
      this
    }

    def println(line: T): Writer = {
      printWriter.println(line)
      this
    }

    override def close = {
      flush
      if (printWriter ne null) printWriter.close()
    }
  }

  def open: TextFile[T] = {
    reader.open
    writer.open
    this
  }

  def close = {
    reader.close
    writer.close
  }

  def closeAndDelete: Unit = {
    close
    if (exists) delete
  }

  override def createNewFile: Boolean = {
    if (getParentFile != null && !getParentFile.exists()) getParentFile.mkdirs()
    super.createNewFile()
  }

  def moveTo(dest: TextFile[T], force: Boolean = true): TextFile[T] = {
    if (force) dest.closeAndDelete
    writer.flush
    close
    renameTo(dest)
    delete()
    dest
  }

}
