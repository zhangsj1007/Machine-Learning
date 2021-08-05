package com.chris.map.data_mining.data

class AdjTraj (val tripId : String, val endPoint : Point) {

  def endClusterId = endPoint.clusterId
}
