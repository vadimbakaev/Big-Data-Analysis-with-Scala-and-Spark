package observatory

import java.io.File
import java.time.LocalDate

object Main extends App {

  private val year                                          = 2015
  private val data: Iterable[(LocalDate, Location, Double)] = Extraction.locateTemperatures(year, "/stations.csv", "/2015.csv")
  private val tepleratures                                  = Extraction.locationYearlyAverageRecords(data)
  private val colors                                        = Map(
    (60d, Color(225, 225, 225)),
    (32d, Color(225, 0, 0)),
    (12d, Color(225, 225, 0)),
    (0d, Color(0, 225, 225)),
    (-15d, Color(0, 0, 225)),
    (-27d, Color(225, 0, 225)),
    (-50d, Color(33, 0, 107)),
    (-60d, Color(0, 0, 0))
  )
  private val zoom                                          = 1
  private val x                                             = 0
  private val y                                             = 1
  //  private val image                                         = Visualization.visualize(tepleratures, colors)
  private val image                                         = Interaction.tile(tepleratures, colors, zoom, x, y)
  private val file = new File(s"target/temperatures/$year/$zoom/$x-$y.png")
  file.createNewFile()
  image.output(file)
}
