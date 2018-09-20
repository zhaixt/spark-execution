package com.zhaixt.basic

import scala.collection.mutable.ArrayBuffer

/**
  * Created by zhaixt on 2018/4/20.
  */
object BasicExample {
  val regex = "yue.+([\\d,]+)".r
  val number = "[\\d,]+".r

  def getDoubleFromString(option: Option[String]): Double = {
    if (option.isEmpty) return 0d
    else
      getDoubleFromString(option.get)
  }

  def getDoubleFromString(string: String): Double = {
    var double = 0d
    try {
      double = string.toDouble
    } catch {
      case _ =>
    }
    return double
  }

  def main(args: Array[String]): Unit = {
    /*
    * Seq
    * */
    val seq = Seq("xiaotongyue8","wangzhen","jiangyue40lei","Homeway,25,Male","XSDYMyue23","scalayue32om")
    val num =  seq.filter(t=>t.contains("a")).map(_.split(",")(0)).size
    System.out.println("num:"+num)



    seq.map(t=>regex.findFirstIn(t)).filter(_.isDefined).map(_.get.replace("e","t"))
        .map(t => getDoubleFromString(number.findFirstIn(t))).foreach(t=>println(t))

    /*
    * Map
    * */
    var numMap:Map[String,Int]  = Map("k1"->1,"k2"->2)
    numMap += ("k3"->3)
    numMap += ("k3"->2)
    numMap.filter(_._2 == 2).map(t=>println())


    //虽然 Scala 可以不定义变量的类型，不过为了清楚些，我还是
    // 把他显示的定义上了

    val myMap: Map[String, String] = Map("key1" -> "value")
    val value1: Option[String] = myMap.get("key1")
    val value2: Option[String] = myMap.get("key2")
    println()
    println(value1) // Some("value1")
    println(value2) // None

    /*
     * 定长数组Array
     */
    val ary1 = new Array[Int](5)
    val ary2 = new Array[String](5)

    val ary3 = Array("zhangsan","lisi")
    val ary4 = Array(1 to 10:_*)
    println("数组的元素："+ary1(0))
    ary1(0) =11
    println("改变后数组的元素："+ary1(0))
  }

  /*
   * 变长数组ArrayBuffer
   */
  var numArrayBuffer = new ArrayBuffer[Int]();

  // 使用+=操作符，可以添加一个元素，或者多个元素
  // 这个语法必须要谨记在心！因为spark源码里大量使用了这种集合操作语法！
  numArrayBuffer += 1
  numArrayBuffer += (2, 3, 4, 5)
  // 使用++=操作符，可以添加其他集合中的所有元素
  numArrayBuffer ++= Array(6, 7, 8, 9, 10)
  numArrayBuffer.insert(4,3)
  numArrayBuffer.remove(4)
  for(i<-0 until numArrayBuffer.length){
//    println(s"date:"+numArrayBuffer(i))
  }
}
