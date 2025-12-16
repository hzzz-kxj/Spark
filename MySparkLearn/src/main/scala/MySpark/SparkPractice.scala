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
  val data = sc.textFile(localPath + "/input/students_10w.data")
    .map(_.trim.split("\t"))
    .filter(_.length == 8)
}

//男女生人数
object numbers extends DataSource {
  def main(args: Array[String]): Unit = {
    data
      .map(x => x(3))
      .groupBy(x => x)
      //.groupBy(x => x(3))
      //.mapValues(_.toList)
      //.mapValues(_.size)
      //.sortByKey()
      .foreach(println)
  }
}

//及格与不及格的人数
object passNum extends DataSource {
  def main(args: Array[String]): Unit = {
    println("及格的人数：", data.filter(x => x(7).toInt >= 60).count())
    println("不及格的人数：", data.filter(x => x(7).toInt < 60).count())
  }
}

//出生年月日的最大值
object maxBirth extends DataSource {
  def main(args: Array[String]): Unit = {
    println("出生年月日最大值：", data.map(x => x(4).replaceAll("-", "").toLong).min())
    println("出生年月日最小值：", data.map(x => x(4).replaceAll("-", "").toLong).max())
  }
}

//电话号码为奇数的最大值
object maxPhone extends DataSource {
  def main(args: Array[String]): Unit = {
    println("奇数号码最大值：", data.map(x => x(5).toLong).filter(_ % 2 == 1).max())
    println("奇数号码最小值：", data.map(x => x(5).toLong).filter(_ % 2 == 1).min())
    println("奇数号码数量：", data.map(x => x(5).toLong).filter(_ % 2 == 1).count())
    println("偶数号码数量：", data.map(x => x(5).toLong).filter(_ % 2 == 0).count())
  }
}

//所有姓氏去重
object surnameDistinct extends DataSource {
  def main(args: Array[String]): Unit = {
    data
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
    data
      .map(x => x(4).substring(5, 7))
      .distinct()
      .take(20)
      .foreach(println)
  }
}

//求平均年龄
object averageAge extends DataSource {
  def main(args: Array[String]): Unit = {
    val age = data
      .map(x => 2025 - x(4).take(4).toInt)
    //.foreach(println)
    println(age.sum().toFloat / age.count())
  }
}

//手机号前五位的平均值
object phoneNumber5Avg extends DataSource {
  def main(args: Array[String]): Unit = {
    val phone = data.map(x => x(5).take(5).toInt)
    println(phone.sum().toFloat / phone.count())
  }
}

//年龄分布
object ageDistribution extends DataSource {
  def main(args: Array[String]): Unit = {
    data
      .map(x => 2025 - x(4).take(4).toInt)
      .groupBy(x => x)
      .mapValues(_.size)
      .sortBy(x => x._2)
      .foreach(println)
  }
}

//手机号前三位分布
object phoneNumber3Distribution extends DataSource {
  def main(args: Array[String]): Unit = {
    data
      .map(x => x(5).take(3).toInt)
      .groupBy(x => x)
      .mapValues(_.size)
      .sortBy(x => x._2)
      .foreach(println)
  }
}

//电话号码排序
object phoneNumberSort extends DataSource {
  def main(args: Array[String]): Unit = {
    data
      .map(x => x(5).toLong)
      .sortBy(x => x)
      .foreach(println)
  }
}

//按住址门牌号排序输出学生名字和住址
object houseNumSort extends DataSource {
  def main(args: Array[String]): Unit = {
    data
      .map(x => (x(0), x(6), x(6).takeRight(4).take(3).toInt))
      .sortBy(_._3)
      .foreach(println)
  }
}

//相同生日下的同学链表
object sameBirthdayList extends DataSource {
  def main(args: Array[String]): Unit = {
    data
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
    data
      .map(x => (x(6).takeRight(4).take(3), x(0)))
      .groupByKey()
      .map(x => (x._1, x._2.toList))
      //.groupBy(_._1)
      //.map(x => (x._1, x._2.map(_._2)))
      .foreach(println)
  }
}

//生日最大的五个同学
object birthdayTop5 extends DataSource {
  def main(args: Array[String]): Unit = {
    data
      .map(x => (x(4).takeRight(5).replaceAll("-", "").toInt, x(0)))
      .sortBy(_._1, ascending = false)
      .take(20)
      .foreach(println)
  }
}

//手机号内8数量最多的前三位
object phoneNumCount8Top3 extends DataSource {
  def main(args: Array[String]): Unit = {
    data
      .map(x => (x(0), x(5).split("")))
      .map(x => (x._2.count(_ == "7"), x._1))
      .sortByKey(ascending = false)
      .take(20)
      .foreach(println)
  }
}

//一.统计每个班男生的人数
object manNum extends DataSource {
  def main(args: Array[String]): Unit = {
    println("刘正作业专用")
    data
      .map(x => (x(1), x(3)))
      .filter(_._2.equals("男"))
      .groupByKey()
      .mapValues(_.size)
      .foreach(println)
  }
}

//二.统计家不是郑州的学生名单
object houseNotInZhengZhou extends DataSource {
  def main(args: Array[String]): Unit = {
    data
      .map(x => (x(6).substring(3, 6), x(0)))
      .filter(_._1 != "郑州市")
      .foreach(println)
    println("刘正作业专用")
  }
}

//三.统计手机号后四位有4的男女生人数
object phoneNumLast4 extends DataSource {
  def main(args: Array[String]): Unit = {
    println("刘正作业专用")
    data
      .map(x => (x(3), x(5).takeRight(4)))
      .filter(_._2.contains("4"))
      .groupByKey()
      .mapValues(_.size)
      .foreach(println)
  }
}

//四.统计成绩在80分以上（包括80）的学生家在哪个市的多
object o21 extends DataSource {
  def main(args: Array[String]): Unit = {
    println("刘正作业专用")
    data
      .map(x => (x(6).substring(3, 6), x(7).toInt))
      .filter(_._2 >= 80)
      .groupByKey()
      .mapValues(_.size)
      .sortBy(_._2, ascending = false)
      .take(20)
      .foreach(println)
  }
}

//四.全国有哪些市
object o22 extends DataSource {
  def main(args: Array[String]): Unit = {
    data
      .map(x => x(6).dropRight(4))
      .filter(x => x.contains("省"))
      .filter(x => x.contains("市"))
      .map(x => (x.take(3), x.substring(3)))
      .map(x => {
        if (x._2.startsWith("省"))
          (x._1 + x._2.take(1), x._2.substring(1))
        else (x._1, x._2)
      })
      .map(x => (x._1, x._2.take(3), x._2.substring(3)))
      .map(x => {
        if (x._3.startsWith("市"))
          (x._1, x._2 + x._3.take(1), x._3.substring(1))
        else (x._1, x._2, x._3)
      })
      .filter(_._1 == "黑龙江省")
      .distinct()

      .take(100)
      .foreach(println)
  }
}

object o23 extends DataSource {
  def main(args: Array[String]): Unit = {
    data.map(x => x(6).dropRight(4))
      .filter(x => x.contains("黑龙江省牡丹江市"))
      .distinct()
      .foreach(println)
  }
}

//门牌号奇偶数分布
object o1 extends DataSource {
  def main(args: Array[String]): Unit = {
    val d1 = data
      .map(x => x(6).takeRight(4).take(3))
    println("奇数", d1.filter(x => x.toInt % 2 == 1).count())
    println("偶数", d1.filter(x => x.toInt % 2 == 0).count())
  }
}

// 手机号包含7的数量最多的学生名单
object o2 extends DataSource {
  def main(args: Array[String]): Unit = {
    data
      .map(x => (x(0), x(5).split("")))
      .map(x => (x._2.count(_ == "7"), x._1))
      .sortByKey(false)
      .take(10)
      .foreach(println)
  }
}

// 手机号前三位
object o3 extends DataSource {
  def main(args: Array[String]): Unit = {
    data
      .map(_(5).take(3))
      .distinct()
      .foreach(println)
  }
}

object o4 extends DataSource {
  def main(args: Array[String]): Unit = {

  }
}

object o5 extends DataSource {
  def main(args: Array[String]): Unit = {

  }
}

// 成绩均值，只需要一次action型计算 d.sum / d.count需要两次action计算
object scoreAvg extends DataSource {
  def main(args: Array[String]): Unit = {
    val d = data
      .map(x => (x(7).toInt, 1))
      .filter(x => x._1 >= 60 && x._1 <= 80)
      .reduce((x, y) => (x._1 + y._1, x._2 + y._2))
    println(d._1.toFloat / d._2)
  }
}

// 出生月份分布
object oo2 extends DataSource {
  def main(args: Array[String]): Unit = {
    data
      .map(x => (x(4).substring(5, 7).toInt, 1))
      .groupBy(_._1)
      .map(x => (x._1, x._2.size))
      .foreach(println)
  }
}

// 学号后四位跟生日拼接后的学生 如0001 + 0421 == 0422
object oo3 extends DataSource {
  def main(args: Array[String]): Unit = {
    data.map(x => (x(2).substring(5).toInt + x(4).replaceAll("-", "").substring(4).toInt, x(0)))
      .sortBy(_._1)
      .foreach(println)
  }
}

// 生日最大的五个同学
object birthdayMaxTop01 extends DataSource {
  def main(args: Array[String]): Unit = {
    data
      .map(x => (x(4).takeRight(5).replaceAll("-", "").toInt, x(0)))
      .sortBy(_._1, ascending = false)
      .take(5)
      .foreach(println)
  }
}

// 索引河南省各市有哪些各县或区
object CityContainsCounty extends DataSource {
  def main(args: Array[String]): Unit = {
    data
      .map(x => (x(6).take(3), x(6).substring(3, 6), x(6).substring(6)))
      .filter(x => x._1.equals("河南省"))
      .filter(_._3.length > 4)
      .map(x => (x._2, x._3.dropRight(4)))
      .map(x => {
        if (x._2.startsWith("市")) {
          (x._1 + x._2.take(1), x._2.substring(1))
        } else (x._1, x._2)
      })
      .filter(_._2.length > 0)
      .distinct()
      .groupByKey()
      .map(x => (x._1, x._2.toList))
      .foreach(println)
  }
}

// 手机号按位加和最大值前三位
object sumPhoneTop3 extends DataSource {
  def main(args: Array[String]): Unit = {
    data
      .map(x => (x(5), x(5).split("")))
      .map(x => (x._1, x._2.map(_.toInt).sum))
      .sortBy(_._2, false)
      .take(10)
      .foreach(println)
  }
}

object oo1 extends DataSource {
  def main(args: Array[String]): Unit = {
    data
      .map(x => (x(6).take(3), x(6).substring(3)))
      .filter(x => x._1.equals("河南省"))
      .mapValues(_.dropRight(4))
      .map(x => (x._2.take(3), x._2.substring(3)))
      .map(x => {
        if (x._2.startsWith("市")) {
          (x._1 + x._2.take(1), x._2.substring(1))
        } else (x._1, x._2)
      })
      .filter(_._2.length > 0)
      .distinct()
      .groupByKey()
      .map(x => (x._1, x._2.toList))
      .foreach(println)
  }
}

object oo4 extends DataSource {
  def main(args: Array[String]): Unit = {
    val sum = data
      .map(x => x(7).toInt)
      .takeSample(true, 1000)
      .map(x => (x, 1))
      .reduce((x, y) => (x._1 + y._1, x._2 + y._2))
    println(sum._1.toFloat / sum._2)

  }
}

object oo5 extends DataSource {
  def main(args: Array[String]): Unit = {
    data
      .map(x => x(4))
      .top(3)
      .foreach(println)

  }
}


