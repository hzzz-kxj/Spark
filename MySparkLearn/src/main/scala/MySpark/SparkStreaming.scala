package MySpark
package s2

import org.apache.kafka.clients.consumer.{ConsumerConfig, KafkaConsumer}
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerConfig, ProducerRecord}
import org.apache.kafka.common.serialization.StringDeserializer

import java.time.Duration
import java.util
import java.util.Properties
import org.apache.spark.SparkContext
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions.expr
import org.apache.spark.sql.streaming.Trigger
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.apache.spark.streaming.kafka010.KafkaUtils
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.{Seconds, StreamingContext}

import java.io.{File, PrintStream}
import java.net.ServerSocket
import java.text.SimpleDateFormat
import scala.util.Random
import scala.util.control.Breaks.breakable

class GenRandomStudents {
  def readSrcFile(filename: String) = {
    scala.io.Source
      .fromFile("input/students/" + filename, "UTF-8")
      .getLines()
      .filter(_.length > 0)
      .toArray
  }

  val familyNames = readSrcFile("family_names.txt").flatMap(_.trim.split(""))
  val commonWords = readSrcFile("common_words.txt").flatMap(_.trim.split(""))
  val phoneStart = readSrcFile("phone_start.txt")
  val citys = readSrcFile("citys.txt")

  val rand = new Random
  var clazzs = 1.to(100).map("RB" + "%03d".format(_))
  val genders = Array("男", "女")


  def genOneStudent(): String = {
    val name = familyNames(rand.nextInt(familyNames.length)) + commonWords(rand.nextInt(commonWords.length)) + commonWords(rand.nextInt(commonWords.length))
    val clz = clazzs(rand.nextInt(clazzs.length))
    val no = clz + "%02d".format(rand.nextInt(100))
    val gender = genders(rand.nextInt(genders.length))
    val sdf = new SimpleDateFormat("yyyy-MM-dd")
    val birthday = sdf.format(sdf.parse("2000-01-01").getTime + 24l * 3600 * 1000 * rand.nextInt(3 * 365))
    val phone = phoneStart(rand.nextInt(phoneStart.length)) + "%08d".format(rand.nextInt(100000000))
    val loc = citys(rand.nextInt(citys.length)) + "%03d".format(rand.nextInt(1000)) + "号"
    val score = rand.nextInt(100)
    Array(name, no, clz, gender, birthday, phone, loc, score).mkString("\t")
  }
}

object TryStudents extends GenRandomStudents {
  def main(args: Array[String]): Unit = {
    println(genOneStudent())
  }
}

object 生成学生信息 extends GenRandomStudents {
  def main(args: Array[String]): Unit = {
    var ss = new ServerSocket(7777)
    while (true) {
      println("server ready")
      val s = ss.accept
      s.setSoTimeout(1)
      val ps = new PrintStream(s.getOutputStream)
      breakable {
        for (i <- 0 until 10) {
          val msg = genOneStudent()
          ps.println(msg)
          println(msg)
          Thread.sleep(500)
        }
      }
      s.close()
    }
  }
}

class LoadSparkStreaming {
  val hadoop_home = "E:\\hadoop\\hadoop-3.2.2"
  System.setProperty("hadoop.home.dir", hadoop_home)
  System.load(hadoop_home + "/bin/hadoop.dll")
  val localProjectPath = new File("").getAbsolutePath
  val sc = new SparkContext("local[2]", "app")
  sc.setLogLevel("ERROR")
  val ssc = new StreamingContext(sc, Seconds(2))
  val lines = ssc.socketTextStream("localhost", 7777, StorageLevel.MEMORY_ONLY)
    .map(_.trim.split("\t"))
    .filter(_.length == 8)
}

//男女生人数
object manNumbers extends LoadSparkStreaming {
  def main(args: Array[String]): Unit = {
    lines
      .map(x => (x(3), 1))
      .groupByKey()
      .mapValues(_.size)
      .print()

    ssc.start()
    ssc.awaitTermination()
    ssc.stop()
  }
}

//及格不及格的人数
object passNum extends LoadSparkStreaming {
  def main(args: Array[String]): Unit = {
    lines
      .map(x => if(x(7).toInt >= 60) ("及格", 1) else ("不及格", 1))
      .groupByKey()
      .mapValues(_.size)
      .print()

    ssc.start()
    ssc.awaitTermination()
    ssc.stop()
  }
}

//出生年月日的最大值
object maxBirthday extends LoadSparkStreaming {
  def main(args: Array[String]): Unit = {
    lines
      .map(x => x(4).replaceAll("-", ""))
      .reduce((x, y) => if (x > y) x else y)
      .print()

    ssc.start()
    ssc.awaitTermination()
    ssc.stop()
  }
}

object o11 extends LoadSparkStreaming {
  def main(args: Array[String]): Unit = {
    lines
      .map(x => x(4).replaceAll("-", ""))
      .foreachRDD(x => x.sortBy(x => x, false).take(3).foreach(println))

    ssc.start()
    ssc.awaitTermination()
    ssc.stop()
  }
}

object o12 extends LoadSparkStreaming {
  def main(args: Array[String]): Unit = {
    lines
      .filter(x => x(6).take(3).contains("河南省"))
      .map(x => x(7).toInt)
      .reduce((x, y) => if (x > y) x else y)
      .print()

    ssc.start()
    ssc.awaitTermination()
    ssc.stop()
  }
}

//电话号码为奇数的最大值
object maxPhoneNumber extends LoadSparkStreaming {
  def main(args: Array[String]): Unit = {
    lines
      .map(x => x(5).toLong)
      .filter(x => x % 2 == 1)
      .foreachRDD(x => x.sortBy(x => x, ascending = false).take(1).foreach(println))

    ssc.start()
    ssc.awaitTermination()
    ssc.stop()
  }
}

//所有姓氏去重
object surnameDistinct extends LoadSparkStreaming {
  def main(args: Array[String]): Unit = {
    lines
      .map(x => x(6).take(3))
      .foreachRDD(x => x.distinct().foreach(println))

    ssc.start()
    ssc.awaitTermination()
    ssc.stop()
  }
}

object o13 extends LoadSparkStreaming {
  def main(args: Array[String]): Unit = {
    lines
      .map(x => x(0).take(1))
      .foreachRDD(x => x.distinct().foreach(println))

    ssc.start()
    ssc.awaitTermination()
    ssc.stop()
  }
}

//所有出生月份去重
object monthDistinct extends LoadSparkStreaming {
  def main(args: Array[String]): Unit = {
    lines
      .map(x => x(4).substring(5, 7))
      .foreachRDD(x => x.distinct().foreach(println))

    ssc.start()
    ssc.awaitTermination()
    ssc.stop()
  }
}

//平均年龄
object avgAge extends LoadSparkStreaming {
  def main(args: Array[String]): Unit = {
    lines
      .map(x => (2025.0 - x(4).take(4).toDouble, 1))
      .reduce((x, y) => (x._1 + y._1, x._2 + y._2))
      .map(x => x._1 / x._2)
      .print()

    ssc.start()
    ssc.awaitTermination()
    ssc.stop()
  }
}

//手机号后四位平均值
object o14 extends LoadSparkStreaming {
  def main(args: Array[String]): Unit = {
    lines
      .map(x => (x(5).takeRight(4).toDouble, 1))
      .reduce((x, y) => (x._1 + y._1, x._2 + y._2))
      .map(x => x._1 / x._2)
      .print()

    ssc.start()
    ssc.awaitTermination()
    ssc.stop()
  }
}

//手机号前五位的平均值
object phoneNumber5Avg extends LoadSparkStreaming {
  def main(args: Array[String]): Unit = {
    lines
      .map(x => (x(5).take(5).toDouble, 1))
      .reduce((x, y) => (x._1 + y._1, x._2 + y._2))
      .map(x => x._1 / x._2)
      .print()

    ssc.start()
    ssc.awaitTermination()
    ssc.stop()
  }
}

//年龄分布
object ageDistribution extends LoadSparkStreaming {
  def main(args: Array[String]): Unit = {
    lines
      .map(x => (2025 - x(4).take(4).toInt, 1))
      .groupByKey()
      .mapValues(_.size)
      .print()

    ssc.start()
    ssc.awaitTermination()
    ssc.stop()
  }
}

//手机号前三位分布
object phoneNumber3Distribution extends LoadSparkStreaming {
  def main(args: Array[String]): Unit = {
    lines
      .map(x => (x(5).take(3).toInt, 1))
      .groupByKey()
      .mapValues(_.size)
      .print()

    ssc.start()
    ssc.awaitTermination()
    ssc.stop()
  }
}

//电话号码排序
object phoneNumberSort extends LoadSparkStreaming {
  def main(args: Array[String]): Unit = {
    lines
      .map(x => (x(5).toLong, 1))
      .foreachRDD(_.sortBy(x => x._1, ascending = false).foreach(println))

    ssc.start()
    ssc.awaitTermination()
    ssc.stop()
  }
}

object o15 extends LoadSparkStreaming {
  def main(args: Array[String]): Unit = {
    lines
      .map(x => (x(6).takeRight(4).take(3).toInt, x(0), x(6)))
      .foreachRDD(_.sortBy(x => x._1, ascending = false).foreach(println))

    ssc.start()
    ssc.awaitTermination()
    ssc.stop()
  }
}

//按住址门牌号排序输出学生名字和住址
object houseNumSort extends LoadSparkStreaming {
  def main(args: Array[String]): Unit = {
    lines
      .map(x => (x(6).takeRight(4).take(3).toInt, x(0), x(6)))
      .foreachRDD(_.sortBy(x => x._1, ascending = false).foreach(println))

    ssc.start()
    ssc.awaitTermination()
    ssc.stop()
  }
}

//相同生日下的同学链表
object sameBirthdayList extends LoadSparkStreaming {
  def main(args: Array[String]): Unit = {
    lines
      .map(x => (x(4).takeRight(5).replaceAll("-", "").toInt, x(0)))
      .groupByKey()
      .print()

    ssc.start()
    ssc.awaitTermination()
    ssc.stop()
  }
}

//相同门牌号下的同学链表
object sameHouseNumList extends LoadSparkStreaming {
  def main(args: Array[String]): Unit = {
    lines
      .map(x => (x(6).takeRight(4).take(3).toInt, x(0)))
      .groupByKey()
      .print()

    ssc.start()
    ssc.awaitTermination()
    ssc.stop()
  }
}

//生日最大的五个同学
object birthdayTop5 extends LoadSparkStreaming {
  def main(args: Array[String]): Unit = {
    lines
      .map(x => (x(4).takeRight(5).replaceAll("-", "").toInt, x(0)))
      .foreachRDD(x => x.sortBy(_._1, ascending = false).take(5).foreach(println))

    ssc.start()
    ssc.awaitTermination()
    ssc.stop()
  }
}

//手机号内8数量最多的前三位
object phoneNumCount8Top3 extends LoadSparkStreaming {
  def main(args: Array[String]): Unit = {
    lines
      .map(x => x(5).split(""))
      .map(x => x.count(_ == "8"))
      .foreachRDD(_.sortBy(x => x, ascending = false).take(3).foreach(println))
    ssc.start()
    ssc.awaitTermination()
    ssc.stop()
  }
}

//手机号各位加和最大值的前两位
object o16 extends LoadSparkStreaming {
  def main(args: Array[String]): Unit = {
    lines
      .map(x => (x(5).split("").map(y => y.toInt), x(5)))
      .map(x => (x._1.sum, x._2))
      .foreachRDD(_.top(2).foreach(println))
    ssc.start()
    ssc.awaitTermination()
    ssc.stop()
  }
}

object o17 extends LoadSparkStreaming {
  def main(args: Array[String]): Unit = {
    lines
      .map(x => ((x(5).split("").map(y => y.toInt), x(0)), 0))
      .map(x => (x._1._1.sum, x._2))
      .reduce((x, y) => if(x._1 < y._1) (y._1, x._2) else if(x._2 > y._1) (x._2, y._1) else (x._1, x._2)
       )
      .foreachRDD(_.top(2).foreach(println))
    ssc.start()
    ssc.awaitTermination()
    ssc.stop()
  }
}

//一.统计每个班男生的人数
object manNum extends LoadSparkStreaming {
  def main(args: Array[String]): Unit = {
    lines
      .filter(x => x(3) == "男")
      .map(x => (x(2), x(3)))
      .groupByKey()
      .mapValues(_.size)
      .print()
    //刘正作业专用
    ssc.start()
    ssc.awaitTermination()
    ssc.stop()
  }
}

//二.统计家不是郑州的学生名单
object houseNotInZhengZhou extends LoadSparkStreaming {
  def main(args: Array[String]): Unit = {
    lines
      .map(x => (x(0), x(6).substring(3, 6)))
      .filter(_._2 != "郑州市")
      .print()
    //刘正作业专用
    ssc.start()
    ssc.awaitTermination()
    ssc.stop()
  }
}

//三.统计手机号后四位有4的男女生人数
object phoneNumLast4 extends LoadSparkStreaming {
  def main(args: Array[String]): Unit = {
    lines
      .map(x => (x(3), x(5).takeRight(4)))
      .filter(_._2.contains("4"))
      .groupByKey()
      .mapValues(_.size)
      .print()
    //刘正作业专用
    ssc.start()
    ssc.awaitTermination()
    ssc.stop()
  }
}

//四.统计成绩在80分以上（包括80）的学生家在哪个市的多
object cityDistribution extends LoadSparkStreaming {
  def main(args: Array[String]): Unit = {
    lines
      .filter(_(7).toInt >= 80)
      .map(x => (x(6).substring(3, 6), 1))
      .groupByKey()
      .mapValues(_.size)
      .foreachRDD(_.sortBy(_._2, ascending = false).take(3).foreach(println))
    //刘正作业专用
    ssc.start()
    ssc.awaitTermination()
    ssc.stop()
  }
}

//成绩均值
object scoreAvg extends LoadSparkStreaming {
  def main(args: Array[String]): Unit = {
    lines
      .map(x => (x(7).toInt, 1))
      .reduce((x, y) => (x._1 + y._1, x._2 + y._2))
      .map(x => x._1.toFloat / x._2)
      .print()

    ssc.start()
    ssc.awaitTermination()
    ssc.stop()
  }
}

// 学号后四位跟生日拼接后的学生 如0001 + 0421 == 0422
object oo1 extends LoadSparkStreaming {
  def main(args: Array[String]): Unit = {
    lines
      .map(x => (x(1).takeRight(4).toInt + x(4).replaceAll("-", "").substring(4).toInt, x(0)))
      .foreachRDD(_.sortBy(x => x._1, ascending = false).foreach(println))

    ssc.start()
    ssc.awaitTermination()
    ssc.stop()
  }
}

// 索引河南省各市有哪些各县或区
object CityContainsCounty extends LoadSparkStreaming {
  def main(args: Array[String]): Unit = {
    lines
      .map(x => (x(6).take(3), x(6).substring(3, 6), x(6).substring(6)))
      .filter(x => x._1.equals("河南省"))
      .filter(_._3.length > 4)
      .map(x => (x._2, x._3.dropRight(4)))
      .map(x => {
        if (x._2.startsWith("市")) {
          (x._1 + x._2.take(1), x._2.substring(1))
        } else (x._1, x._2)
      })
      .filter(_._2.nonEmpty)
      .foreachRDD(_.distinct()
        .groupByKey()
        .map(x => (x._1, x._2.toList)).foreach(println)
      )

    ssc.start()
    ssc.awaitTermination()
    ssc.stop()
  }
}

// 手机号按位加和最大值前三位
object sumPhoneTop3 extends LoadSparkStreaming {
  def main(args: Array[String]): Unit = {
    lines
      .map(x => (x(5), x(5).split("")))
      .map(x => (x._1, x._2.map(_.toInt).sum))
      .foreachRDD(_.sortBy(_._2, ascending = false)
        .take(10)
        .foreach(println))

    ssc.start()
    ssc.awaitTermination()
    ssc.stop()
  }
}

object 两个流合并 extends LoadSparkStreaming {
  def main(args: Array[String]): Unit = {
    val s1 = lines
      .map(x => (x(0), x(1)))
    val s2 = lines
      .map(x => (x(0), x(3)))
    s1.join(s2)
      .print()


    ssc.start()
    ssc.awaitTermination()
    ssc.stop()
  }
}

object 一个流拆两个流 extends LoadSparkStreaming {
  def main(args: Array[String]): Unit = {
    val s1 = lines

    val s2 = s1.transform(x => x)
      .map(_(3))

    s1.print()
    s2.print()


    ssc.start()
    ssc.awaitTermination()
    ssc.stop()
  }
}

object 基本窗口 extends LoadSparkStreaming {
  def main(args: Array[String]): Unit = {
    lines.window(Seconds(6), Seconds(2))
      .print()

    ssc.start()
    ssc.awaitTermination()
    ssc.stop()
  }
}

object 窗口countByWindow extends LoadSparkStreaming {
  def main(args: Array[String]): Unit = {
    ssc.checkpoint(".checkpoint")
    lines.countByWindow(Seconds(4), Seconds(2))
      .print()

    ssc.start()
    ssc.awaitTermination()
    ssc.stop()
  }
}

object 窗口reduceByWindow extends LoadSparkStreaming {
  def main(args: Array[String]): Unit = {
    ssc.checkpoint(".checkpoint")
    lines
      .map(_(2))
      .reduceByWindow(
        _ + "_" + _,
        Seconds(4),
        Seconds(2))
      .print()

    ssc.start()
    ssc.awaitTermination()
    ssc.stop()
  }
}

object 基本StructureSQL extends LoadSparkStreaming {
  def main(args: Array[String]): Unit = {
    val ss = SparkSession.builder().master("local[1]")
      .getOrCreate()
    ss.sparkContext.setLogLevel("ERROR")
    val lines = ss.readStream
      .format("socket")
      .option("host", "localhost")
      .option("port", 7777)
      .load()
    val table = lines.selectExpr("split(value,'\t') as stu")
      .withColumn("name", expr("stu[0]"))
      .withColumn("no", expr("stu[1]"))
      .withColumn("clz", expr("stu[2]"))
      .withColumn("gender", expr("stu[3]"))
      .withColumn("birthday", expr("stu[4]"))
      .withColumn("phone", expr("stu[5]"))
      .withColumn("loc", expr("stu[6]"))
      .withColumn("score", expr("stu[7]"))
      .drop("stu")
      .createTempView("students")
    val query = ss.sql("select * from students")
    val running = query.writeStream
      .outputMode("append")
      .format("console")
      .trigger(Trigger.ProcessingTime("1 seconds"))
      .start()
    running.awaitTermination()
  }
}

//很慢
object 统计男女生人数 extends LoadSparkStreaming {
  def main(args: Array[String]): Unit = {
    val ss = SparkSession.builder().master("local[1]")
      .getOrCreate()
    ss.sparkContext.setLogLevel("ERROR")
    val lines = ss.readStream
      .format("socket")
      .option("host", "localhost")
      .option("port", 7777)
      .load()
    val table = lines.selectExpr("split(value,'\t') as stu")
      .withColumn("name", expr("stu[0]"))
      .withColumn("no", expr("stu[1]"))
      .withColumn("clz", expr("stu[2]"))
      .withColumn("gender", expr("stu[3]"))
      .withColumn("birthday", expr("stu[4]"))
      .withColumn("phone", expr("stu[5]"))
      .withColumn("loc", expr("stu[6]"))
      .withColumn("score", expr("stu[7]"))
      .drop("stu")
      .createTempView("students")
    val query = ss.sql("select gender, count(gender) from students group by gender")
    val running = query.writeStream
      .outputMode("complete")
      .format("console")
      .trigger(Trigger.ProcessingTime("1 seconds"))
      .start()
    running.awaitTermination()
  }
}


object Kafka基本Producer {
  def main(args: Array[String]): Unit = {
    val props = new Properties()
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.137.150:9092")
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer")
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer")
    val producer = new KafkaProducer[String, String](props)
    producer.send(new ProducerRecord[String, String]("topic-1", "key1", "value1"))
    producer.send(new ProducerRecord[String, String]("topic-1", "key1", "value1"))
    producer.send(new ProducerRecord[String, String]("topic-1", "key1", "value1"))
    producer.send(new ProducerRecord[String, String]("topic-1", "key2", "value2"))
    producer.send(new ProducerRecord[String, String]("topic-1", "key3", "value3"))
    producer.close()
  }
}

object Kafka基本Consumer {
  def main(args: Array[String]): Unit = {
    val props = new Properties()
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.137.150:9092")
    props.put(ConsumerConfig.GROUP_ID_CONFIG, "group-1")
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer")
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer")
    val consumer = new KafkaConsumer[String, String](props)
    consumer.subscribe(util.Arrays.asList("topic-1"))
    try {
      while (true) {
        val records = consumer.poll(Duration.ofSeconds(1))
        records.forEach(record => println(s"offset = ${record.offset}, key = ${record.key}, value = ${record.value}"))
      }
    } finally {
      consumer.close()
    }
  }
}

object 基本SparkStreaming连接Kafka extends LoadSparkStreaming {
  def main(args: Array[String]): Unit = {
    val kafkaParams = Map[String, Object](
      "bootstrap.servers" -> "zzti:9092",
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[StringDeserializer],
      "group.id" -> "group-1",
      "auto.offset.reset" -> "latest",
      "enable.auto.commit" -> (true: java.lang.Boolean)
    )
    val topics = Set("topic-1")
    val stream = KafkaUtils.createDirectStream[String, String](
      ssc,
      PreferConsistent,
      Subscribe[String, String](topics, kafkaParams)
    )
    //算子
    stream.foreachRDD(x => x.foreach(println))
    ssc.start()
    ssc.awaitTermination()
    ssc.stop()
  }
}
