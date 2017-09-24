package observatory

import org.scalatest.FunSuite
import org.scalatest.prop.Checkers

//import scala.collection.concurrent.TrieMap

trait InteractionTest extends FunSuite with Checkers {

  test("tileLocation of zoom 1, x 0 and y 0") {
    val expectedLocation = Location(85.0511287798066,-180.0)

    assert(Interaction.tileLocation(1, 0, 0) === expectedLocation)
  }

}
