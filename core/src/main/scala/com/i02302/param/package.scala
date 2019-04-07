package com.i02302

import org.json4s.NoTypeHints
import org.json4s.native.Serialization

package object param {

  object JsonSerializer {

    implicit val formats = Serialization.formats(NoTypeHints) +
      SystemType.Serializer() + SystemType.KeySerializer() +
      JobType.Serializer() + JobType.KeySerializer() +
      JobStatus.Serializer() + JobStatus.KeySerializer() +
      OptionKey.Serializer() + OptionKey.KeySerializer() +
      PartitionType.Serializer() + PartitionType.KeySerializer()

  }

}
