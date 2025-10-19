
class LoadData{
  val data=scala.io.Source
    .fromFile("input/students.data","utf8")
    .getLines()
    .toList
    .map(_.trim().split("\t"))
    .filter(_.length==8)
}
object base extends LoadData{
  def main(args: Array[String]): Unit = {

  }
}