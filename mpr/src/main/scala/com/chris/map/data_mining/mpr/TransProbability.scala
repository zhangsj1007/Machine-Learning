package com.chris.map.data_mining.mpr


//scala
import scala.collection.mutable.Map

//breeze
import breeze.linalg.DenseMatrix
import breeze.linalg.DenseVector

//mpr
import com.chris.map.data_mining.data.Trip
import com.chris.map.data_mining.data.Cluster
import com.chris.map.data_mining.util.Util


class TransProbability (val clusters : Array[Cluster], val trips : Array[Trip]){

  def getPArray() = {
    for (trip <- trips){
      trip.dispatchTraj(clusters)
    }

    val num = clusters.length
    val Ps = new Array[DenseMatrix[Double]](num)

    //以i作为终点，进行遍历
    for (i <- 0 until num){

      val P = DenseMatrix.zeros[Double](num, num)
      val destCluster = clusters(i)
      val destCenter = destCluster.center

      for (j <- 0 until num){
        val cluster = clusters(j)

        if (i == j){
          //作为destination
          P(i, i) = 1.0
        }else if (cluster.absorbState){
          //作为absorbing state
          P(j, j) = 1.0
        }else{
          val clusterId = cluster.clusterId
          val collectFreq : Map[Int, Double] = Map()
          var sumFreq = 0.0

          //遍历每一个cluster的adjacent trajectory
          for (adjTraj <- cluster.adjTrajs) {
            val adjClusterId = adjTraj.endClusterId
            var freq = collectFreq.getOrElse(adjClusterId, 0.0)
            val dist = Util.dist(adjTraj.endPoint, destCenter)
            freq += TransProbability.heuristic(dist) * 1
            collectFreq(adjClusterId) = freq
            sumFreq += freq
          }

          //计算每个cluster到其他cluster的probablity
          for ((adjClusterId, freq) <- collectFreq){
            val probab = freq / sumFreq
            P(clusterId, adjClusterId) = probab
          }
        }
      } //end for j
      //canonical form
      Util.transferMat(P, i, num - 1)
      Ps(i) = P
    } //end for i

    Ps
  }

  def getVArray(Ps : Array[DenseMatrix[Double]]) = {
    val num = Ps.length
    val Vs = new Array[DenseVector[Double]](num)

    for (i <- 0 until num){
      val P = Ps(i)

      val Q = Util.getSubMat(P, 0, num - 2, 0, num - 2)

      val D = P(::, num - 1).slice(0, num - 1)

      val V_ = DenseVector.zeros[Double](num - 1)

      for (time <- 0 until TransProbability.times)
        V_ += Util.pow_(Q, time) * D

      val V = DenseVector.zeros[Double](num)
      V(num - 1) = 1.0
      for (j <- 0 until num - 1)
        V(j) = V_(j)
      Util.transferVec(V, num - 1, i)

      Vs(i) = V
    }
    Vs
  }
}

object TransProbability {
  val times = 20
  def heuristic (dist : Double)= math.exp(-dist/1000) //dist单位换成公里，否则e指数的倒数超出double能够表示的范围
}
