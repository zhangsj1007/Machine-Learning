package com.chris.map.data_mining.util

import breeze.linalg._
import com.chris.map.data_mining.data.Point

object Util {

  /*
  * 将一个矩阵的某两行元素对换
  * @param mat
  * @param row1
  * @param row2
  * @return
  * */
  def swapRow (mat : DenseMatrix[Double], row1 : Int, row2 : Int) = {
    val row = mat.rows
    val col = mat.cols

    val ignore = row1 > row - 1 || row2 > row - 1

    if (!ignore){
      for (i <- 0 until col){
        val v = mat(row1, i)
        mat(row1, i) = mat(row2, i)
        mat(row2, i) = v
      }
    }

  }

  /*
  * 将一个矩阵的某两列元素对换
  * @param mat
  * @param col1
  * @param col2
  * @return
  * */
  def swapCol (mat : DenseMatrix[Double], col1 : Int, col2 : Int)={
    val row = mat.rows
    val col = mat.cols

    val ignore = col1 > col - 1 || col2 > col - 1

    if (!ignore){
      for (i <- 0 until row){
        val v = mat(i, col1)
        mat(i, col1) = mat(i, col2)
        mat(i, col2) = v
      }
    }
  }

  /*
  *将一个方阵的行从start index位置逐层移到 end index位置，
  * 再将列从start index位置逐层移到end index位置
  * @param vec
  * @param sIdx
  * @param eIdx
  * @return
  * */
  def transferMat (mat : DenseMatrix[Double], sIdx : Int, eIdx : Int) = {

    val row = mat.rows
    val col = mat.cols

    val ignore = (row != col) || (sIdx > row) || (eIdx > row) || (sIdx == eIdx)

    if (!ignore){

      var i = sIdx

      if (sIdx < eIdx){
        while (i < eIdx){
          swapRow(mat, i, i+1)
          i += 1
        }
        i = sIdx
        while (i < eIdx){
          swapCol(mat, i, i+1)
          i += 1
        }
      }
      else{
        while (i > eIdx){
          swapRow(mat, i, i-1)
          i -= 1
        }
        i = sIdx
        while(i > eIdx){
          swapCol(mat, i, i-1)
          i -= 1
        }
      }
    }
  }

  /*
  * 将一个向量的某两元素对换
  * @param vec
  * @param idx1
  * @param idx2
  * @return
  * */
  def swapEle (vec : DenseVector[Double], idx1 : Int, idx2 : Int) = {
    val length = vec.length

    val ignore = idx1 > length - 1 || idx2 > length - 1

    if (!ignore){
      val t = vec(idx1)
      vec(idx1) = vec(idx2)
      vec(idx2) = t
    }
  }

  /*
  *将一个向量从start index位置逐层移到 end index位置
  * @param vec
  * @param sIdx
  * @param eIdx
  * @return
  * */
  def transferVec (vec : DenseVector[Double], sIdx : Int, eIdx : Int)={

    val length = vec.length

    val ignore = (sIdx > length - 1) || (eIdx > length - 1) || (sIdx == eIdx)

    if (!ignore) {
      var i = sIdx

      if (sIdx < eIdx){
        while (i < eIdx){
          swapEle(vec, i, i+1)
          i += 1
        }
      }else{
        while (i > eIdx){
          swapEle(vec, i, i-1)
          i -= 1
        }
      }
    }
  }

  /*
  * 获得一个矩阵的子矩阵
  * @param mat
  * @param sRowIdx_
  * @param eRowIdx_
  * @param sColIdx_
  * @param eColIdx_
  * @return subMat
  * */
  def getSubMat (mat : DenseMatrix[Double], sRowIdx_ : Int, eRowIdx_ : Int,
                 sColIdx_ : Int, eColIdx_ : Int) : DenseMatrix[Double] = {
    val sRowIdx = if (sRowIdx_ > mat.rows - 1) mat.rows -1 else sRowIdx_
    val eRowIdx = if (eRowIdx_ > mat.rows - 1) mat.rows -1 else eRowIdx_
    val sColIdx = if (sColIdx_ > mat.cols - 1) mat.cols -1 else sColIdx_
    val eColIdx = if (eColIdx_ > mat.cols - 1) mat.cols -1 else eColIdx_

    val res =
      if (eRowIdx < sRowIdx || eColIdx < sColIdx)
        null
      else {
        val subMat = DenseMatrix.zeros[Double](eRowIdx - sRowIdx + 1, eColIdx - sColIdx + 1)

        for (i <- sRowIdx to eRowIdx){
          for (j <- sColIdx to eColIdx)
            subMat(i - sRowIdx, j - sColIdx) = mat(i, j)
        }

        subMat
      }

    res
  }


  def pow_(mat : DenseMatrix[Double], e : Int) = {
    val res = if (e <= 0) {
      DenseMatrix.eye[Double](mat.rows)
    }else if (e == 1){
      mat.copy
    }else{
      var res_ = mat.copy
      for (i <- 1 until e){
        res_ = res_ * res_
      }
      res_
    }
    res
  }

  /*计算欧式距离
  * @param p
  * @param q
  * @return distance
  * */
  def dist (p : Point, q : Point)={
    math.sqrt(math.pow(p.x - q.x, 2) + math.pow(p.y - q.y, 2))
  }

  /*
  * 计算一个list points的均值中心点
  * @param points
  * @return center
  * */
  def avgPoint (points : Array[Point]) = {
    var x = 0.0
    var y = 0.0
    val num = points.length
    val clusterId = points(0).clusterId

    for (p <- points) {
      x = x + p.x
      y = y + p.y
    }

    x = x / num
    y = y / num

    val center = new Point(x, y)
    center.clusterId = clusterId
    center
  }

}
