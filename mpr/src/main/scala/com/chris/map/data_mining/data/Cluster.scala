package com.chris.map.data_mining.data

import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.Set

class Cluster {
  val points = new ArrayBuffer[Point]
  var center = new Point(0.0, 0.0)
  val adjTrajs = new ArrayBuffer[AdjTraj]
  val adjClusters = Set[Int]()

  def addPoint (point : Point): Unit ={
    points.append(point)
  }

  def clusterId = center.clusterId

  def size= points.size

  def absorbState = {
    if (adjClusters.isEmpty ||
      (adjClusters.size == 1 && adjClusters.contains(clusterId)))
      true
    else
      false
  }
}
