package MyScala

import scala.collection.immutable.StringOps
import scala.io.StdIn
import scala.util.control.Breaks.{break, breakable}


class Person {
  var name: String = "ZhangSan"
}

object Hello {
  def main(args: Array[String]): Unit = {


    /*var n:Int = 130
    var b:Byte = n.toByte
    println(b)//-126

    def f1 : Unit = {
      println("Unit")
    }
    f1*/

    /*var n1 = 1.23456789f
    var n2 = 1.23456789
    println(n1)
    println(n2)


    var s1:StringOps = "aaa"

    var n = 150
    var b111:Byte = n.toByte
    println(b111)*/

    /*var num: Int = 2.6.toInt
    println(num)*/

    //var c2: Char = 'a' + 1
    // println(c2)  b

    /*// 1. 输入姓名
    println("输入姓名：")
    val name = StdIn.readLine()
    println(name)*/

    /*val s =
      """
        select
         1
           2
        3
        """
        //println(s)


    var p1 = new Person()
    p1.name = "lisi"
    p1.name = null
    p1 = null

    val p2 = new Person()
    p2.name = "WangWu"
    p2.name = null
    //  p2  = null

    var a = 10
    a = 20*/

    //val b = 20
    // b = 10 不行

    // 单行注释 //
    //println("hello scala")
    //println("helle scala")

    // 多行注释 /* */
    /*println("helle scala")
    println("helle scala")*/

    // 文档注释 /** **/
    /**
     * println("helle scala")
     * println("helle scala")
     */

    var c = 2
    var d = 4
    //c -= 1
    c <<= 3
    d <<= 4
    println(d)
    println(c) //左移相当于乘2 右移相当于除2 最小为0

  }
}

object forDemo {
  def main(args: Array[String]): Unit = {

    for (i <- 1 to 5 if i % 2 == 0) {
      print(i + " ")
    }
    println()

    for (i <- 1 to 10 by 2) {
      print(i + " ")
    }
    println()

    for (i <- 1 to 9) {
      for (j <- 1 to i) {
        print(j + " * " + i + " = " + i * j + "\t")
      }
      println()
    }

    println()

    var res = for (i <- 1 to 10) yield i * 2
    println(res)

    for (i <- 1 to 10 reverse) {
      println(i)
    }

    for (elem <- 1 to 10) {
      if (elem % 2 == 1) println(elem)
    }

    breakable {
      for (elem <- 1 to 10) {
        println(elem)
        if (elem == 5) break
      }
    }
  }
}
