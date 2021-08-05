package com.chris.map.data_mining.mpr

import scala.collection.mutable.Buffer
import scala.collection.mutable.Queue
import scala.collection.mutable.ArrayBuffer

import com.chris.map.data_mining.data.Point
import com.chris.map.data_mining.data.Interval
import com.chris.map.data_mining.data.Cluster

import com.chris.map.data_mining.util.Util

//rtree
import com.vividsolutions.jts.index.strtree.STRtree
import com.vividsolutions.jts.geom.Envelope
import com.vividsolutions.jts.geom.Coordinate

import scala.collection.JavaConverters._

class Coherence (val points : Array[Point], val pointRtree : STRtree) {
  val intervals = Array.ofDim[Interval](Coherence.INTERVAL_X, Coherence.INTERVAL_Y)

  def init()={

    var minX = Double.MaxValue
    var maxX = Double.MinValue
    var minY = Double.MaxValue
    var maxY = Double.MinValue

    for (p <- points){
      if (p.x < minX) minX = p.x
      if (p.x > maxX) maxX = p.x
      if (p.y < minY) minY = p.y
      if (p.y > maxY) maxY = p.y
    }

    val spanX = maxX - minX
    val intervalX = spanX / Coherence.INTERVAL_X

    val spanY = maxY - minY
    val intervalY = spanY / Coherence.INTERVAL_Y

    for (i <- 0 until Coherence.INTERVAL_X){
      for (j <- 0 until Coherence.INTERVAL_Y){
        val startX = minX + i * intervalX
        val endX = if (i == Coherence.INTERVAL_X - 1)  maxX else minX + (i + 1) * intervalX
        var startY = minY + j * intervalY
        var endY = if (j == Coherence.INTERVAL_Y - 1) maxY else minY + (j + 1) * intervalY
        val interval = new Interval(startX, endX, startY, endY)
        intervals(i)(j) = interval
      }
    }

    for (p <- points){
      for (i <- 0 until Coherence.INTERVAL_X){
        for (j <- 0 until Coherence.INTERVAL_Y){
          val interval = intervals(i)(j)
          if (p.x >= interval.startX &&
            p.x <= interval.endX &&
            p.y >= interval.startY &&
            p.y <= interval.endY){
            interval.addPoint(p)
            p.row = i
            p.column = j
          }
        }
      }
    }
  }

  def rangePoints(center : Point)= {
    //val rangePoints = new ArrayBuffer[Point]

//    val row = center.row
//    val column = center.column
//
//    for (dir <- Coherence.directions){
//      val x = row + dir(0)
//      val y = column + dir(1)
//
//      if (x >= 0 && x < intervals.length &&
//        y >= 0 && y < intervals(0).length){
//        val interval = intervals(x)(y)
//        for (p <- interval.containPoints){
//          val dist = Util.dist(center, p)
//          if (dist <= Coherence.Range_Length)
//            rangePoints.append(p)
//        }
//      }
//    }

    val minX = center.x - Coherence.Range_Length / 2
    val maxX = center.x + Coherence.Range_Length / 2
    val minY = center.y - Coherence.Range_Length / 2
    val maxY = center.y + Coherence.Range_Length / 2

    val queryPointIds = pointRtree.query(new Envelope(minX, maxX, minY, maxY))

    //rangePoints.toArray
    queryPointIds.asScala.asInstanceOf[Buffer[Int]].toArray
  }

  def cluster()= {
    val clusters = new ArrayBuffer[Cluster]
    var clusterId = 0

    for (p <- points){
      if (p.visited == false){
        p.visited = true
        val cluster = expand(p)
        if (cluster.size >= Coherence.Cluster_threshold) {
          clusters.append(cluster)
          for (p <- cluster.points) {
            p.clusterId = clusterId
          }
          clusterId += 1
        }
      }
    }

    println(s"size = $clusterId")

    for (cluster <- clusters)
      cluster.center = Util.avgPoint(cluster.points.toArray)

    clusters.toArray
  }

  def expand(center : Point) = {
    val seeds = new Queue[Point]
    val result = new Cluster

    seeds.enqueue(center)
    result.addPoint(center)

    while (seeds.isEmpty == false){
      val seed = seeds.dequeue
      //val neighPoints = rangePoints(seed)
      val queryPointIds = rangePoints(seed)
//      for (p <- neighPoints){
//        val coh = Coherence.coh(seed, p)
//        if (coh >= Coherence.Coh_Threshold){
//          if (p.visited == false){
//            p.visited = true
//            seeds.enqueue(p)
//            result.addPoint(p)
//          }
//        }
//      }

      for (id <- queryPointIds){
        val p = points(id.asInstanceOf[Int])
        val coh = Coherence.coh(seed, p)
        if (coh >= Coherence.Coh_Threshold){
          if (p.visited == false){
            p.visited = true
            seeds.enqueue(p)
            result.addPoint(p)
          }
        }
      }
    }
    result
  }
}

object Coherence {

  private val delte = 30
  private val alpha = 5
  private val beta = 2

  private val Coh_Threshold = 0.5
  private val Cluster_threshold = 100
  private val Range_Length = 150

  private val INTERVAL_X = 25
  private val INTERVAL_Y = 25

  private val directions = Array(
    Array(-1, -1), Array(-1, 0), Array(-1, 1),
    Array(0, -1), Array(0, 0), Array(0, 1),
    Array(1, -1), Array(1, 0), Array(1, 1)
  )

  def coh(p : Point , q : Point)={
    math.exp(- math.pow((Util.dist(p, q) / delte) , alpha)) * math.pow(math.abs(math.sin(math.toRadians(p.a - q.a))), beta)
  }

}
