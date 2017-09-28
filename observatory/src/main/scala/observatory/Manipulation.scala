package observatory

import observatory.Visualization._

/**
  * 4th milestone: value-added information
  */
object Manipulation {

  /**
    * @param temperatures Known temperatures
    * @return A function that, given a latitude in [-89, 90] and a longitude in [-180, 179],
    *         returns the predicted temperature at this location
    */
  def makeGrid(
                temperatures: Iterable[(Location, Double)]
              ): (Int, Int) => Double = {
    (latitude, longitude) => {
      require(latitude >= -89)
      require(latitude <= 90)
      require(longitude >= -180)
      require(longitude <= 179)

      predictTemperature(temperatures, Location(latitude, longitude))
    }
  }

  /**
    * @param temperaturess Sequence of known temperatures over the years (each element of the collection
    *                      is a collection of pairs of location and temperature)
    * @return A function that, given a latitude and a longitude, returns the average temperature at this location
    */
  def average(
               temperaturess: Iterable[Iterable[(Location, Double)]]
             ): (Int, Int) => Double = {
    (latitude, longitude) => temperaturess.map(makeGrid(_)(latitude, longitude)).sum / temperaturess.size
  }

  /**
    * @param temperatures Known temperatures
    * @param normals      A grid containing the “normal” temperatures
    * @return A grid containing the deviations compared to the normal temperatures
    */
  def deviation(
                 temperatures: Iterable[(Location, Double)],
                 normals: (Int, Int) => Double
               ): (Int, Int) => Double = {
    (latitude, longitude) => makeGrid(temperatures)(latitude, longitude) - normals(latitude, longitude)
  }


}

