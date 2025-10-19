package MySpark

import org.apache.spark.SparkContext

import java.io.File

class DataSource {
  val hadoopHome = "E:\\hadoop\\hadoop-3.2.2"
  System.setProperty("hadoop.home.dir", hadoopHome)
  System.load(hadoopHome + "\\bin\\hadoop.dll")

  val sc = new SparkContext("local", "myapp", System.getenv("SPARK_HOME"))
  sc.setLogLevel("ERROR")

  val localPath = new File("").getAbsolutePath()
  val file = sc.textFile(localPath + "/input/students_10w.data")
    .map(_.trim.split("\t"))
    .filter(_.length == 8)
}

//男女生人数
object numbers extends DataSource {
  def main(args: Array[String]): Unit = {
    file
      .groupBy(x => x(3))
      .mapValues(_.size)
      .sortByKey()
      .foreach(println)
  }
}

//及格与不及格的人数
object passNum extends DataSource {
  def main(args: Array[String]): Unit = {
    println("及格的人数：", file.filter(x => x(7).toInt >= 60).count())
    println("不及格的人数：", file.filter(x => x(7).toInt < 60).count())
  }
}

//出生年月日的最大值
object maxBirth extends DataSource {
  def main(args: Array[String]): Unit = {
    println("出生年月日最大值：", file.map(x => x(4).replaceAll("-", "").toLong).min())
    println("出生年月日最小值：", file.map(x => x(4).replaceAll("-", "").toLong).max())
  }
}

//电话号码为奇数的最大值
object maxPhone extends DataSource {
  def main(args: Array[String]): Unit = {
    println("奇数号码最大值：", file.map(x => x(5).toLong).filter(_ % 2 == 1).max())
    println("奇数号码最小值：", file.map(x => x(5).toLong).filter(_ % 2 == 1).min())
  }
}

//所有姓氏去重
object surnameDistinct extends DataSource {
  def main(args: Array[String]): Unit = {
    file
      .map(x => x(0).take(1))
      .distinct()
      .groupBy(x => x)
      .mapValues(_.size)
      .sortByKey()
      .take(20)
      .foreach(println)
  }
}

//所有出生月份去重
object monthDistinct extends DataSource {
  def main(args: Array[String]): Unit = {
    file
      .map(x => x(4).substring(5, 7))
      .distinct()
      .take(20)
      .foreach(println)
  }
}

//求平均年龄
object averageAge extends DataSource {
  def main(args: Array[String]): Unit = {

    val age = file
      .map(x => 2025 - x(4).take(4).toInt)
    //.foreach(println)
    println(age.sum().toFloat / age.count())
  }
}

//手机号前五位的平均值
object averagePhone5 extends DataSource {
  def main(args: Array[String]): Unit = {
    val phone = file.map(x => x(5).take(5).toInt)
    println(phone.sum().toFloat / phone.count())
  }
}

//年龄分布
object ageDistribution extends DataSource {
  def main(args: Array[String]): Unit = {
    file
      .map(x => 2025 - x(4).take(4).toInt)
      .groupBy(x => x)
      .mapValues(_.size)
      .sortBy(x => x._2)
      .foreach(println)
  }
}

//手机号前三位分布
object phone3Distribution extends DataSource {
  def main(args: Array[String]): Unit = {
    file
      .map(x => x(5).take(3).toInt)
      .groupBy(x => x)
      .mapValues(_.size)
      .sortBy(x => x._2)
      .foreach(println)
  }
}

//电话号码排序
object phoneSort extends DataSource {
  def main(args: Array[String]): Unit = {
    file
      .map(x => x(5).toLong)
      .sortBy(x => x)
      .foreach(println)
  }
}

//按住址门牌号排序输出学生名字和住址
object houseNumSort extends DataSource {
  def main(args: Array[String]): Unit = {
    file
      .map(x => (x(0), x(6), x(6).takeRight(4).take(3).toInt))
      .sortBy(_._3)
      .foreach(println)
  }
}

//相同生日下的同学链表
object sameBirthdayList extends DataSource {
  def main(args: Array[String]): Unit = {
    file
      .map(x => (x(4).takeRight(5), x(0)))
      .groupByKey()
      .map(x => (x._1, x._2.toList))
      //.groupBy(_._1)
      //.map(x => (x._1, x._2.map(_._2)))
      .foreach(println)
  }
}

//相同门牌号下的同学链表
object sameHouseNumList extends DataSource {
  def main(args: Array[String]): Unit = {
    file
      .map(x => (x(6).takeRight(4).take(3), x(0)))
      .groupByKey()
      .map(x => (x._1, x._2.toList))
      //.groupBy(_._1)
      //.map(x => (x._1, x._2.map(_._2)))
      .foreach(println)
  }
}

//生日最大的五个同学
object  birthdayMaxTop5 extends DataSource{
  def main(args: Array[String]): Unit = {
    file
      .map(x => (x(4).takeRight(5).replaceAll("-", "").toInt, x(0)))
      .sortBy(_._1, ascending = false)
      .take(20)
      .foreach(println)
  }
}

//手机号内8数量最多的前三位
object phone8Top3 extends DataSource{
  def main(args: Array[String]): Unit = {
    file
      .map(x => (x(5), x(5).split("")))
      .map(x => (x._2.count(_ == "8"), x._1))
      .sortByKey(ascending = false)
      .take(20)
      .foreach(println)
  }
}

//一.统计每个班男生的人数
object manNum extends DataSource{
  def main(args: Array[String]): Unit = {
    file
      .map(x => (x(1), x(3)))
      .filter(_._2.equals("男"))
      .groupByKey()
      .mapValues(_.size)
      .foreach(println)
  }
}

//二.统计家不是郑州的学生名单
object house extends DataSource{
  def main(args: Array[String]): Unit = {
    file
      .map(x => (x(6).substring(3,6), x(0)))
      .filter(_._1 != "郑州市")
      .foreach(println)
  }
}

//三.统计手机号后四位有4的男女生人数
object phoneLast4 extends DataSource{
  def main(args: Array[String]): Unit = {
    file
      .map(x => (x(3), x(5).takeRight(4)))
      .filter(_._2.contains("4"))
      .groupByKey()
      .mapValues(_.size)
      .foreach(println)
  }
}

//四.统计成绩在80分以上（包括80）的学生家在哪个市的多
object o21 extends DataSource{
  def main(args: Array[String]): Unit = {
    file
      .map(x => (x(6).substring(3,6), x(7).toInt))
      .filter(_._2 >= 80)
      .groupByKey()
      .mapValues(_.size)
      .sortBy(_._2, ascending = false)
      .take(20)
      .foreach(println)
  }
}