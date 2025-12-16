package MySpark

import com.alibaba.fastjson.JSON
import org.ansj.splitWord.analysis.ToAnalysis
import org.apache.spark.SparkContext

import java.io.File

class Answer(var qid: Int, var title: String, var desc: String,
             var topic: String, var star: Int, var content: String,
             var answer_id: String, var answerer_tags: String) {
  override def toString() = {
    qid + ":" + title + ":" + answer_id
  }
}

class SparkDataSource {
  val hadoopHome = "E:\\hadoop\\hadoop-3.2.2"
  System.setProperty("hadoop.home.dir", hadoopHome)
  System.load(hadoopHome + "\\bin\\hadoop.dll")

  val sc = new SparkContext("local", "myapp", System.getenv("SPARK_HOME"))
  sc.setLogLevel("ERROR")

  val localPath = new File("").getAbsolutePath
  val data = sc.textFile(localPath + "/input/webtext2019zh/web_text_zh_train.json")
    .map(JSON.parseObject)
    .map(x => new Answer(
      x.getIntValue("qid"),
      x.getString("title"),
      x.getString("desc"),
      x.getString("topic"),
      x.getIntValue("star"),
      x.getString("content"),
      x.getString("answer_id"),
      x.getString("answerer_tags")
    ))
  val pos_dict = sc.textFile("input/dict/正面词.dict").collect().toSet
  val neg_dict = sc.textFile("input/dict/负面词.dict").collect().toSet
}

//一.统计问题标题的平均长度
object TitleAvgLen extends SparkDataSource {
  def main(args: Array[String]): Unit = {
    println("刘正专用作业")
    val len = data
      .map(x => x.title.length)
    println(len.sum().toFloat / len.count())
  }
}

//二. 统计topic中出现的最热的词
object TopicTopHOtWord extends SparkDataSource {
  def main(args: Array[String]): Unit = {
    println("刘正作业专业")
    import scala.collection.JavaConverters._
    data
      .map(x => x.topic)
      .flatMap(x => ToAnalysis.parse(x).getTerms.asScala.map(_.getName))
      .groupBy(x => x)
      .mapValues(_.size)
      .sortBy(_._2, ascending = false)
      .take(10)
      .foreach(println)
  }
}

//三. 请挖掘问题标题长度和回答数量之间的关系，问题长度多长的时候， 能够得到最多的回答
object TitleLenAndContentCount extends SparkDataSource {
  def main(args: Array[String]): Unit = {
    println("刘正专用作业")
    var d = data.map(x => (x.title.length, x.content))
    (d ++ d.map(x => (x._1 - 1, x._2)) ++ d.map(x => (x._1 + 1, x._2)))
      .groupBy(_._1)
      .filter(x => x._2.size >= 3)
      .mapValues(_.size)
      .sortBy(_._2, ascending = false)
      .take(20)
      .foreach(println)
  }
}

//四. 对用户的个人标签进行分词， 统计哪些分词会导致用户更正面， 哪些词会导致用户更负面
object PosTopic extends SparkDataSource {
  def main(args: Array[String]): Unit = {
    import scala.collection.JavaConverters._
    val md = data
      .map(x => x.answerer_tags)
      .flatMap(x => ToAnalysis.parse(x).getTerms.asScala.map(_.getName))
      .map(x => (x, {
        if (pos_dict.contains(x)) 1
        else if (neg_dict.contains(x)) -1
        else 0
      }))
      .groupByKey()
      .mapValues(_.sum)
    println("刘正作业专用")
    println("最正面：", md.sortBy(_._2, ascending = false).toJavaRDD().take(20))
    println("最负面：", md.sortBy(_._2).toJavaRDD().take(20))
  }
}

//回答者身份标签(分词)与回答受欢迎程度分析
object test1 extends SparkDataSource {
  def main(args: Array[String]): Unit = {
    import scala.collection.JavaConverters._
    println("刘正作业专用")
    val d = data
      .map(x => (x.answerer_tags, x.star))
      .flatMap(x => ToAnalysis.parse(x._1).getTerms.asScala.map(_.getName).map(y => (y, x._2)))
      .filter(_._1.length > 1)
      .groupByKey()
      .filter(_._2.size >= 3)
      .map(x => (x._1, {
        (x._2.sum - x._2.max - x._2.min).toFloat / (x._2.size - 2)
      }))
      .sortBy(_._2, ascending = false)
      .take(20)
      .toList
    val d2 = d.map(_._2).toArray
    val result = new Array[Float](d2.length)
    for (i <- Range(0, d2.length)) {
      if (i == 0) result(i) = d2(i) + d2(i + 1)
      else if (i == d2.length - 1) result(i) = d2(i) + d2(i - 1)
      else result(i) = d2(i) + d2(i - 1) + d2(i + 1)
    }
    d.map(_._1).zip(result)
      .sortBy(_._2).reverse
      .take(20)
      .foreach(println)
  }
}
object test10{
  def main(args: Array[String]): Unit = {
    var l = List(("a", 1), ("b", 2), ("b", 2), ("b", 2), ("b", 2))
    var l2 = l.map(_._2).toArray
    var result = new Array[Int](l2.length)
    for (i <- Range(1, l.length-1)) {
      result(i) = l2(i) + l2(i - 1) + l2(i + 1)
    }
    println(result.toList)
    println(l.map(_._1).zip(result))
  }
}

