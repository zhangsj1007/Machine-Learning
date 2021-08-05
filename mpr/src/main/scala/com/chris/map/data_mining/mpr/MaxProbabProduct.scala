package com.chris.map.data_mining.mpr

//scala
import util.control.Breaks._
import scala.collection.mutable.{ArrayBuffer, PriorityQueue}

//breeze
import breeze.linalg.DenseVector

//mpr
import com.chris.map.data_mining.data.TransNode

class MaxProbabProduct (val transNodes : Array[TransNode], val Vs : Array[DenseVector[Double]]){

  def init (dest : Int)= {
    val v = Vs(dest)
    for (i <- 0 until transNodes.length){
      val transNode = transNodes(i)
      transNode.popularity = v(i)
      transNode.predecessor = -1
    }
  }

  def getMPR (start : Int, dest : Int) = {
    init(dest)

    val num = transNodes.length
    val popularProbab = new Array[Double](num)
    popularProbab(start) = 1.0
    val pq = new PriorityQueue[TransNode]()
    val scanNodes = new ArrayBuffer[TransNode]()
    val res = new ArrayBuffer[Int]()

    val startNode = transNodes(start)
    pq.enqueue(startNode)

    breakable{
      while (!pq.isEmpty){
        val transNode = pq.dequeue()
        if (transNode.id == dest){
          var tId = transNode.id
          while (tId != -1){
            res += tId
            val tTN = transNodes(tId)
            tId = tTN.predecessor
          }
          break
        }else {
          scanNodes += transNode
          for (adjTNId <- transNode.adjNodes){
            val adjTN = transNodes(adjTNId)
            if (popularProbab(adjTNId) < popularProbab(transNode.id) * adjTN.popularity){
              popularProbab(adjTNId) = popularProbab(transNode.id) * adjTN.popularity
              adjTN.predecessor = transNode.id
              pq.enqueue(adjTN)
            }
          }
        }
      }
    }

    res.toArray.reverse
  }
}
