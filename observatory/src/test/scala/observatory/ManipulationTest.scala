package observatory

import org.scalatest.FunSuite
import org.scalatest.prop.Checkers

trait ManipulationTest extends FunSuite with Checkers {

  test("makeGrid simple") {
    val temperatures = List((Location(-89, 179), 2d))
    val grid = Manipulation.makeGrid(temperatures)
    assert(grid(80, -89) === 2d)
  }

  test("makeGrid 2 location") {
    import org.scalactic.Tolerance._
    val temperatures = List((Location(-89, 179), 100d), (Location(90, -180), 0d))
    val grid = Manipulation.makeGrid(temperatures)
    assert(grid(0, 0) +- 2 === 50d)
  }

}