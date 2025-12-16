package MySpark
package s1

import org.apache.spark.sql.SparkSession

import java.io.File

class SQLData {
  val hadoop_home = "E:\\hadoop\\hadoop-3.2.2"
  System.setProperty("hadoop.home.dir", hadoop_home)
  System.load(hadoop_home + "/bin/hadoop.dll")

  val ss = SparkSession.builder()
    .master("local").getOrCreate()
  ss.sparkContext.setLogLevel("ERROR")
  val localPath = new File("").getAbsolutePath
  val data = ss.read.schema("name STRING, cls STRING, no STRING, gender STRING, " +
      "birthday STRING, phone STRING, loc STRING, score INT")
    .option("sep", "\t")
    .option("header", "false")
    .csv(localPath + "/input/students_10w.data")
  data.createTempView("students")
}

//男女生人数
object number extends SQLData {
  def main(args: Array[String]): Unit = {
    ss.sql("select gender, count(*) from students group by gender").show()
  }
}

//及格与不及格的人数
object passNum1 extends SQLData {
  def main(args: Array[String]): Unit = {
    ss.sql("select count(*) from students where score >= 60").show()
  }
}

//所有姓氏去重
object surnameDistinct1 extends SQLData {
  def main(args: Array[String]): Unit = {
    ss.sql("select distinct substr(name, 0, 1) from students").show()
  }
}

//出生年月日的最大值
object MaxBirthday extends SQLData {
  def main(args: Array[String]): Unit = {
    ss.sql("select max(birthday) from students").show()
  }
}

//电话号码为奇数的最大值
object addPhoneNumMax extends SQLData {
  def main(args: Array[String]): Unit = {
    ss.sql("select max(phone) from students where phone % 2 == 1").show()
  }
}

//所有出生月份去重
object monthDistinct1 extends SQLData {
  def main(args: Array[String]): Unit = {
    ss.sql("select distinct substr(birthday, 6, 2) from students").show()
    // ss.sql("select distinct month(birthday) from students").show()
  }
}

//求平均年龄
object averageAge1 extends SQLData {
  def main(args: Array[String]): Unit = {
    ss.sql("select avg(2025 - substr(birthday, 0, 4)) from students").show()
    ss.sql("select avg(*) from (select 2025 - substr(birthday, 0, 4) from students) age").show()
  }
}

//手机号前五位的平均值
object averagePhone51 extends SQLData {
  def main(args: Array[String]): Unit = {
    ss.sql("select avg(substr(phone, 0, 5)) from students").show()
  }
}

//年龄分布
object ageDistribution1 extends SQLData {
  def main(args: Array[String]): Unit = {
    ss.sql("select age, count(*) from (select (2025 - substr(birthday, 0, 4)) age from students) group by age").show()
  }
}

//手机号前三位分布
object phone3Distribution1 extends SQLData {
  def main(args: Array[String]): Unit = {
    ss.sql("select phone3, count(*) from (select substr(phone, 0, 3) phone3 from students) group by phone3").show()
  }
}

//电话号码排序
object phoneSort1 extends SQLData {
  def main(args: Array[String]): Unit = {
    ss.sql("select name, phone from students order by phone desc").show()
  }
}

//手机号后四位排序
object phoneLast4Sort2 extends SQLData {
  def main(args: Array[String]): Unit = {
    ss.sql("select name, substr(phone, len(phone)  - 3) phoneLast4, phone from students order by phoneLast4 desc").show()
  }
}

//按住址门牌号排序输出学生名字和住址
object houseNumSort1 extends SQLData {
  def main(args: Array[String]): Unit = {
    ss.sql("select name, loc, substr(loc, len(loc) - 3, 3) houseNum from students order by houseNum desc").show()
  }
}

//门牌号
object houseNumSort2 extends SQLData {
  def main(args: Array[String]): Unit = {
    ss.sql("select substr(loc, len(loc) - 3, (len(loc) -3)) from students ").show()
  }
}


//相同生日下的同学链表
object sameBirthdayList1 extends SQLData {
  def main(args: Array[String]): Unit = {
    //ss.sql("select gender, collect_list(name) from students group by gender").show()
    ss.sql("select substr(birthday, 6) birth, collect_list(name) from students group by birth").show(false)
  }
}

//相同门牌号下的同学链表
object sameHouseNumList extends SQLData {
  def main(args: Array[String]): Unit = {
    //ss.sql("select substr(loc, len(loc) - 3, 3) houseNum from students ").show()
    ss.sql("select substr(loc, len(loc) - 3, 3) houseNum, collect_list(name) from students group by houseNum").show()
  }
}

//生日最大的五个同学
object birthdayMaxTop51 extends SQLData {
  def main(args: Array[String]): Unit = {
    ss.sql("select name, birthday from students order by birthday desc limit(5)").show()
  }
}

//一.统计每个班男生的人数
object classManNum extends SQLData {
  def main(args: Array[String]): Unit = {
    ss.sql("select cls, count(gender) from students where gender = '男' group by cls").show()
  }
}

//二.统计家不是郑州的学生名单
object houseNotInZhengZhou extends SQLData {
  def main(args: Array[String]): Unit = {
    ss.sql("select name, loc from students where substr(loc, 3, 3) != '郑州市'").show()
  }
}

//三.统计手机号后四位有4的男女生人数
object phoneNumLast4 extends SQLData {
  def main(args: Array[String]): Unit = {
    ss.sql("select gender, count(*)   from students where substr(phone, len(phone) - 3) like '%4%' group by gender").show()
    //ss.sql("select gender, count(*) from students where substr(phone, len(phone) - 3).contains('4') group by gender").show()
  }
}

//四.统计成绩在80分以上（包括80）的学生家在哪个市的多
object score80 extends SQLData {
  def main(args: Array[String]): Unit = {
    ss.sql("select substr(loc, 3, 3) city, count(*) count  from students where score >= 80 group by substr(loc, 3, 3)  order by city desc").show()
    //ss.sql("select gender, count(*) from students where substr(phone, len(phone) - 3).contains('4') group by gender").show()
  }
}

//门牌号奇偶数分布
object houseNumDistribution extends SQLData {
  def main(args: Array[String]): Unit = {
    ss.sql("select count(*) from students where substr(loc, len(loc) - 3, 3) % 2 == 1").show()
    ss.sql("select count(*) from students where substr(loc, len(loc) - 3, 3) % 2 == 0").show()
  }
}

//各班男女生人数
object oo1 extends SQLData {
  def main(args: Array[String]): Unit = {
    ss.sql("select cls, gender, count(gender) from students group by cls, gender").show()
  }
}

// 电话号码为偶数的学生的平均分
object oo2 extends SQLData {
  def main(args: Array[String]): Unit = {
    ss.sql("select avg(score) from students where phone % 2 == 0").show()
  }
}

//家住洛阳的同学来自哪个班
object oo3 extends SQLData {
  def main(args: Array[String]): Unit = {
    //ss.sql("select substr(loc, 4, 3) from students ").show()
    ss.sql("select cls, name, loc from students where substr(loc, 4, 3) == '洛阳市'").show()
  }
}

//60分以上的同学的平均分
object oo4 extends SQLData {
  def main(args: Array[String]): Unit = {
    ss.sql("select avg(score) from students where score >= 60").show()
  }
}

//1.求学生的年龄分布
object ageDistribution extends SQLData{
  def main(args: Array[String]): Unit = {
    ss.sql("select (2025 - year(birthday)) age, count(*) numbers from students group by (2025 - year(birthday)) ").show()
    println("刘正作业专用")
  }
}

//2.按照同学们的出生月份进行大小排序
object monthSort extends SQLData{
  def main(args: Array[String]): Unit = {
    ss.sql("select  distinct month(birthday) month from students order by month desc").show()
    println("刘正作业专用")
  }
}

//3.索引出相同城市下的同学链表
object sameCityStuList extends SQLData{
  def main(args: Array[String]): Unit = {
    ss.sql("select substr(loc, 4, 3) city, collect_list(name) from students group by substr(loc, 4, 3)").show()
    println("刘正作业专用")
  }
}

//4.索引出手机号后四位最大的三个同学的名字
object phoneNumLast4Max extends SQLData{
  def main(args: Array[String]): Unit = {
    ss.sql("select name,  substr(phone, len(phone) - 3) last4 from students order by substr(phone, len(phone) - 3) desc").show()
    println("刘正作业专用")
  }
}

//5.索引出手机号后四位之和最大的三个同学的名字
object phoneNumLast4SumMax extends SQLData{
  def main(args: Array[String]): Unit = {
    ss.sql("select phone, aggregate(cast(split((substr(phone, len(phone) - 3)), ''), int), 0, (acc, x) -> acc + x) last4Sum from students").show()
    println("刘正作业专用")
  }
}

//年龄分布（子查询）
object ageDistribution2 extends SQLData{
  def main(args: Array[String]): Unit = {
    ss.sql("select  t.age, count(*) from (select (2025 - int(substr(birthday, 0, 4))) age from students) t group by t.age").show()
  }
}

//生日各月分布
object birthdayMonthDistribution extends SQLData{
  def main(args: Array[String]): Unit = {
    ss.sql("select  t.month, count(*) from (select month(birthday) month from students) t group by t.month").show()
  }
}

