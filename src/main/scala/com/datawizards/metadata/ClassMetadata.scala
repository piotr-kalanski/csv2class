package com.datawizards.metadata

import scala.reflect.ClassTag

object ClassMetadata {

  def getClassFields[T](implicit ct:ClassTag[T]): Seq[String] =
    implicitly[ClassTag[T]]
      .runtimeClass
      .getDeclaredFields
      .map(f => f.getName)

}
