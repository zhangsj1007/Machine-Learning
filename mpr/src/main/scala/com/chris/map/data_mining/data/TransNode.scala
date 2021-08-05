package com.chris.map.data_mining.data

import scala.collection.mutable.Set

class TransNode (val id : Int, val adjNodes : Set[Int]) extends Comparable[TransNode]{

  var popularity = 0.0

  var predecessor = -1

  override def compareTo(o: TransNode): Int = (o.popularity - popularity).asInstanceOf[Int]

}
