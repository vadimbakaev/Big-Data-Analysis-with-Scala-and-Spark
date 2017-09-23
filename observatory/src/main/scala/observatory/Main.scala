package observatory

import java.io.File
import java.time.LocalDate

object Main extends App {

  private val data: Iterable[(LocalDate, Location, Double)] = Extraction.locateTemperatures(2015, "/stations.csv", "/2015.csv")
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
  private val image                                         = Visualization.visualize(tepleratures, colors)
  image.output(new File("target/some-image.png"))
}
