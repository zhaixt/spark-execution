package com.zhaixt.als

import org.apache.spark.mllib.recommendation.{ALS, Rating}
import org.apache.spark.{SparkConf, SparkContext}

/**
  * Created by zhaixiaotong on 2017-5-4.
  * http://www.tuicool.com/articles/fANvieZ
  *  ALS最小二乘法
  */
object AlsCFTest {
   def main(args: Array[String]): Unit = {
     val conf = new SparkConf().setAppName("CollaborationFilterTest").setMaster("local").set("spark.sql.warehouse.dir", "D:\\ProgrammingStudy\\spark-data")
     val sc = new SparkContext(conf)
     //第一列位用户编号，第二列位产品编号，第三列的评分Rating为Double类型
     val data = sc.textFile("D:\\ProgrammingStudy\\spark-data\\als\\test.dat")
     /**
      * Product ratings are on a scale of 1-5:
      * 5: Must see
      * 4: Will enjoy
      * 3: It's okay
      * 2: Fairly bad
      * 1: Awful
      */
     val ratings = data.map(_.split(',') match { case Array(user, product, rate) =>
       Rating(user.toInt, product.toInt, rate.toDouble)
     })

     //使用ALS训练数据建立推荐模型
     val rank = 10
     val numIterations = 5//todo 源程序是20
     val model = ALS.train(ratings, rank, numIterations, 0.01)
//或者
//    val model = new ALS()
//      .setRank(params.rank)
//      .setIterations(params.numIterations)
//      .setLambda(params.lambda)
//      .setImplicitPrefs(params.implicitPrefs)
//      .setUserBlocks(params.numUserBlocks)
//      .setProductBlocks(params.numProductBlocks)
//      .run(training)
//         //从 ratings 中获得只包含用户和商品的数据集
     val usersProducts = ratings.map { case Rating(user, product, rate) =>
       (user, product)
     }

     //使用推荐模型对用户商品进行预测评分，得到预测评分的数据集
     val predictions =
       model.predict(usersProducts).map { case Rating(user, product, rate) =>
         ((user, product), rate)
       }
//     model.productFeatures.lookup()

     //将真实评分数据集与预测评分数据集进行合并
     val ratesAndPreds = ratings.map { case Rating(user, product, rate) =>
       ((user, product), rate)
     }.join(predictions).sortByKey()  //ascending or descending

     //然后计算均方差，注意这里没有调用 math.sqrt方法
     val MSE = ratesAndPreds.map { case ((user, product), (r1, r2)) =>
       val err = (r1 - r2)
       err * err
     }.mean()

     //打印出均方差值
     println("Mean Squared Error = " + MSE)
     //Mean Squared Error = 1.37797097094789E-5

//     todo 上面的例子只是对训练集并进行了评分，我们还可以进一步的给用户推荐商品。以 Scala 程序为例，在原来代码基础上继续执行下面代码：

     //为每个用户进行推荐，推荐的结果可以以用户id为key，结果为value存入redis或者hbase中
     val users=data.map(_.split(",") match {
       case Array(user, product, rate) => (user)
     }).distinct().collect()
     //users: Array[String] = Array(4, 2, 3, 1)

     users.foreach(
       user => {
         //依次为用户推荐商品
         val rs = model.recommendProducts(user.toInt, numIterations)
         var value = ""
         var key = 0

         //拼接推荐结果
         rs.foreach(r => {
           key = r.user
           value = value + r.product + ":" + r.rating + ","
         })

         println("recommend result:key:"+key.toString+"   " +",value:"+ value)
       }
     )
     //4   4:4.9948551991729,2:4.9948551991729,3:1.0007160894300133,1:1.0007160894300133,
     //2   1:4.994747095003154,3:4.994747095003154,2:1.0007376098628127,4:1.0007376098628127,
     //3   2:4.9948551991729,4:4.9948551991729,3:1.0007160894300133,1:1.0007160894300133,
     //1   3:4.994747095003154,1:4.994747095003154,2:1.0007376098628127,4:1.0007376098628127,



     //todo 上面的代码调用 model.recommendProducts 方法分别对用户进行推荐，其实在之前的代码中已经计算出了预测的评分，我们可以通过 predictions 或者 ratesAndPreds 来得到最后的推荐结果：

     //对预测结果按预测的评分排序
     predictions.collect.sortBy(_._2)
     //Array[((Int, Int), Double)] = Array(((4,1),1.0007160894300133), ((3,1),1.0007160894300133), ((4,3),1.0007160894300133), ((3,3),1.0007160894300133), ((1,4),1.0007376098628127), ((2,4),1.0007376098628127), ((1,2),1.0007376098628127), ((2,2),1.0007376098628127), ((1,1),4.994747095003154), ((2,1),4.994747095003154), ((1,3),4.994747095003154), ((2,3),4.994747095003154), ((4,4),4.9948551991729), ((3,4),4.9948551991729), ((4,2),4.9948551991729), ((3,2),4.9948551991729))

     //对预测结果按用户进行分组，然后合并推荐结果，这部分代码待修正
     predictions.map{ case ((user, product), rate) => (user, (product,rate) )}.groupByKey.collect

     //格式化测试评分和实际评分的结果
     val formatedRatesAndPreds = ratesAndPreds.map {
       case ((user, product), (rate, pred)) => user + "," + product + "," + rate + "," + pred
     }
     //Array(2,1,5.0,4.994747095003154, 4,4,5.0,4.9948551991729, 4,2,5.0,4.9948551991729, 4,1,1.0,1.0007160894300133, 3,4,5.0,4.9948551991729, 1,4,1.0,1.0007376098628127, 3,1,1.0,1.0007160894300133, 2,3,5.0,4.994747095003154, 1,2,1.0,1.0007376098628127, 1,1,5.0,4.994747095003154, 2,2,1.0,1.0007376098628127, 2,4,1.0,1.0007376098628127, 3,2,5.0,4.9948551991729, 3,3,1.0,1.0007160894300133, 4,3,1.0,1.0007160894300133, 1,3,5.0,4.994747095003154)

   }
 }
