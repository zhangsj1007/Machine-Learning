package com.chris.map.data_mining.util

//breeze
import breeze.linalg._

//mpr
import com.chris.map.data_mining.data.Point


object UtilTest {
  def transferMatTest = {
    var mat = DenseMatrix((1.0, 0.0, 0.0), (0.5, 0.5, 0.0), (0.5, 0.0, 0.5))
    Util.transferMat(mat, 0, 2)
    assert(mat(0, 0) == 0.5 && mat(0, 1) == 0.0 && mat(0, 2) == 0.5)
    assert(mat(1, 0) == 0.0 && mat(1, 1) == 0.5 && mat(1, 2) == 0.5)
    assert(mat(2, 0) == 0.0 && mat(2, 1) == 0.0 && mat(2, 2) == 1.0)

    //reverse
    mat = DenseMatrix((1.0, 0.0, 0.0), (0.5, 0.5, 0.0), (0.5, 0.0, 0.5))
    Util.transferMat(mat, 2, 0)
    assert(mat(0, 0) == 0.5 && mat(0, 1) == 0.5 && mat(0, 2) == 0.0)
    assert(mat(1, 0) == 0.0 && mat(1, 1) == 1.0 && mat(1, 2) == 0.0)
    assert(mat(2, 0) == 0.0 && mat(2, 1) == 0.5 && mat(2, 2) == 0.5)


    println ("Test transferMat done")
  }

  def transferVec = {
    var vec = DenseVector(1.0, 2.0, 3.0, 4.0, 5.0)
    Util.transferVec(vec, 2, 4)
    assert(vec(0) == 1.0 && vec(1) == 2.0 && vec(2) == 4.0 && vec(3) == 5.0 && vec(4) == 3.0)

    //reverse
    vec = DenseVector(1.0, 2.0, 3.0, 4.0, 5.0)
    Util.transferVec(vec, 4, 2)
    assert(vec(0) == 1.0 && vec(1) == 2.0 && vec(2) == 5.0 && vec(3) == 3.0 && vec(4) == 4.0)

    println ("Test transferVec done")
  }

  def distTest: Unit = {
    var p1 = new Point(3.0, 0.0, 0.0)
    var p2 = new Point(0.0, 0.0, 0.0)
    var dist = Util.dist(p1, p2)
    assert(dist == 3)

    p1 = new Point(5.0, 8.0, 0.0)
    p2 = new Point(2.0, 4.0, 0.0)
    dist = Util.dist(p1, p2)
    assert(dist == 5)

    println ("Test dist done")
  }

  def main(args: Array[String]): Unit = {
    transferMatTest
    transferVec
  }
}
