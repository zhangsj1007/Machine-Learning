package com.chris.map.data_mining.data

import scala.collection.mutable.ArrayBuffer

class Interval(val startX : Double, val endX : Double, val startY : Double, val endY : Double){

  val containPoints = new ArrayBuffer[Point]()

  def addPoint (point : Point)={
    containPoints.append(point)
  }
}

