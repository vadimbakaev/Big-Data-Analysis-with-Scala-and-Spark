package observatory

import java.time.LocalDate

import org.scalatest.FunSuite

class ExtractionTest extends FunSuite {

  test("stationID2LocationMap should return emptyMap") {
    assert(Extraction.stationID2LocationMap(Nil.iterator).isEmpty)
  }

  test("stationID2LocationMap should return Map with 2 items") {
    val input = List(
      "010013,,,",
      "724017,03707,+37.358,-078.438",
      "724017,,+37.350,-078.433"
    ).iterator
    val expectedValue = Map(
      ("724017", "03707") -> Location(37.358d, -078.438d),
      ("724017", "") -> Location(37.350d, -078.433d)
    )

    assert(Extraction.stationID2LocationMap(input) === expectedValue)
  }

  test("fahrenheitToCelsius should return 2.0C for 35.6F") {
    assert(Extraction.fahrenheitToCelsius(35.6d) === 2.0d)
  }

  test("localDateWithLocationAndTempInCelsius should return 3 items with temp in celsius") {
    val input = List(
      "010013,,11,25,39.2",
      "724017,,08,11,81.14",
      "724017,03707,12,06,32",
      "724017,03707,01,29,35.6",
      "724017,03707,01,29,9999.9"
    ).iterator

    val expectedValue = List(
      (LocalDate.of(2017, 8, 11), Location(37.35, -78.433), 27.3d),
      (LocalDate.of(2017, 12, 6), Location(37.358, -78.438), 0d),
      (LocalDate.of(2017, 1, 29), Location(37.358, -78.438), 2.0d)
    )

    val map = Map(
      ("724017", "") -> Location(37.35, -78.433),
      ("724017", "03707") -> Location(37.358, -78.438)
    )

    assert(Extraction.localDateWithLocationAndTempInCelsius(input, map, 2017) === expectedValue)
  }

  test("locationYearlyAverageRecords should return 2 items with average temp") {
    val input = List(
      (LocalDate.of(2017, 8, 11), Location(37.35, -78.433), 27.3d),
      (LocalDate.of(2017, 12, 6), Location(37.358, -78.438), 0d),
      (LocalDate.of(2017, 1, 29), Location(37.358, -78.438), 2.0d)
    )

    val expectedValue = Seq(
      (Location(37.35, -78.433), 27.3),
      (Location(37.358, -78.438), 1.0)
    )

    assert(Extraction.locationYearlyAverageRecords(input).toList.diff(expectedValue).isEmpty)
  }

}