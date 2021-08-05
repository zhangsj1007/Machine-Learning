package com.chris.map.data_mining.boot

//java
import java.io.{BufferedWriter, File, FileWriter}
import java.text.SimpleDateFormat
import java.util.Date

import com.chris.map.data_mining.mpr.TransProbability

//spark
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.sql.{SQLContext, SparkSession}
import org.apache.spark.sql.functions._
import org.apache.spark.sql.catalyst.expressions._
import org.apache.spark.sql.Row

//scala
import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.Map

//rtree
import com.vividsolutions.jts.index.strtree.STRtree
import com.vividsolutions.jts.geom.Envelope
import com.vividsolutions.jts.geom.Coordinate

//mpr
import com.chris.map.data_mining.data.Point
import com.chris.map.data_mining.data.Trip
import com.chris.map.data_mining.mpr.Coherence


object Boot extends App{
  val conf = new SparkConf().setMaster("local").setAppName("Coherence")
  val sc = new SparkContext(conf)
  sc.setLogLevel("ERROR")

  val spark = SparkSession
    .builder()
    .config(conf)
    .getOrCreate()


  val sqlContext = spark.sqlContext

  //val path = "/Users/songjianzhang/Data/trajectory/taxi/beijing_2019-02-18"
  //val path = "/Users/songjianzhang/Data/trajectory/beijing-2ring-20190212"
  val path = "/Users/songjianzhang/Data/trajectory/taxi_mm/beijing_2019-04-10"

  val data = sqlContext.read.json(path)

  data.printSchema()

  import sqlContext.implicits._

//  val points_ = data.map(row => Point(row.getAs[Double]("x"),
//                                      row.getAs[Double]("y"),
//                                      row.getAs[Double]("a"),
//                                      row.getAs[Long]("t"),
//                                      row.getAs[String]("tripid"))).collect()

//  val points_ = data.map(row => Point(row.getAs[Double]("from"),
//    row.getAs[Double]("from.prjY"),
//    row.getAs[Double]("from.modifyAngle"),
//    row.getAs[Long]("from.gpsTm"),
//    row.getAs[String]("from.tripID"))).col\lect()

//  val points_ = data.map(row1 => row1.getAs[Seq[Row]]("from").map(row2 => Point(row2.getAs[Double]("prjX"),
//                                                                            row2.getAs[Double]("prjY"),
//                                                                            row2.getAs[Double]("modifyAngle"),
//                                                                            row2.getAs[Long]("gpsTm"),
//                                                                            row2.getAs[String]("tripID")))).collect

  val points_ = data.map (row => {
    val from = row.getAs[Row]("from")
    val x = from.getAs[Double]("prjX")
    val y = from.getAs[Double]("prjY")
    val a = from.getAs[Double]("modifyAngle")
    val ts = from.getAs[Long]("gpsTm")
    val tripId = from.getAs[String]("tripID")
    val status = from.getAs[String]("status")

    val p = if (status != null && status.equals("MatchSuccess"))
      new Point(x, y, a, ts, tripId)
    else
      null
    p

    //new Point(x, y, a, ts, tripId)

  } ).filter(_ != null).collect

  println(points_.length)

  val points = new ArrayBuffer[Point]

  //12951625,4823562.5;12960984.375,4831015.625
//  val x1 = 12950671.875
//  val x2 = 12961125
//  val y1 = 4822093.75
//  val y2 = 4831343.75


  //rtree
  val pointRtree = new STRtree

  var count = 0
  import scala.util.control.Breaks._
  breakable {
    for (p <- points_) {
      //    if (p.x > x1 && p.y > y1 &&
      //      p.x < x2 && p.y < y2){
      //      p.id = count
      //      points.append(p)
      //      count += 1
      //    }
      if (p != null) {
        p.id = count
        points.append(p)
        pointRtree.insert(new Envelope(new Coordinate(p.x, p.y)), count)
        count += 1
      }

      if (count == 200000){
        break()
      }

    }
  }

  //val pointsSeg = points.take(200000).toArray

//  for (p <- pointsSeg){
//    if (p != null) {
//      p.id = count
//      points.append(p)
//      pointRtree.insert(new Envelope(new Coordinate(p.x, p.y)), count)
//      count += 1
//    }
//  }

  //println(pointsSeg.length)




  //val conherence = new Coherence(points.toArray)
  val conherence = new Coherence(points.toArray, pointRtree)



  val start = System.currentTimeMillis() / 1000
  conherence.init
  val clusters = conherence.cluster

  val end = System.currentTimeMillis() / 1000
  val duration = end - start
  println(s"Operate time = $duration")


  val tripMap = Map[String,Trip]()

//  for (p <- points) {
//    if (tripMap.contains(p.tripid))
//      tripMap(p.tripid).addPoint(p)
//    else{
//      val trip = new Trip(p.tripid)
//      tripMap(p.tripid) = trip
//    }
//  }

  //按照time stamp排序
  for ((tripId, trip) <- tripMap)
    trip.sort

  val trips = tripMap.values.toArray

//  val transProbab = new TransProbability (clusters, trips)
//
//  val PArray = transProbab.getPArray()
//  val V = transProbab.getVArray(PArray)


  val sdf = new SimpleDateFormat("yyyyMMddHHmmss")
  val date = sdf.format(new Date())

  val out = "/Users/songjianzhang/Public/Idea/data/Coherence/points-" + date + ".txt"
  val file = new File(out)
  if (file.exists)
    file.delete
  val fileWriter = new FileWriter(file)
  val bwfile = new BufferedWriter(fileWriter)
  bwfile.write("x,y,a,id,clusterId\n")
  //for (p <- pointsSeg){
  for (p <- points){
    val line = p.x.toString + "," + p.y.toString + "," + p.a.toString + "," + p.id.toString + "," + p.clusterId.toString + "\n"
    bwfile.write(line)
  }
  bwfile.flush()
  bwfile.close()
  fileWriter.close
}
