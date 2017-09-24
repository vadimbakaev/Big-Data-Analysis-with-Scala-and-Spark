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
    assert(Visualization.greatCircleDistance(from, to) +- 0.001 === expectedResult)
  }

  test("interpolateColor between equidistant points") {
    val points = Seq(
      (0d, Color(100, 100, 100)),
      (1d, Color(0, 0, 0)),
      (3d, Color(44, 22, 11))
    )

    assert(Visualization.interpolateColor(points, 0.5d) === Color(50, 50, 50))
  }

  test("interpolateColor between 2 points") {
    val points = Seq(
      (0d, Color(100, 100, 100)),
      (4d, Color(0, 0, 0))
    )

    assert(Visualization.interpolateColor(points, 1d) === Color(75, 75, 75))
  }

  test("interpolateColor no greater point") {
    val points = Seq(
      (0d, Color(100, 100, 100))
    )

    assert(Visualization.interpolateColor(points, 1d) === Color(100, 100, 100))
  }

  test("interpolateColor no lower point") {
    val points = Seq(
      (4d, Color(0, 0, 0))
    )

    assert(Visualization.interpolateColor(points, 1d) === Color(0, 0, 0))
  }

  test("interpolateColor for 0 distant point greatest point") {
    val points = Seq(
      (-100.0, Color(255, 0, 0)),
      (34.65264354993715, Color(0, 0, 255))
    )

    assert(Visualization.interpolateColor(points, 34.65264354993715d) === Color(0, 0, 255))
  }

  test("interpolateColor for 0 distant point lowest point") {
    val points = Seq(
      (-100.0, Color(255, 0, 0)),
      (34.65264354993715, Color(0, 0, 255))
    )

    assert(Visualization.interpolateColor(points, -100) === Color(255, 0, 0))
  }

}
