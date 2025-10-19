package MySpark

import org.apache.spark.SparkContext

import java.io.File

class LoadData {
  val hadoopHome = "E:\\hadoop\\hadoop-3.2.2"
  System.setProperty("hadoop.home.dir", hadoopHome)
  System.load(hadoopHome + "\\bin\\hadoop.dll")

  val sc = new SparkContext("local", "myapp", System.getenv("SPARK_HOME"))
  sc.setLogLevel("ERROR")

  val localPath = new File("").getAbsolutePath()
  val file = sc.textFile("G:\\Spark\\spark\\FB15K-237.2\\Release\\train.txt")
    .map(_.trim.split("\t").toList)
    .filter(_.length == 3)
}

object SparkPageRank extends LoadData{
  def main(args: Array[String]): Unit = {
    // 停留矩阵
    var data =  file
      .map(x => (x(0), x(2)))
      .groupByKey()
      .map(x => (x._1, (1.0, x._2.toList)))
    // (/m/05tfn1,(1.0,List(/m/0dgrmp, /m/02_j1w, /m/0dgrmp, /m/02nzb8, /m/02sdk9v, /m/02nzb8, /m/02_j1w)))

    for(i <- 1 to 100) {
      data = data.flatMap(x => x._2._2.map(y => (y, (x._2._1 / x._2._2.size, x._1))))
        .groupByKey()
        .mapValues(_.map(_._1).sum)
        .join(data)
        .map(x => (x._1, (x._2._1, x._2._2._2)))
      println("---------第 " + i + " 轮---------")
      data.take(3).foreach(println)
    }

    data.map(x => (x._2._1, x._1))
      .sortByKey(ascending = false)
      .saveAsTextFile("output/sparkPageRank100")
  }
}
