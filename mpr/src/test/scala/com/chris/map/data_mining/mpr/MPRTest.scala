package com.chris.map.data_mining.mpr

//mpr
import com.chris.map.data_mining.data.{Cluster, Point, TransNode, Trip}


import scala.collection.mutable.ArrayBuffer


object MPRTest {

  val a = new Point(0.0, 0.0)
  val b = new Point(0.0, 1000.0)
  val c = new Point(1000.0, 1000.0)
  val d = new Point(1000.0, 0.0)

  //new e
  val e = new Point(2000.0, 0.0)

  val cluster1 = new Cluster(); cluster1.addPoint(a); cluster1.center = a; a.clusterId = 0
  val cluster2 = new Cluster(); cluster2.addPoint(b); cluster2.center = b; b.clusterId = 1
  val cluster3 = new Cluster(); cluster3.addPoint(c); cluster3.center = c; c.clusterId = 2
  val cluster4 = new Cluster(); cluster4.addPoint(d); cluster4.center = d; d.clusterId = 3

  //new e
  val cluster5 = new Cluster(); cluster5.addPoint(e); cluster5.center = e; e.clusterId = 4

  //val clusters = Array(cluster1, cluster2, cluster3, cluster4)
  val clusters = Array(cluster1, cluster2, cluster3, cluster4, cluster5)

  val trip1 = new Trip("a->d"); trip1.addPoint(a); trip1.addPoint(d)
  val trip2 = new Trip("d->c"); trip2.addPoint(d); trip2.addPoint(c)
  val trip3 = new Trip("c->b"); trip3.addPoint(c); trip3.addPoint(b)
  val trip4 = new Trip("b->a"); trip4.addPoint(b); trip4.addPoint(a)
  val trip5 = new Trip("a->b"); trip5.addPoint(a); trip5.addPoint(b)
  val trip6 = new Trip("b->c"); trip6.addPoint(b); trip6.addPoint(c)
  val trip7 = new Trip("c->d"); trip7.addPoint(c); trip7.addPoint(d)
  val trip8 = new Trip("d->a"); trip8.addPoint(d); trip8.addPoint(a)

  //new e
  val trip9 = new Trip("d->e"); trip9.addPoint(d); trip9.addPoint(e)

  //val trips = Array(trip1, trip2, trip3, trip4, trip5, trip6, trip7, trip8)
  val trips = Array(trip1, trip2, trip3, trip4, trip5, trip6, trip7, trip8, trip9)

  def main(args: Array[String]): Unit = {
    val transProbab = new TransProbability(clusters, trips)
    val Ps = transProbab.getPArray()
    val Vs = transProbab.getVArray(Ps)

    val transNodes = new ArrayBuffer[TransNode]()
    for (cluster <- clusters){
      val transNode = new TransNode(cluster.clusterId, cluster.adjClusters)
      transNodes.append(transNode)
    }

    val maxProbabPro = new MaxProbabProduct(transNodes.toArray, Vs)
    val route = maxProbabPro.getMPR(4, 0)
    for (n <- route){
      println(n)
    }
  }
}
