package com.zhaixt
import org.apache.spark.{SparkConf, SparkContext}

/**
 * Hello world!
 *
 */
object App{

  def main(args: Array[String]) {
    val conf = new SparkConf().setAppName("MyTestApp").setMaster("local[1]")
    val sc = new SparkContext(conf)
    val x = sc.parallelize(List("a", "b", "a", "a", "b", "b", "b", "b"))
    val s = x.map((_, 1))
    val result = s.reduceByKey((pre, after) => pre + after)
    println(result.collect().toBuffer)

    val array= Array("a","b","a","d")
    val m = sc.parallelize(array)
    val n = m.map((_,1))
    val res = n.reduceByKey((p,a)=>p+a)
    println(res.collect().toBuffer)


    var arr = Array(1,2,3,4)
//    var a = arr.map(_=>_+_)

  }
}
