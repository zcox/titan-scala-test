package com.pongr.titantest

import com.thinkaurelius.titan.core._
import scala.collection.JavaConversions._
import com.google.common.base.Optional //force a compile error with guava r08

object Main {
  def main(args: Array[String]) {
    //val g = TitanFactory.open("/tmp/titan-test")
    val g = TitanFactory.openInMemoryGraph()
    val v1 = g.addVertex("v1")
    println(v1)
    for (v <- g.getVertices) println(v)
  }
}
