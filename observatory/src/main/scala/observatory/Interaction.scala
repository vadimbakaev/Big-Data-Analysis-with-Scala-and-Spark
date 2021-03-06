package observatory

import java.lang.Math._

import com.sksamuel.scrimage.{Image, Pixel, RGBColor}
import observatory.Visualization.{interpolateColor, predictTemperature}

import java.util.concurrent._

/**
  * 3rd milestone: interactive visualization
  */
object Interaction {

  /**
    * @param zoom Zoom level
    * @param x    X coordinate
    * @param y    Y coordinate
    * @return The latitude and longitude of the top-left corner of the tile, as per http://wiki.openstreetmap.org/wiki/Slippy_map_tilenames
    */
  def tileLocation(zoom: Int, x: Int, y: Int): Location = {
    val twoPowZ = pow(2, zoom)
    val lat = atan(sinh(PI - (y / twoPowZ) * 2 * PI)) * (180 / PI)
    val lon = ((x / twoPowZ) * 360) - 180

    Location(lat, lon)
  }

  /**
    * @param temperatures Known temperatures
    * @param colors       Color scale
    * @param zoom         Zoom level
    * @param x            X coordinate
    * @param y            Y coordinate
    * @return A 256×256 image showing the contents of the tile defined by `x`, `y` and `zooms`
    */
  def tile(
            temperatures: Iterable[(Location, Double)],
            colors: Iterable[(Double, Color)],
            zoom: Int,
            x: Int,
            y: Int
          ): Image = {

    val alpha = 127
    val (w, h) = (256, 256)
    val fromX = x * w
    val toX = fromX + w
    val fromY = y * h
    val toY = fromY + h

    val pixels = for {
      y1 <- (fromY until toY).par
      x1 <- (fromX until toX).par
    } yield {
      val maxZoom = zoom + 8
      val color = interpolateColor(colors, predictTemperature(temperatures, tileLocation(maxZoom, x1, y1)))

      Pixel(RGBColor(color.red, color.green, color.blue, alpha))
    }

    Image(w, h, pixels.toArray)
  }

  /**
    * Generates all the tiles for zoom levels 0 to 3 (included), for all the given years.
    *
    * @param yearlyData    Sequence of (year, data), where `data` is some data associated with
    *                      `year`. The type of `data` can be anything.
    * @param generateImage Function that generates an image given a year, a zoom level, the x and
    *                      y coordinates of the tile and the data to build the image from
    */
  def generateTiles[Data](
                           yearlyData: Iterable[(Int, Data)],
                           generateImage: (Int, Int, Int, Int, Data) => Unit
                         ): Unit = {
    for {
      (year, data) <- yearlyData.par
      zoom         <- (0 to 3).par
      y            <- (0 until pow(2, zoom).toInt).par
      x            <- (0 until pow(2, zoom).toInt).par
    } {
      generateImage(year, zoom, x, y, data)
    }

  }

}
