package ch.hearc.starc

import android.content.Context
import android.util.AttributeSet
import android.view.View

class Constellation (val con : String?, context: Context?, attr: AttributeSet?) : View(context, attr) {

    // Stars of the constellation
    var stars: MutableList<Star?>? = null

    // Creation of the constellation
    init {
        isClickable = true
        // The creation of the star list
        this.stars = arrayListOf<Star?>()
    }

    // Addition to the star list
    fun addToList(star : Star?) {
        stars?.add(star)
    }

    fun constellationRefresh() {
        // Refresh all the stars
        for(i in 0..stars!!.size - 1){
            this.stars?.get(0)?.carthesianCoordinates()
        }
    }
}