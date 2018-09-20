package com.zhaixt.classification_regression

import org.apache.spark.{SparkConf, SparkContext}

import scala.collection.mutable.{HashMap, SortedSet}
/**
* AUTHOR: zhaixiaotong
* * http://blog.csdn.net/lujinhong2/article/details/60134181
 * 原理是找到距离其最近的（欧氏距离）k个点，统计哪个分类做多就属于哪一类，是惰性学习法，Bayes、决策树，SVM属于急切学习法
* CREATED ON: 17/3/2 14:06
* DESCRIPTION: KNN算法的示例。分类算法。一个应用场景是模式识别，求其欧氏距离
* 示例数据如下：
* $ cat knn_training_data.txt
ID1 172 60 1
ID2 163 50 0
ID3 188 70 1
ID4 155 40 0
$ cat knn_to_do_data.txt
ID5 164 54
ID6 199 82
ID7 172 50
* 输出如下：
* $ hadoop fs -cat /tmp/ljhn1829/ml/knn/result/\*
ID5 0
ID6 1
ID7 0
*
*/
object SparkKNN {
  val TRAINING_DATA_PATH = "D:\\ProgrammingStudy\\spark-data\\KNN\\knn_training_data.txt";
  val TO_DO_DATA_PATH = "D:\\ProgrammingStudy\\spark-data\\KNN\\knn_to_do_data.txt"
  val OUTPUT_PATH = "D:\\ProgrammingStudy\\spark-data\\KNN\\result"
  val SEPARATOR = " "
  val K = 3
  val MALE_LABEL = "1"
  val FEMALE_LABLE = "0"

  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("KMeansTest").setMaster("local").set("spark.sql.warehouse.dir", "D:\\ProgrammingStudy\\spark-data\\warehouse_dir")

    val sc = new SparkContext(conf)
//    val sc = new SparkContext()
//    val traingDataSetBroadcast = sc.broadcast(sc.textFile(TRAINING_DATA_PATH).collect().toSet);
//    sc.textFile(TO_DO_DATA_PATH).map(line => classify(line, traingDataSetBroadcast.value)).saveAsTextFile(OUTPUT_PATH)

    val trainingDataSet = sc.textFile(TRAINING_DATA_PATH).collect().toSet;
//    sc.textFile(TO_DO_DATA_PATH).map(line=>{line.split(" ").map(_.trim).foreach(word=>{println(word)})}).collect()
//    sc.textFile(TO_DO_DATA_PATH).map(line=>print(line)).collect()
    sc.textFile(TO_DO_DATA_PATH).map(line => classify(line, trainingDataSet)).collect()
  }
  def print(line:String):Unit = {
    line.split(" ").map(_.trim).foreach(word=>{println(word)})
  }

  def classify(line: String, trainingDataSet: Set[String]): String = {
    //记录与待分类元组最小的3个距离
    var minKDistanceSet = SortedSet[Double]()
    //记录与待分类元组最小的3个距离及其对应的分类。
    var minKDistanceMap = HashMap[Double, Int]()
    for (i <- 1 to K) {
      minKDistanceSet += Double.MaxValue
    }

    val info = line.trim.split(SEPARATOR)
    val id = info(0)
    val height = info(1).toDouble
    val weight = info(2).toDouble
    for (trainSampleItem <- trainingDataSet) {
      val sampleInfo = trainSampleItem.trim().split(SEPARATOR)
      val distance = Math.sqrt(Math.pow((height - sampleInfo(1).toDouble), 2) + Math.pow((weight - sampleInfo(2).toDouble), 2))
      if (distance < minKDistanceSet.lastKey) {
        minKDistanceSet -= minKDistanceSet.lastKey
        minKDistanceSet += distance
        minKDistanceMap += ((distance, sampleInfo(3).toInt))
        if (minKDistanceMap.size >= 3) {
          minKDistanceMap -= minKDistanceSet.lastKey
        }
      }

    }
    //根据距离最近的3个样本分类，得出最终分类结果。
    var count = 0
    for (entry <- minKDistanceMap) {
      count += entry._2
    }
    var result = FEMALE_LABLE
    if (count > K / 2) {
      result = MALE_LABEL
    }
    println(id + SEPARATOR + result)
    return id + SEPARATOR + result
  }

}
