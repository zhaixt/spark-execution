package com.zhaixt

import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.mllib.util.MLUtils
import org.apache.spark.{SparkContext, SparkConf}
import org.junit._
import Assert._
import org.apache.spark.rdd.RDD
import org.apache.spark.mllib.linalg.{Vector, Vectors}
import org.apache.spark.mllib.linalg.distributed.RowMatrix
import org.apache.spark.mllib.linalg.distributed.{CoordinateMatrix, MatrixEntry}

@Test
class AppTest {

  @Test
  def testOK() = assertTrue(true)

  @Test
  def hel: Unit = {
    val someValue = true
    assert(someValue == true)
    println("===haha===")
  }

  @Test
  def row_matrix: Unit = {
    val conf = new SparkConf().setAppName("matrixTest").setMaster("local").set("spark.sql.warehouse.dir", "D:\\ProgrammingStudy\\spark-data")
    val sc = new SparkContext(conf)
    val dv1: Vector = Vectors.dense(1.0, 2.0, 3.0)
    val dv2: Vector = Vectors.dense(2.0, 3.0, 4.0)
    val rows: RDD[Vector] = sc.parallelize(Array(dv1, dv2))
    val mat: RowMatrix = new RowMatrix(rows)
    println("行数：" + mat.numRows())
    println("列数：" + mat.numCols())
    mat.rows.foreach(println)
    val summary = mat.computeColumnSummaryStatistics()
    /// 可以通过summary实例来获取矩阵的相关统计信息，例如行数
    println("行数:" + summary.count)
    // 最大向量
    println("最大向量" + summary.max)
    // 方差向量
    println("方差向量" + summary.variance)
    // 平均向量
    println("平均向量" + summary.mean)
    // L1范数向量
    println("L1范数向量" + summary.normL1)
  }

  /*
  * 坐标矩阵
  * 一个基于矩阵项构成的RDD的分布式矩阵。每一个矩阵项MatrixEntry都是一个三元组：(i: Long, j: Long, value: Double)，其中i是行索引，j是列索引，value是该位置的值。
  * 坐标矩阵一般在矩阵的两个维度都很大，且矩阵非常稀疏的时候使用。
  * */
  @Test
  def coordinate_matrix: Unit = {
    val conf = new SparkConf().setAppName("matrixTest").setMaster("local").set("spark.sql.warehouse.dir", "D:\\ProgrammingStudy\\spark-data")
    val sc = new SparkContext(conf)
    val ent1 = new MatrixEntry(0, 1, 0.5)
    val ent2 = new MatrixEntry(2, 2, 1.8)
    // 创建RDD[MatrixEntry]
    val entries: RDD[MatrixEntry] = sc.parallelize(Array(ent1, ent2))
    // 通过RDD[MatrixEntry]创建一个坐标矩阵
    val coordMat: CoordinateMatrix = new CoordinateMatrix(entries)
    //打印
    coordMat.entries.foreach(println)
    // 将coordMat进行转置
    val transMat: CoordinateMatrix = coordMat.transpose()
    println("转置：")
    transMat.entries.foreach(println)
    // 将坐标矩阵转换成一个索引行矩阵
    val indexedRowMatrix = transMat.toIndexedRowMatrix()
    println("索引行矩阵：")
    indexedRowMatrix.rows.foreach(println)
  }

  @Test
  def WordCount(): Unit = {
    val array = "I am a boy and you a b a c"
    //    array.flatMap(array.split(" ")
    //    array.flatMap(array.split(" ")).map((_, 1)).reduceByKey(_+_).collect().foreach(println)
//        val map = array.split(" ").map((_, 1)).reduceByKey(_+_).collect().foreach(println)

    //    val map = array.split(" ").map(word=>word,1){
    val map1 = array.split(" ").map(_*2).foreach(println)
    //      println(word)
    //    })
    val wordsMap = scala.collection.mutable.Map[String,Int]()
    array.split(" ").foreach(
      word => {
        if (wordsMap.contains(word)) {
          wordsMap(word) += 1
        } else {
          //        wordsMap.put(word,1)
          wordsMap += (word -> 1)
        }
      }
    )
    for ((key, value) <- wordsMap) {
      println(key+":"+value)
    }
  }

  @Test
  def WordCount2():Unit = {
    val conf = new SparkConf().setAppName("wordcount").setMaster("local[1]")
    val sc = new SparkContext(conf)
    val x = sc.parallelize(List("a", "boy", "a", "a", "b", "b", "b", "b"))
//    var array = Array("haha","hehe")
    val s = x.map((_,1))
    println("add tail map result:")
    var s4 = x.map(m=>m+"_tail").foreach(println)
    println("map result:")
    val s2 = x.map((_,1)).foreach(println)
    println("to string map result:")
    var s3 = x.map(_.toString).foreach(println)
    val wordCount = s.reduceByKey(_+_)
    println("reduce by key result 1:")
    wordCount.foreach(y=>{
      val (key,value) = y
      println(key+"="+value)
    })
    println("reduce by key result 2:")
    val result2 = s.reduceByKey((pre,after)=>pre+after)
    result2.foreach(y=>{
      val (key,value) = y
      println(key+"="+value)
    })
    val result3 = s.groupByKey().collect()
    println("group by key :")
    result3.foreach(y=>{
      val (key,value) = y
      println(key+"="+value)
    })
    val c = sc.parallelize(1 to 10)
    val res = c.reduce((x,y)=>x+y)
    println(res)
  }

  @Test
  def filter(): Unit = {
    val array = Array(1, 2, 3, 4, 5, 6, 7, 18, 9)
    array.map(2 * _).filter(_ > 6).foreach(println)
  }
  @Test
  def readFile(): Unit = {
    val conf = new SparkConf().setAppName("KMeansTest").setMaster("local").set("spark.sql.warehouse.dir", "D:\\ProgrammingStudy\\spark-data\\warehouse_dir")

    val sc = new SparkContext(conf)

    val data_path = "D:\\ProgrammingStudy\\spark-data\\kmeans4_training_data.csv"

    val rawTrainingData = sc.textFile(data_path)
    println("*****first result*****")
    println(rawTrainingData.first())
//    rawTrainingData.foreach(println)
    println("=====foreach result=====")
    rawTrainingData.foreach(line=>{line.split(",").filter(_.toDouble>10).map(_.trim).foreach(word=>{println(word)})})
    println("+++++map result+++++")
    rawTrainingData.map(line=>{line.split(",").map(_.trim).filter(_.toDouble>1000).foreach(word=>{println(word)})}).collect()

  }
  @Test
  def Map_FlatMap(): Unit = {


    flatMap1() //展开
    map1()

  }

  def flatMap1(): Unit = {
    val li = List(1, 2, 3)
    val res = li.flatMap(x => x match {
      case 3 => List('a', 'b')
      case _ => List(x * 2)
    })
    println(res)
  }

  def map1(): Unit = {
    val li = List(1, 2, 3)
    val res = li.map(x => x match {
      case 3 => List('a', 'b')
      case _ => x * 2
    })
    println(res)
  }
  @Test
  def spark_map(): Unit = {
    val conf = new SparkConf().setAppName("KMeansTest").setMaster("local").set("spark.sql.warehouse.dir", "D:\\ProgrammingStudy\\spark-data\\warehouse_dir")
    val sc = new SparkContext(conf)
    val data = sc.parallelize(List(1,2,3,4,5,6,7,8,9))
    val dataInt = sc.makeRDD(List(1,2,3,4,5,6,7,8,9))
    val res = dataInt.map(x => x * 2).collect().mkString(",")
    val res2 = dataInt.map(x=>println(x*2)).collect()
    println("map result")
    println(res2)
    println("foreach result")
    dataInt.foreach(x=>{println(x*2)})
    val rddInt:RDD[Int] = sc.makeRDD(List(1,2,3,4,5,6,2,5,1))
    println(rddInt.map(x => x + 1).collect().mkString(","))


  }
  @Test
  def set_test():Unit = {
    var jetSet = Set("Boeing", "Airbus")
    jetSet += "Lear"
    println(jetSet.contains("Cessna"))
  }
  //    @Test
  //    def testKO() = assertTrue(false)
 /*
 * 向量测试
 * */
  @Test
  def vector_test():Unit = {
    //密集向量
    val vd = Vectors.dense(2, 5, 8)
    println(vd(1))
    println(vd)
    //稀疏向量
    //向量个数，序号，value
    val vs = Vectors.sparse(4,Array(0,1,2,3),Array(9,3,5,7))
    println(vs(0))//序号访问
    println(vs)
    val vs2 = Vectors.sparse(4,Array(0,2,1,3),Array(9,3,5,7))
    println(vs2(2))
    println(vs2)
  }


  @Test
  def LabelPoint_test():Unit = {
    //=======1:直接静态生成向量标签=======
    //密集型向量测试
    val vd: Vector = Vectors.dense(1, 2, 3)
    //建立标记点内容数据
    val vl1 = LabeledPoint(1, vd)
    println("val1:" + vl1)
    //标记点和内容属性(静态类)
    println("vl1 label:" + vl1.label + ",features:" + vl1.features)

    val vl2 = LabeledPoint(0, Vectors.sparse(3, Array(1, 2), Array(10, 100)))
    println("val2:" + vl2)
    println("vl2 label:" + vl2.label + ",features:" + vl2.features)


    //=======2:文件API生成=======
    val conf = new SparkConf()
      .setMaster("local")
      .setAppName("LabeledPointLearn")

    val sc = new SparkContext(conf)
    val mu = MLUtils.loadLibSVMFile(sc, "D:\\ProgrammingStudy\\spark-data\\labeledPointTestData.txt")
    mu.foreach(println)

    //labeledPointTestData.txt
    //        1 1:2 2:3 3:4
    //        2 1:1 2:2 3:3
    //        1 1:1 2:3 3:3
    //        1 1:3 2:1 3:3
    //结果
    //    (1.0,(3,[0,1,2],[2.0,3.0,4.0]))
    //    (2.0,(3,[0,1,2],[1.0,2.0,3.0]))
    //    (1.0,(3,[0,1,2],[1.0,3.0,3.0]))
    //    (1.0,(3,[0,1,2],[3.0,1.0,3.0]))


  }
}


