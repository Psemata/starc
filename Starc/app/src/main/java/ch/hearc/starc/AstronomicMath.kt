package ch.hearc.starc

import java.util.*
import kotlin.math.sin
import kotlin.math.asin
import kotlin.math.cos

/**
 * Static class used to compute astronomic mathematic
 */
class AstronomicMath {
    companion object {
        var lat = 0.0;
        var lge = 0.0;
        var userAzimuth = 0f;

        fun calculateJulianDayFromGregorian(year: Int, month: Int, day: Int, hour: Int) : Double {
            var yearj : Int = 0
            var monthj : Int = 0
            if(month <= 2){
                yearj = year-1
                monthj = month+12
            }
            val q : Double = day + (hour/24) - 0.5
            val s : Int = (yearj/100.0).toInt()
            val b : Int = 2 - s + (s/4.0).toInt()
            val jd : Double = (365.25 * (yearj + 4716)).toInt() + (30.6001 * (monthj + 1)).toInt() + q + b - 1524
            return jd
        }

        fun calculateLocalApparentSideralTimeFromJulianDay(jd: Double, lge: Double) : Double {
            val d : Double = jd - 2451545
            val gmst : Double = 18.697374558 + 24.06570982441908 * d
            val gmst24 : Double = (gmst/24.0 - (gmst/24.0).toInt()) * 24
            val alpha : Double = lge/15.0
            val omega : Double = 125.04 - 0.052954 * d
            val l : Double = 280.47 + 0.98565 * d
            val epsilon : Double = 23.4393 - 0.0000004 * d
            val psi = -0.000319 * sin(omega) - 0.000024 * sin(2 * l)
            val ee : Double = psi * cos(epsilon)
            val gast : Double = gmst24 + ee
            val last = gast + alpha
            return last
        }

        fun calculateHourlyAngleFromAscension(last: Double, rightAscent: Double) : Double {
            return 15 * last - rightAscent
        }

        fun calculateHorizontalCoordinateFromAscensionAndDeclension(hourlyAngle: Double, declension: Double, lat: Double) : Pair<Double, Double>{
            val sinh : Double = cos(lat) * cos(declension) * cos(hourlyAngle) + sin(lat) * sin(declension)
            val h : Double = asin(sinh)
            val a : Double = sin(lat) * cos(declension) * cos(hourlyAngle) - cos(lat) * sin(declension)
            val sinz : Double = (cos(declension) * sin(hourlyAngle))/cos(h)
            var z : Double = 0.0
            if (a >= 0){
                z = asin(sinz)
            }else{
                z = asin(sinz) + 180
            }
            val az : Double = z + 180
            return Pair(h, az)
        }

        fun calculateCartesianCoordinateFromHorizontalCoordinates(height : Double, azimuth : Double, azimuthObserver : Double, zoomFactor : Double) : Pair<Double, Double>{
            val x : Double = zoomFactor * (1 - (2*height)/Math.PI) * sin(azimuthObserver - azimuth)
            val y : Double = zoomFactor * (1 - (2*height)/Math.PI) * cos(azimuthObserver - azimuth)
            return Pair(x,y)
        }

        fun calculateWithStar(ra : Double, dec : Double, lat : Double, lge: Double) : Pair<Double, Double>{
            val cal : Calendar = Calendar.getInstance()
            val jd : Double = calculateJulianDayFromGregorian(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH)+1, cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.HOUR_OF_DAY))
            val last : Double = calculateLocalApparentSideralTimeFromJulianDay(jd, lge) //Longitude à avoir avec le GPS
            val hourlyAngle : Double = calculateHourlyAngleFromAscension(last, ra) // star.ra?.toDouble() ?: 0.0
            val horizontalCoordinates : Pair<Double, Double> = calculateHorizontalCoordinateFromAscensionAndDeclension(hourlyAngle, dec, lat) //Latitude a avoir avec le GPS // star.dec ?: 0.0
            val cartesianCoordinates : Pair<Double, Double> = calculateCartesianCoordinateFromHorizontalCoordinates(horizontalCoordinates.first, horizontalCoordinates.second, this.userAzimuth.toDouble(), 4000.0) //Azimuth de l'observer et le facteur de zoom à définir
            return cartesianCoordinates
        }
    }
}