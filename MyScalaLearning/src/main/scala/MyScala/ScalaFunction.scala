package MyScala

import scala.collection.mutable.ArrayBuffer
import scala.io.Source

object ListDemo1 {
  def main(args: Array[String]): Unit = {

    val list: List[Int] = List(1, 2, 3, 4, 5)

    //1.获取集合长度
    println(list.length)

    //2.获取集合大小
    println(list.size)

    //3.循环遍历
    list.foreach(println)

    //4.迭代遍历
    for (elem <- list.iterator) {
      println(elem)
    }

    //5.生成字符串
    println(list.mkString(","))

    //6.是否包含
    println(list.contains(3))
  }
}

object ListDemo2 {
  def main(args: Array[String]): Unit = {

    val list1: List[Int] = List(1, 2, 3, 4, 5, 6, 7)
    val list2: List[Int] = List(4, 5, 6, 7, 8, 9, 10)

    //1.获取集合的头
    println(list1.head)

    //2.获取集合的尾(除了头的部分都是尾)
    println(list1.tail)

    //3.获取集合的最后一个元素
    println(list1.last)

    //4.集合反转
    println(list1.reverse)

    //5.取集合的前n个数
    println(list1.take(3))
    println(list1.take(3).mkString(","))

    //6.取集合的后n个数
    println(list1.takeRight(3))
    println(list1.takeRight(3).mkString(",").reverse)

    //7.去掉前(后)n个元素
    println(list1.drop(2))
    println(list1.dropRight(2))
    println(list1)

    //8.拉链
    println(list1.zip(list2))

    //9.滑窗
    list1.sliding(3, 5).foreach(println)
  }
}

object ListDemo3 {
  def main(args: Array[String]): Unit = {

    val list: List[Int] = List(1, 5, -3, 4, 2, -7, 6)

    //1.求和
    println(list.sum)

    //2.求乘积
    println(list.product)

    //3.最大值
    println(list.max)

    //4.最小值
    println(list.min)

    //5.排序
    println(list.sorted)
    println(list.sortBy(x => x))
    println(list.sortBy(_.abs)) //按绝对值排序
  }
}

object ListDemo4 {
  def main(args: Array[String]): Unit = {

    val list: List[Int] = List(1, 2, 3, 4, 5, 6, 7, 8, 9)
    val nestedList: List[List[Int]] = List(List(1, 2, 3), List(4, 5, 6), List(7, 8, 9))
    val wordList: List[String] = List("hello world", "hello scala", "hello spark")


    println(list)

    //1.过滤
    println(list.filter(x => x % 2 == 0))

    //2.转化/映射
    println(list.map(x => x + 1))
    println(list)

    //3.扁平化
    println(nestedList)
    println(nestedList.flatten)

    //4.扁平化+映射
    println(wordList)
    println(wordList.flatMap(x => x.split(" ")))

    //5.分组
    println(list.groupBy(x => x % 3))

    //6.fold
    val i = list.foldLeft(1)((x, y) => x - y)
    val i1 = list.foldRight(10)((x, y) => x - y)
    println(i)
    println(i1)
  }
}

object WordCountDemo {
  def main(args: Array[String]): Unit = {

    val stringList = List("Hello Scala Habse Kafka", "Hello Scala Habse", "Hello Scala", "Hello")
    println(stringList)

    //1.将每一个字符串转化成单个的单词
    val wordList = stringList.flatMap(str => str.split(" "))
    println(wordList)

    //2.将相同的单词放置在一起
    val wordToWordsMap = wordList.groupBy(word => word)
    println(wordToWordsMap)

    //3.对相同的单词进行计数
    val wordToCountMap = wordToWordsMap.map(x => (x._1, x._2.length))
    println(wordToCountMap)

    //4.排序
    val sortList = wordToCountMap.toList.sortBy(x => x._2).reverse
    println(sortList)

    val resultList = sortList.mkString(",")
    println(resultList)

    println(stringList
      .flatMap(x => x.trim.split(" "))
      .groupBy(x => x)
      .map(x => (x._1, x._2.length))
      .toList.sortBy(x => x._2)
      .reverse
      .mkString(",")
    )


    println(stringList
      .flatMap(_.trim().split(" "))
      .map((_, 1))
      .groupBy(_._1)
      .map(x => (x._1, x._2.length))
      .toList
      .sortBy(_._2)
      .reverse
      .mkString(",")
    )
  }
}

object WordCountDemo2 {
  def main(args: Array[String]): Unit = {

    val source = Source
      .fromFile("input/books/the_old_man_and_the_sea.txt", "UTF-8")
      .getLines()
      .toList

    source
      .flatMap(_.trim.split(" "))
      .groupBy(x => x)
      .map(x => (x._1, x._2.length))
      .toList.sortBy(x => -x._2)
      .take(20)
      .foreach(println)
  }
}

object matchDemo {
  def main(args: Array[String]): Unit = {

    def abs(x: Int) = x match {
      case i if i >= 0 => i
      case j if j < 0 => -j
    }

    println(abs(-5))


    var a = 10
    var b = 2
    var operator = '/'

    var result = operator match {
      case '+' => a + b
      case '-' => a - b
      case '*' => a * b
      case '/' if b == 0 => "除零错误"
      case '/' => a / b
      case _ => "illegal"
    }
    println(result)
  }
}

object FunctionDemo1 {
  def main(args: Array[String]): Unit = {

    def sum(x: Int, y: Int): Int = {
      x + y
    }

    def test1(): Unit = {
      println("无参，无返回值")
    }

    test1
    test1()

    def test2(): String = {
      "无参，有返回值"
    }

    println(test2)
    println(test2())

    def test3(s: String): Unit = {
      println(s)
    }

    test3("dalang")

    def test4(s: String): String = {
      s + " 有参，有返回值"
    }

    println(test4("dalang"))


    def test5(a: Int, b: Int) = {
      println(a + b)
      "A"
    }

    println(test5(1, 6))


    def test6(a: Int, b: Int) {
      println(a + b)
      "a"
    }

    test6(1, 6)

    def f1(f: (Int, Int) => Int) = {
      println(f(1, 3))
    }

    var f2 = (a: Int, b: Int) => a + b
    //println(f2(6,4))
    f1(f2)

    def operation(arr: Array[Int], op: Int => Int) = {
      for (elem <- arr) yield op(elem)
    }

    println(operation(Array(1, 2, 3, 4, 5), _ + 1).mkString(","))

    def calculator(a: Int, b: Int, op: (Int, Int) => Int) = op(a, b)

    println(calculator(3, 6, _ + _))

    def map(arr: Array[Int], op: Int => Int) = {
      for (elem <- arr) yield op(elem)
    }

    println(operation(Array(1, 2, 3, 4, 5), x => x * x).mkString(","))

  }
}

object arrDemo1 {
  def main(args: Array[String]): Unit = {

    val arr01 = new Array[Int](4)

    println(arr01.length)

    arr01(3) = 10
    arr01.update(0, 1)
    println(arr01.mkString(","))

    arr01.foreach(println)

    println((arr01 :+ 5).mkString(","))

    val arr02 = ArrayBuffer[Any](1, 2, 3)

    println(arr02.mkString(","))
    arr02.+=(4)
    println(arr02.mkString(","))
    arr02.append(5, 6)
    println(arr02.mkString(","))
    arr02.insert(0, 7, 8)
    println(arr02.mkString(","))
    println(arr02.hashCode())
    arr02(1) = 9
    println(arr02.mkString(","))
    println(arr02.length)

    val set = Set(1, 2, 3, 4, 5)
    val set1 = Set(1, 2, 2)
    val set2 = set + (6, 7)
    println(set)
    println(set1)
    println(set2)
    set.foreach(println)


    val map = Map("a" -> 1, "b" -> 2, "c" -> 3)
    val map1 = Map(("a", 1), ("b", 2), ("c", 3))

    map.foreach(tuple => {
      println(tuple._1 + " = " + tuple._2)
    })
    map1.foreach(println)

    def f() = {
      println("我是f方法")
      "张三"
    }

    lazy val name = f()
    println("哈哈哈")
    println(name)
  }
}

object foldDemo {
  def main(args: Array[String]): Unit = {

    val nums = List(2, 3, 4)
    val sum = nums.fold(1)((x, y) => x + y)
    println(sum) //10
    val num2 = List(2.0, 3.0)
    val result1 = num2.foldLeft(4.0)(math.pow)
    val result2 = num2.foldRight(1.0)(math.pow)
    println(result1) //4096.0
    println(result2) //8.0

    val nums1 = List(1, 2, 3)
    val nums2 = List(2, 3, 4)
    val diff1 = nums1.diff(num2)
    val diff2 = nums2.diff(nums1)
    println(diff1)
    println(diff2)
    //并集
    val union1 = nums1.union(nums2)
    println(union1)
    val union2 = nums2 ++ nums1
    println(union2)
    //交集
    val intersect1 = nums1.intersect(num2)
    println(intersect1)

    //去重
    val list = List("A", "B", "C", "B", "A", "C", "C", "A", "C", "B", "A", "B", "C", "A", "C", "B", "A", "B", "C", "B")
    println(list.distinct)
    //分组
    println(list.groupBy(x => x))
    //计数
    println(list.groupBy(x => x)
      .map(x => (x._1, x._2.length))
    )
    //排序
    println(list.groupBy(x => x)
      .map(x => (x._1, x._2.length))
      .toList.sortBy(-_._2)
    )
    println(list.groupBy(x => x)
      .map(x => (x._1, x._2.length))
      .toList.sortBy(-_._2)
      .mkString(",")
    )

    val nums11 = List(1, 2, 3)
    val result11 = nums11.scan(10)(_ + _)
    println(result11) //List(10, 11, 13, 16)

    val result12 = nums11.scanLeft(10)(_ + _)
    println(result12) //List(10, 11, 13, 16)

    val result13 = nums11.scanRight(10)(_ + _)
    println(result13) //List(16, 15, 13, 10)


  }
}

object operatorDemo {
  def main(args: Array[String]): Unit = {
    val nums1 = List(1, 1, 1, 1, 4, 4, 4, 4)
    val left1 = nums1.take(4)
    val right1 = nums1.takeRight(4)
    val headNums = nums1.takeWhile(_ == nums1.head)
    println(left1)
    println(right1)
    println(headNums)

    val left2 = nums1.drop(4)
    val right2 = nums1.dropRight(4)
    val tailNums = nums1.dropWhile(_ == nums1.head)
    println(left2)
    println(right2)
    println(tailNums)

    val genderList = List("男", "男", "女", "男", "男", "女", "女", "男", "女", "男", "男", "女")
    println(genderList
      .flatMap(_.trim.split(""))
      .groupBy(x => x)
      .map(x => (x._1, x._2.length))
      .toList.sortBy(-_._2)
      .mkString(",")
    )

    val nums2 = List(1, 1, 1, 2, 3, 2, 1)
    val (perfix1, suffix1) = nums2.span(_ == 1)
    val (perfix2, suffix2) = nums2.splitAt(3)
    val (perfix3, suffix3) = nums2.partition(_ == 1)
    println((perfix1, suffix1))
    println((perfix2, suffix2))
    println((perfix3, suffix3))
    println(nums2.partition(_ == 1))

  }
}

object practice {
  def main(args: Array[String]): Unit = {

    val source = Source
      .fromFile("input/students.data")
      .getLines()
      .toList
      .map(_.trim.split("\t"))
      .filter(_.length == 8)

    //1.统计男女生人数
    source
      .groupBy(x => x(3))
      .map(x => (x._1, x._2.length))
      .foreach(println)


    //2.求出生年月最大值
    val dates = source
      .map(x => x(4).replace("-", "").toInt)
    //.foreach(println)

    println(dates.max)
    println(dates.min)

    //3.求学生中出现的所有姓氏（去重）
    source
      .map(_(0).substring(0,1).distinct)
      .foreach(println)

    //4.求年龄平均值
    val age =  source.map(2025 - _(4).substring(0,4).toInt)
    println(age.sum / age.length)

    //5.求学生的年龄分布
    source
      .map(2025 - _(4).substring(0,4).toInt)
      .groupBy(x => x)
      .map(x => (x._1, x._2.length))
      .foreach(println)

    //6.按同学们的电话号码大小排序
    source
      .map(_(5).toLong)
      .sorted.reverse
      .foreach(println)

    //7.索引出相同生日下的同学链表
    source
      .map(x => (x(0), x(4).substring(5)))
      .groupBy(_._2)
      //.mapValues(_.map(_._1))
      .map(x => (x._1, x._2.map(_._1)))
      .foreach(println)

    //8.求生日最大的五个同学的名字
    source
      .map(x => (x(0), x(4).substring(5)))
      .sortBy(x => x._2).reverse.take(5)
      .map(_._1)
      .foreach(println)
  }
}