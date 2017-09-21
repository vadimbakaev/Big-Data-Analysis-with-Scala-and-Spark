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
      .map(_.split(",").toList)
      .filter(arr => arr.length == 4 && arr.drop(2).forall(_.nonEmpty))
      .map { case List(stnID, wbanID, latitude, longitude) =>
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
      .filter(arr => arr.length == 5 && arr(4) != "9999.9")
      .map { case Array(stnID, wbanID, moth, dayOfMonth, temp) =>
        (stationId2Location.get((stnID, wbanID)), moth, dayOfMonth, temp)
      }
      .filter(_._1.isDefined)
      .map { case (maybeLocation, moth, dayOfMonth, temp) =>
        (
          LocalDate.of(year, moth.toInt, dayOfMonth.toInt),
          maybeLocation.get,
          fahrenheitToCelsius(temp.toDouble)
        )
      }
      .toList
  }

  def fahrenheitToCelsius(f: Double): Double =
    ((BigDecimal(f) - BigDecimal(32.0)) * (BigDecimal(5.0) / BigDecimal(9.0))).toDouble

}
