package com.zhaixt
import java.util.concurrent.ConcurrentHashMap
import java.util
/**
 * Created by zhaixiaotong on 2017-6-21.
 */
object scala_java_example {
  def main(args: Array[String]): Unit = {
    val hash = new ConcurrentHashMap[Int,Int]()
    hash.put(1,100)
    hash.put(2,200)
    println(hash)

    val hashmap = new util.HashMap[String,String]()
    hashmap.put("key1","value1")
    hashmap.put("key2","value2")
    println(hashmap)


    val javaClass = new MyJavaClass()
    val addResult = javaClass.adder(3,4)
    println(addResult)
  }
}
