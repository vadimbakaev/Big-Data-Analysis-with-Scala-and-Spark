package observatory

import java.lang.Math._

import com.sksamuel.scrimage.{Image, Pixel, RGBColor}

/**
  * 2nd milestone: basic visualization
  */
object Visualization {

  /**
    * @param temperatures Known temperatures: pairs containing a location and the temperature at this location
    * @param location     Location where to predict the temperature
    * @return The predicted temperature at `location`
    */
  def predictTemperature(
                          temperatures: Iterable[(Location, Double)],
                          location: Location
                        ): Double = {

    val powerParameter = 6
    val distance2Temperature = temperatures.par.map { case (toLocation, temperature) =>
      (greatCircleDistance(location, toLocation), temperature)
    }

    distance2Temperature.find(_._1 == 0).map(_._2).getOrElse {
      val (numerator, denominator) =
        distance2Temperature.filter(!_._1.isNaN).foldLeft((0d, 0d)) {
          case ((leftAcc, rightAcc), (distance, temperature)) =>
            val weight = 1d / Math.pow(distance, powerParameter)

            (leftAcc + weight * temperature, rightAcc + weight)
        }

      numerator / denominator
    }
  }

  /**
    * @param points Pairs containing a value and its associated color
    * @param value  The value to interpolate
    * @return The color that corresponds to `value`, according to the color scale defined by `points`
    */
  def interpolateColor(
                        points: Iterable[(Double, Color)],
                        value: Double
                      ): Color = {

    val (lts, gts) = points.par.partition { case (pointValue, _) => pointValue < value }
    val maybeLower = if (lts.nonEmpty) Some(lts.maxBy(_._1)) else None
    val maybeGrader = if (gts.nonEmpty) Some(gts.minBy(_._1)) else None

    (maybeLower, maybeGrader) match {
      case (Some((lowerTemp, lowerColor)), Some((graderTemp, graderColor))) =>
        val denominator = graderTemp - lowerTemp
        val left = graderTemp - value
        val right = value - lowerTemp

        val r = Math.round((left * lowerColor.red + right * graderColor.red) / denominator).toInt
        val g = Math.round((left * lowerColor.green + right * graderColor.green) / denominator).toInt
        val b = Math.round((left * lowerColor.blue + right * graderColor.blue) / denominator).toInt

        Color(
          if (r > 255) 255 else r,
          if (g > 255) 255 else g,
          if (b > 255) 255 else b
        )
      case _                                                                =>
        maybeLower.orElse(maybeGrader).map(_._2).get
    }

  }

  /**
    * @param temperatures Known temperatures
    * @param colors       Color scale
    * @return A 360×180 image where each pixel shows the predicted temperature at its location
    */
  def visualize(
                 temperatures: Iterable[(Location, Double)],
                 colors: Iterable[(Double, Color)]
               ): Image = {

    val (w, h) = (360, 180)

    val pixels = for {
      lat <- (90 until -90 by -1).par
      lon <- (-180 until 180).par
    } yield {
      val color = interpolateColor(colors, predictTemperature(temperatures, Location(lat, lon)))

      Pixel(RGBColor(color.red, color.green, color.blue))
    }


    Image(w, h, pixels.toArray)
  }

  def greatCircleDistance(from: Location, to: Location): Double = {
    val fromRadians = from.lat.toRadians
    val toRadians = to.lat.toRadians

    acos(sin(fromRadians) * sin(toRadians) + cos(fromRadians) * cos(toRadians) * cos((to.lon - from.lon).toRadians))
  }

}

