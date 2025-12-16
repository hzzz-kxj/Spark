import com.alibaba.fastjson.{JSON, JSONObject}
import org.ansj.splitWord.analysis.BaseAnalysis


import scala.io.Source
import scala.util.matching.Regex

class Answer(var qid: Int, var title: String, var desc: String,
             var topic: String, var star: Int, var content: String,
             var answer_id: String, var answerer_tags: String) {
  override def toString() = {
    qid + ":" + title + ":" + answer_id
  }
}

class dataSource {
  val data = Source.fromFile("input/webtext2019zh/web_text_zh_test.json", "UTF-8")
    .getLines()
    .toList
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

  val ansArr = data
    .sortBy(_.qid)
    .toArray

  val qMap = data
    .map(x => (x.qid, x))
    .toMap

  val aMap = data
    .map(x => (x.answer_id, x))
    .toMap

}

object dataAnalysis extends dataSource {
  def main(args: Array[String]): Unit = {

    ansArr.take(10).foreach(println)
    println()
    qMap.take(10).foreach(println)
    println()
    aMap.take(10).foreach(println)
  }
}

//热点问题
object TopTitle extends dataSource {
  def main(args: Array[String]): Unit = {

    data
      .groupBy(_.qid)
      .map(x => (x._1, x._2.length, x._2(0).title))
      .toList
      .sortBy(_._2)
      .reverse.take(20)
      .foreach(println)
  }
}

//热点标签
object TopTags extends dataSource {
  def main(args: Array[String]): Unit = {

    val pattern = new Regex("[\\u4E00-\\u9FA5]+")
    data
      .map(_.answerer_tags)
      .filter(x => x != null)
      .flatMap(_.trim.split(" ").toList)
      .groupBy(x => x)
      .mapValues(_.length)
      .toList
      .filter(_._2 > 1)
      .filter(x => pattern.findFirstIn(x._1).size > 0)
      .sortBy(_._2)
      .reverse
      .take(20)
      .foreach(println)

  }
}

//高赞用户
object TopStar extends dataSource {
  def main(args: Array[String]): Unit = {

    data
      .map(x => (x.answerer_tags, x.star))
      .groupBy(_._1)
      .map(x => (x._1, x._2.map(_._2).sum))
      .toList
      .sortBy(_._2)
      .reverse
      .take(10)
      .foreach(println)
  }
}

//高频词
object TopWord extends dataSource{
  def main(args: Array[String]): Unit = {
    import scala.collection.JavaConverters._
    data
      .map(_.content)
      .flatMap(BaseAnalysis.parse(_).getTerms.asScala.map(_.getName))
      .groupBy(x => x)
      .mapValues(_.length)
      .toList
      .filter(_._1.length > 1)
      .sortBy(_._2).reverse.take(20)
      .foreach(println)
  }
}

//正面词负面词
object PosAndNeg extends dataSource{
  def main(args: Array[String]): Unit = {

    val pos_dict = Source
      .fromFile("input/dict/正面词.dict")
      .getLines()
      .toList

    val neg_dict = Source
      .fromFile("input/dict/负面词.dict")
      .getLines()
      .toList
    println(pos_dict.take(10))
    println()
    println(neg_dict.take(10))

    import scala.collection.JavaConverters._

    val answer = data
      .map(x => (x.answer_id, x.content))
      .map(x => (x._1, BaseAnalysis.parse(x._2).getTerms.asScala.map(_.getName)))
      .map(x => (x._1, x._2.map(a => {if(pos_dict.contains(a)) 1 else if (neg_dict.contains(a)) -1 else 0})))
      .map(x => (x._1, x._2.sum))
      .sortBy(_._2)
    println("最正面的答案：", answer.takeRight(3).map(x => aMap.get(x._1).toList))
    println("最负面的答案：", answer.take(3).map(x => aMap.get(x._1).toList))
    println("正面回答数量：", answer.count(_._2 > 0))
    println("负面回答数量：", answer.count(_._2 < 0))
    println("中立回答数量：", answer.count(_._2 == 0))
  }
}

object Test1  {
  def main(args: Array[String]): Unit = {

    def hashKey(x: JSONObject, k: String): Int = {
      if (x.containsKey(k)) 1 else 0
    }

    val full_dim = Source.fromFile("input/webtext2019zh/web_text_zh_test.json", "UTF-8")
      .getLines()
      .toList
      .map(JSON.parseObject)
      .map(x => (
        hashKey(x, "answerer_tags"),
        hashKey(x, "star"),
        hashKey(x, "topic"),
        hashKey(x, "title"),
        hashKey(x, "qid"),
        hashKey(x, "answer_id"),
        hashKey(x, "content"),
        hashKey(x, "desc")))
      .reduce((a, b) => (a._1 + b._1, a._2 + b._2, a._3 + b._3,
        a._4 + b._4, a._5 + b._5, a._6 + b._6, a._7 + b._7, a._8 + b._8))
    println(full_dim)
  }
}

//问题标题的平均长度
object TitleAvgLength {
  def main(args: Array[String]): Unit = {

    val titleLen =
    new dataSource()
      .data
      .map(_.title.length)
      println("问题标题的平均长度： " + titleLen.sum.toFloat / titleLen.length)
  }
}

//请挖掘问题标题长度和回答数量之间的关系，问题长度多长时，能够得到做多的回答
object  obj1{
  def main(args: Array[String]): Unit = {

    new dataSource()
      .data
      .map(x => (x.qid, x.title.length, x.content))
      .groupBy(_._1)
      .map(x => (x._2.map(_._2), x._2.length))
      .map(x => (x._1(0),x._2))
      .groupBy(_._1)
      .toList
      .sortBy(x=> x._2.values)
      .reverse
      .take(10)
      .foreach(println)
  }
}

//请在topic字段下进行统计最热的十个topic
object TopicTop10 {
  def main(args: Array[String]): Unit = {

    new dataSource()
      .data
      .map(x => x.topic)
      .groupBy(x => x)
      .toList
      .map(x => (x._1, x._2.length))
      .sortBy(_._2).reverse
      .take(10)
      .foreach(println)
  }
}

//敏感问题
object sensitiveQuestion {
  def main(args: Array[String]): Unit = {

    import scala.collection.JavaConverters._

    val sen_dict = Source
      .fromFile("input/dict/敏感词.dict")
      .getLines()
      .toList

    new dataSource()
      .data
      .map(x => (x.qid, x.title))
      .map(x => (x._1, x._2, BaseAnalysis.parse(x._2).getTerms.asScala.map(_.getName)))
      .map(x => (x._1, x._2,  x._3.map(a => {if(sen_dict.contains(a)) 1 else 0})))
      .filter(_._3.contains(1))
      .map(x => (x._1, x._2))
      .take(100)
      .foreach(println)
  }
}

object  obj2 {
  def main(args: Array[String]): Unit = {

    new dataSource()
      .data
      .map(x => (x.qid, x.title, x.answer_id, x.content))
      .filter(x => (x._2.contains("嘉豪") || x._4.contains("嘉豪")))
      .foreach(println)

  }
}