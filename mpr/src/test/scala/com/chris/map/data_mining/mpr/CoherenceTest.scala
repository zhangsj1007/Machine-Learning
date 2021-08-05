package com.chris.map.data_mining.mpr

import com.chris.map.data_mining.data.Point

object CoherenceTest {

  def cohTest() = {
    //delte = 200
    //alpha = 5
    //beta = 2

    var p1 = new Point(200, 0, 90)
    var p2 = new Point(0, 0, 0)

    var coh_ = Coherence.coh(p1, p2)
    var coh = (coh_ * 1000).toInt / 1000.0

    assert(coh == 0.367)

    p1 = new Point(100, 0, 0)
    p2 = new Point(0, 0, 30)

    coh_ = Coherence.coh(p1, p2)
    coh = (coh_ * 1000).toInt / 1000.0

    assert(coh == 0.242)

    println ("Test Coh done")

  }

  def main(args: Array[String]): Unit = {
    cohTest
  }
}
