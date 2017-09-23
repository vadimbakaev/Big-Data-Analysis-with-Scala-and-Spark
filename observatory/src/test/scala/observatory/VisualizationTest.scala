package observatory


import org.scalatest.FunSuite
import org.scalatest.prop.Checkers

trait VisualizationTest extends FunSuite with Checkers {

  test("greatCircleDistance return ZERO for distance from itself") {
    val from = Location(37.358d, -078.438d)
    val to = from

    assert(Visualization.greatCircleDistance(from, to) === 0d)
  }

  test("greatCircleDistance return distance form point to center") {
    import org.scalactic.Tolerance._

    val from = Location(37.358, -078.438)
    val to = Location(0d, 0d)

    val expectedResult = 8990716d / 6371000d
    assert(Visualization.greatCircleDistance(from, to) +- 0.001 === expectedResult )
  }

}
