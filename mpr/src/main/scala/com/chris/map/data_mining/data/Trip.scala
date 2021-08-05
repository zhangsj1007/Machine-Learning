package com.chris.map.data_mining.data

//scala
import scala.collection.mutable.ArrayBuffer
import scala.util.control.Breaks._

//mpr
import com.chris.map.data_mining.util.Util

class Trip (val tripId : String){
  private var containPoints = new ArrayBuffer[Point]

  private var _startNode : Point = null
  private var _endNode : Point = null

  private def getNextSegment = {
    _startNode = _endNode
    _endNode = null

    var i = 0
    var j = 0

    breakable{
      //for (i <- 0 until containPoints.length)
      while(i < containPoints.length){

        val p = containPoints(i)

        if (p.clusterId != -1){
          j = i
          val clusterId = p.clusterId
          val trajPoints = new ArrayBuffer[Point]
          while (j < containPoints.length && containPoints(j).clusterId == clusterId){
            trajPoints.append(containPoints(j))
            j = j+1
          }
          val center = Util.avgPoint(trajPoints.toArray)
          _endNode = center
          break
        }

        i += 1
      }
    }

    containPoints.remove(0, math.max(i, j))
  }

  def addPoint(p : Point): Unit ={
    containPoints.append(p)
  }

  def sort = {
    containPoints = containPoints.sortBy(p => p.ts)(Ordering.Long)
  }

  def dispatchTraj (clusters : Array[Cluster])  = {
    while (containPoints.length != 0){
      getNextSegment
      if (_startNode != null && _endNode != null){
        val startCluster = clusters(_startNode.clusterId)
        //给cluster添加traj
        startCluster.adjTrajs.append(new AdjTraj(tripId, new Point(_endNode)))
        //给cluster添加邻边
        startCluster.adjClusters.add(_endNode.clusterId)
      }
    }
  }
}
