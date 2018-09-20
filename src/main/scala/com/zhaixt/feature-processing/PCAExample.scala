package com.zhaixt.feature_processing

import org.apache.spark.mllib.feature.PCA
import org.apache.spark.mllib.linalg.{Vector, Vectors}
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkContext, SparkConf}
/**
 * Created by zhaixiaotong on 2017-6-6.
 * http://blog.csdn.net/wangpei1949/article/details/53311642
 */
/*
 PCA（Principal Component Analysis）即主成分分析。正如其名，PCA可以找出特征中最主要的特征，把原来的n个特征用k(k < n)个特征代
替，去除噪音和冗余。PCA是特征提取、数据降维的常用方法。
MLlib中PCA的实现思路：
1)原始数据3行4列经过转换得到矩阵A3∗4
2)得到矩阵A3∗4的协方差矩阵B4∗4
3)得到协方差矩阵B4∗4的右特征向量
4)选取k(如k=2)个大的特征值对应的特征向量,得到矩阵C4∗2
5)对矩阵A3∗4降维得到A′3∗2=A3∗4*C4∗2
* */
object PCAExample {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("PCAExample").setMaster("local[8]")
    val sc = new SparkContext(conf)

    // 设置日志等级
    sc.setLogLevel("WARN")

    // 原始数据
    val arr = Array(
      Vectors.dense(4.0,1.0, 4.0, 5.0),
      Vectors.dense(2.0,3.0, 4.0, 5.0),
      Vectors.dense(4.0,0.0, 6.0, 7.0))
    val data: RDD[Vector] = sc.parallelize(arr)

    // PCA降维
    val pca: RDD[Vector] = new PCA(2).fit(data).transform(data)

    // 1)协方差矩阵A，通过debug可看到
    //  1.3333333333333321  -1.666666666666666  0.6666666666666643  0.6666666666666643
    //  -1.666666666666666  2.3333333333333335  -1.333333333333334  -1.333333333333334
    //  0.6666666666666643  -1.333333333333334  1.3333333333333286  1.3333333333333286
    //  0.6666666666666643  -1.333333333333334  1.3333333333333286  1.3333333333333286

    // 2)协方差的特征向量，通过debug可看到
    //    -0.4272585628633996   -0.588949815490069  -0.6859943405700341   0.0
    //    0.6495967555956428    0.3277740377539023  -0.6859943405700355   5.91829962568105E-17
    //    -0.44467638546448407  0.522351555472326   -0.17149858514251054  -0.7071067811865475
    //    -0.44467638546448407  0.522351555472326   -0.17149858514251054  0.7071067811865476

    // 3)取k=2个特征向量，形成新矩阵B
    //    -0.4272585628633996   -0.588949815490069
    //    0.6495967555956428    0.3277740377539023
    //    -0.44467638546448407  0.522351555472326
    //    -0.44467638546448407  0.522351555472326

    //  4)矩阵A*矩阵B达到降维目的
    //  [-5.061524965038313,2.6731387750445608]
    //  [-7.489827262491891,4.4347709591799624]
    //  [-2.9078143281202276,4.506586481532503]
    pca.foreach(println)
  }
}
