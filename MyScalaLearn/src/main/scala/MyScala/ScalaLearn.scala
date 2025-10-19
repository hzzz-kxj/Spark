package MyScala

class LoadData{
  val data=scala.io.Source
    .fromFile("input/students.data","utf8")
    .getLines()
    .toList
    .map(_.trim().split("\t"))
    .filter(_.length==8)
}
//男女生人数
object mannumber extends LoadData{
  def main(args: Array[String]): Unit = {
    data.map(x=>(x(3),1))
      .groupBy(_._1)
      //.map(x=>(x._1,x._2.length))
      .foreach(println)
  }
}

//出生年月最大值
object yearmax extends LoadData{
  def main(args: Array[String]): Unit = {
    val data2=data.map(_(4).replace("-","").toInt)
      println(data2.max)
  }
}

//求学生中出现的所有姓氏
object surname extends LoadData{
  def main(args: Array[String]): Unit = {
    data.map(_(0).substring(0,1).distinct)
      .foreach(println)
  }
}

//求平均年龄
object ageAVG extends LoadData{
  def main(args: Array[String]): Unit = {
    val age=data.map(2025-_(4).substring(0,4).toInt)
      println(age.sum/age.length)
  }
}

//求年龄分布
object age extends LoadData{
  def main(args: Array[String]): Unit = {
    data.map(2025-_(4).substring(0,4).toInt)
      .map(x=>(x,1))
      .groupBy(_._1)
      .map(x=>(x._1,x._2.length))
      .foreach(println)
  }
}
object age2 extends LoadData{
  def main(args: Array[String]): Unit = {
    data.map(x=>(2025-x(4).substring(0,4).toInt,1))
      .groupBy(_._1)
      .map(x=>(x._1,x._2.length))
      .foreach(println)
  }
}

//电话号码排序
object phonesort extends LoadData{
  def main(args: Array[String]): Unit = {
    data.map(_(5).toLong)
      .sorted
      .reverse
      .foreach(println)

  }
}

//相同生日下姓名
object mouthday extends LoadData{
  def main(args: Array[String]): Unit = {
    data.map(x=>(x(4).substring(5),x(0)))
      .groupBy(_._1)
      .mapValues(_.map(_._2))
      .foreach(println)
  }
}
