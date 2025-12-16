package MyScala

import scala.io.Source

class DataSource {
  val source = Source
    .fromFile("input/students_10w.data")
    .getLines()
    .toList
    .map(_.trim.split("\t"))
    .filter(_.length == 8)
}

//1.统计男女生人数
object numbers extends DataSource {
  def main(args: Array[String]): Unit = {
    println("刘正专用作业")
    source
      .groupBy(x => x(3))
      .mapValues(_.length)
      //.map(x => (x._1, x._2.length))
      .foreach(println)
  }
}

//2.求出生年月最大值
object birthdayMax extends DataSource {
  def main(args: Array[String]): Unit = {
    val dates = source
      .map(x => x(4).replace("-", "").toInt)
    //.foreach(println)

    println("最大值：" + dates.max.toString.substring(0, 4) + "-" +
      dates.max.toString.substring(4, 6) + "-" +
      dates.max.toString.substring(6, 8))
    println("最小值：" + dates.min.toString.substring(0, 4) + "-" +
      dates.min.toString.substring(4, 6) + "-" +
      dates.min.toString.substring(6, 8))
  }
}

//3.求学生中出现的所有姓氏（去重）
object surnameDistinct extends DataSource {
  def main(args: Array[String]): Unit = {
    source
      .map(_(0).substring(0, 1))
      .distinct
      /*.groupBy(x => x)
      .mapValues(_.size)
      .toList.sortBy(_._2)
      .reverse.take(20)*/
      .foreach(println)
      }
}

//4.求年龄平均值
object ageAvg extends DataSource {
  def main(args: Array[String]): Unit = {
    val age = source.map(2025 - _(4).substring(0, 4).toInt)
    println(age.sum.toFloat / age.size)
  }
}

//5.求学生的年龄分布(每个年龄有多少人)
object ageDistribution extends DataSource {
  def main(args: Array[String]): Unit = {
    source
      .map(2025 - _(4).substring(0, 4).toInt)
      .groupBy(x => x)
      .map(x => (x._1, x._2.length))
      .foreach(println)
  }
}

//6.按同学们的电话号码大小排序
object phoneSort extends DataSource {
  def main(args: Array[String]): Unit = {
    source
      .map(_(5).toLong)
      .sorted.reverse
      .foreach(println)
  }
}

//7.索引出相同生日下的同学链表
object sameBirthday extends DataSource {
  def main(args: Array[String]): Unit = {
    source
      .map(x => (x(0), x(4).substring(5)))
      .groupBy(_._2)
      //.mapValues(_.map(_._1)) //mapValues是对map的value进行处理，key不动
      .map(x => (x._1, x._2.map(_._1)))
      .foreach(println)
  }
}

//8.求生日最大的五个同学的名字
object birthdayTop5 extends DataSource {
  def main(args: Array[String]): Unit = {
    source
      .map(x => (x(0), x(4).substring(5)))
      .sortBy(x => x._2).reverse.take(5)
      .map(x => (x._1,x._2))
      .foreach(println)
  }
}

//一.统计每个班男生的人数
object manNumbers extends DataSource {
  def main(args: Array[String]): Unit = {
    println("刘正专用作业")
    source
      .map(x => (x(1),x(3)))
      .filter(x => x._2.equals("男"))
      .groupBy(_._1)
      .mapValues(_.size)
      .foreach(println)
  }
}

//二.统计家不在郑州的学生名单
object addressNotInZhengzhou extends DataSource {
  def main(args: Array[String]): Unit = {
    println("刘正专用作业")
      source
        .map(x => (x(0),x(6).substring(3, 6)))
        .filter(_._2 != "郑州市")
        .foreach(println)
    /*source
      .map(x => (x(6), if(x(6).contains("郑州市")) 1 else 0))
      .groupBy(_._2)
      .filter(x => x._1 == 0)
      .map(x => (x._2.size, x._2.map(_._1)))
      //.map(x => x._2.length)
      .foreach(println)*/
  }
}

//三.统计手机号后四位有4的的男女生人数
object phone4 extends DataSource {
  def main(args: Array[String]): Unit = {
    println("刘正专用作业")
      source
        .map(x => (x(3), x(5).takeRight(4)))
        .filter(_._2.contains("4"))
        .groupBy(_._1)
        .mapValues(_.size)
        .foreach(println)
    /*source
      .map(x => (x(5), x(5).substring(7)))
      .groupBy(_._2.contains("4"))
      .filter(_._1.equals(true))
      .map(x=> (x._2.length, x._2.map(_._1)))
      .foreach(println)*/

  }
}

//四.统计成绩在80分以上（包括80）的学生家在哪个市的多
object obj4 extends DataSource {
  def main(args: Array[String]): Unit = {
    println("刘正专用作业")
    source
      .map(x => (x(6).substring(3, 6), x(7)))
      .filter(_._2.toInt >= 80)
      .groupBy(_._1)
      .mapValues(_.size)
      .toList.sortBy(_._2).reverse.take(6)
      .foreach(println)
  }
}

//相同生日下同学链表
object o1 extends DataSource{
  def main(args: Array[String]): Unit = {

    source
      .map(x => (x(0), x(4).substring(5)))
      .groupBy(_._2)
      .map(x => (x._1, x._2.map(_._1)))
      .foreach(println)
  }
}

object o2 {
  def main(args: Array[String]): Unit = {

    new DataSource()
      .source
      .map(x => x(6).substring(0,3).distinct)
      .foreach(println)
  }
}

object o3 {
  def main(args: Array[String]): Unit = {

    val l1 = new DataSource()
      .source
      .map(x => x(6).takeRight(4).substring(0,3).toInt)
    println(l1.sum.toFloat / l1.size)
  }
}

//各省份人数分布
object o4 {
  def main(args: Array[String]): Unit = {

    new DataSource()
      .source
      .map(x => x(6).substring(0,3))
      .groupBy(x => x)
      .mapValues(_.size)
      .foreach(println)
  }
}

object o5 {
  def main(args: Array[String]): Unit = {

    new DataSource()
      .source
      .map(x => (x(0),x(7).toInt))
      .filter(_._1.substring(0,1).equals("张"))
      .sortBy(_._2)
      .reverse
      .foreach(println)
  }
}

object o6 {
  def main(args: Array[String]): Unit = {

    new DataSource()
      .source
      .map(x => (x(0), x(7)))
      .groupBy(_._2)
      .map(x => (x._1, x._2.map(_._1)))
      .foreach(println)
  }
}

object o7 {
  def main(args: Array[String]): Unit = {

    new DataSource()
      .source
      .map(x => (x(0), x(6).substring(0,3),x(7)))
      .filter(_._2.equals("河南省"))
      .sortBy(x => x._3).reverse.take(3)
      .map(x => (x._1,x._2,x._3))
      .foreach(println)
  }
}

object o8 {
  def main(args: Array[String]): Unit = {

    new DataSource()
      .source
      .filter(!_.apply(6).contains("河南省郑州"))
      .map(_.apply(0))
      .foreach(println)
  }
}



