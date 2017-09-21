package observatory

import java.time.LocalDate

import scala.io.Source._

/**
  * 1st milestone: data extraction
  */
object Extraction {

  /**
    * @param year             Year number
    * @param stationsFile     Path of the stations resource file to use (e.g. "/stations.csv")
    * @param temperaturesFile Path of the temperatures resource file to use (e.g. "/1975.csv")
    * @return A sequence containing triplets (date, location, temperature)
    */
  def locateTemperatures(
                          year: Int,
                          stationsFile: String,
                          temperaturesFile: String
                        ): Iterable[(LocalDate, Location, Double)] = {

    val stationFileLines = fromInputStream(ClassLoader.getSystemResourceAsStream(stationsFile)).getLines()
    val temperaturesFileLines = fromInputStream(ClassLoader.getSystemResourceAsStream(temperaturesFile)).getLines()

    val stationId2Location = stationID2LocationMap(stationFileLines)
    localDateWithLocationAndTempInCelsius(temperaturesFileLines, stationId2Location, year)
  }

  /**
    * @param records A sequence containing triplets (date, location, temperature)
    * @return A sequence containing, for each location, the average temperature over the year.
    */
  def locationYearlyAverageRecords(
                                    records: Iterable[(LocalDate, Location, Double)]
                                  ): Iterable[(Location, Double)] = {
    records.groupBy { case (_, location, _) => location }
      .mapValues { iterableOfDateWithLocationAndTemp =>
        iterableOfDateWithLocationAndTemp.foldLeft(0d)(_ + _._3) / iterableOfDateWithLocationAndTemp.size
      }
  }

  def stationID2LocationMap(stationFileLines: Iterator[String]): Map[(String, String), Location] = {
    stationFileLines
      .map(_.split(","))
      .filter(_.forall(_.nonEmpty))
      .map { case Array(stnID, wbanID, latitude, longitude) =>
        ((stnID, wbanID), Location(latitude.toDouble, longitude.toDouble))
      }
      .toMap
  }

  def localDateWithLocationAndTempInCelsius(
                                             temperaturesFileLines: Iterator[String],
                                             stationId2Location: Map[(String, String), Location],
                                             year: Int
                                           ): List[(LocalDate, Location, Double)] = {
    temperaturesFileLines
      .map(_.split(","))
      .filter(arr => arr.forall(_.nonEmpty) && arr(4) != "9999.9")
      .map { case Array(stnID, wbanID, moth, dayOfMonth, temp) =>
        (
          LocalDate.of(year, moth.toInt, dayOfMonth.toInt),
          stationId2Location((stnID, wbanID)),
          fahrenheitToCelsius(temp.toDouble)
        )
      }
      .toList
  }

  private def fahrenheitToCelsius(f: Double): Double = (f - 32.0) * (5.0 / 9.0)

}
