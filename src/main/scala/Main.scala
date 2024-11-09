import cats.effect.{ExitCode, IO, IOApp}
import cats.implicits.{catsSyntaxParallelFlatTraverse, catsSyntaxParallelTraverse}

object Main extends IOApp {
  type PyramidRow = Array[Int]
  type Pyramid = Seq[PyramidRow]
  
  object Pyramid {
    val empty: Pyramid = Seq.empty
  }
  
  type Path = List[Int]
  
  case class Convolution(path: Path, idx: Int)
  
  type Tree = List[Convolution]
  
  implicit class TreeWrap(tree: Tree) {
    def expand(row: PyramidRow): IO[Tree] =
      tree.parFlatTraverse { c =>
        IO(List(
          Convolution(path = row(c.idx) :: c.path, idx = c.idx),
          Convolution(path = row(c.idx + 1) :: c.path, idx = c.idx + 1),
        ))
      }
  }
  
  object Tree {
    val empty: Tree = List.empty
    
    def fromPyramidRow(row: PyramidRow): Tree = {
      row.zipWithIndex.map { r =>
        Convolution(path = List(r._1), idx = r._2)
      }.toList
    }
  }
  
  val readLine: IO[String] = IO(scala.io.StdIn.readLine())
  
  def parseLine(line: String): IO[Array[Int]] =
    for {
      rawNodes <- if (line.isEmpty) IO.pure(Array.empty[String]) else IO(line.split(" "))
      nodes <- if (rawNodes.isEmpty) IO.pure(Array.empty[Int]) else IO(rawNodes.map(_.toInt))
    } yield nodes
  
  def buildPyramid(pyramid: Pyramid = Pyramid.empty, rowIndex: Int = 1): IO[Pyramid] =
    for {
      line <- readLine
      row <- parseLine(line)
      rez <- if (row.nonEmpty) {
        if (row.length == rowIndex) {
          buildPyramid(pyramid :+ row, rowIndex + 1)
        } else
          IO.raiseError(new RuntimeException(s"Number of elements [${row.length}] in the last row doesn't conform to pyramid's row index [$rowIndex]"))
      } else {
        IO.pure(pyramid)
      }
    } yield rez
  
  def buildTree(pyramid: Pyramid, tree: Tree): IO[Tree] =
    IO.suspend {
      if (pyramid.isEmpty) {
        IO.pure(tree)
      } else {
        for {
          newTree <- tree.expand(pyramid.head)
          rez <- buildTree(pyramid.tail, newTree)
        } yield rez
      }
    }
  
  def findPath(tree: Tree): IO[Unit] =
    for {
      sums <- tree.parTraverse(c => IO(c.path.sum, c))
      sorted <- IO(sums.sortBy(_._1))
      min <- IO.pure(sorted.head)
      minPath <- IO.pure(min._2.path).map(_.reverse.mkString(" + "))
      minSum <- IO.pure(min._1)
      _ <- IO(println(s"Minimal path: $minPath = $minSum"))
    } yield ()
  
  override def run(args: List[String]): IO[ExitCode] =
    (for {
      _ <- IO(println("Building pyramid..."))
      pyramid <- buildPyramid()
      _ <- IO(println("Building path tree..."))
      tree <- if (pyramid.nonEmpty) {
        buildTree(pyramid.tail, Tree.fromPyramidRow(pyramid.head))
      } else IO.pure(Tree.empty)
      _ <- IO(println("Finding shortest path..."))
      _ <- findPath(tree)
    } yield ()).as(ExitCode.Success)
}
