package observatory

import org.scalatest.FunSuite
import org.scalatest.prop.Checkers

trait InteractionTest extends FunSuite with Checkers {

  test("tileLocation of zoom 0, x 0 and y 0") {
    val expectedLocation = Location(85.0511287798066,-180.0)

    assert(Interaction.tileLocation(0, 0, 0) === expectedLocation)
  }

  test("tileLocation of zoom 1, x 0 and y 0") {
    val expectedLocation = Location(85.0511287798066,-180.0)

    assert(Interaction.tileLocation(1, 0, 0) === expectedLocation)
  }

  test("tileLocation of zoom 256, x 0 and y 0") {
    val expectedLocation = Location(85.0511287798066,-180.0)

    assert(Interaction.tileLocation(256, 0, 0) === expectedLocation)
  }

  test("tileLocation of zoom 0, x 1 and y 0") {
    val expectedLocation = Location(85.0511287798066, 180.0)

    assert(Interaction.tileLocation(0, 1, 0) === expectedLocation)
  }

  test("tileLocation of zoom 1, x 1 and y 1") {
    val expectedLocation = Location(0, 0)

    assert(Interaction.tileLocation(1, 1, 1) === expectedLocation)
  }

  test("tileLocation of zoom 2, x 1 and y 1") {
    val expectedLocation = Location(66.51326044311186, -90)

    assert(Interaction.tileLocation(2, 1, 1) === expectedLocation)
  }

}
