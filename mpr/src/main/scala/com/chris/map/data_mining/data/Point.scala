package com.chris.map.data_mining.data


case class Point(val x : Double, val y : Double, val a : Double = 0.0, val ts : Long = 0, val tripid : String = ""){

  //space row
  var row = 0
  //space column
  var column = 0

  var visited = false

  var clusterId = -1

  var id = 0

  def this (p : Point){
    this(p.x, p.y, p.a, p.ts, p.tripid)
    clusterId = p.clusterId
  }

  override def toString() = {
    s"x = $x, y = $y, a = $a, clusterId = $clusterId"
  }
}


