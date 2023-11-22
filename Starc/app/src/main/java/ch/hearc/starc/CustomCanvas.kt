package ch.hearc.starc

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.nio.charset.Charset


/**
 * Custom view used as a canvas to draw the space and the stars
 */
class CustomCanvas (context: Context, attr: AttributeSet) : View(context, attr) {

    interface PopUpListener {
        fun OpenPopUp(title: String, text: String, btnText: String, bg: Boolean)
    }
    var listener: PopUpListener? = null

    // Star list
    var stars : MutableList<Star?>
    // Const list
    var constellations : MutableMap<String, Constellation>
    // Value used to draw data
    private var x1 : Float = 0f
    private var y1 : Float = 0f
    private var x2: Float = 0f
    private var y2 : Float = 0f
    private var offsetX : Float = 0f
    private var checkOffsetX : Float = 0f
    private var offsetY : Float = 0f
    private var checkOffsetY : Float = 0f
    val MIN_DISTANCE = 150

    // Delta used to correct click on stars
    val DELTA_CLICK = 30

    /**
     * Initialisation of all the stars in stars.json
     */
    init {
        isClickable = true
        // The creation of the star list
        this.stars = arrayListOf<Star?>()
        // The creation of the constellation map
        this.constellations = mutableMapOf<String, Constellation>()

        // Parse the json file
        try {
            // As we have JSON object, so we are getting the object
            // Here we are calling a Method which is returning the JSON object
            val obj = JSONObject(getJSONFromAssets()!!)
            // fetch JSONArray named stars by using getJSONArray
            val starsArray = obj.getJSONArray("stars")

            // Get the stars data
            for (i in 0 until starsArray.length()) {
                // Create a JSONObject for fetching single User's Data
                val star = starsArray.getJSONObject(i)

                // Get all the variables and stock them
                val proper = star.optString("proper")
                val ra = star.optDouble("ra")
                val dec = star.optDouble("dec")
                val dist = star.optDouble("dist")
                val mag = star.optDouble("mag")
                val ci = star.optDouble("ci")
                val x = star.optDouble("x")
                val y = star.optDouble("y")
                val z = star.optDouble("z")
                val bayer = star.optString("bayer")
                val flam = star.optDouble("flam")
                val con = star.optString("con")

                // Now we create the star object to be added to the list
                val starObject = Star(proper, ra, dec, dist, mag, ci, x, y, z, bayer, flam, con, context, attr)
                // Set the cartesian coordinates of the star
                starObject.carthesianCoordinates()

                // Constellations and star management, add the stars to Constellation objects
                if (constellations.containsKey(con)) {
                    constellations[con]?.addToList(starObject)
                } else {
                    constellations.put(con, Constellation(con, context, attr))
                    constellations[con]?.addToList(starObject)
                }
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    /**
     * Draw the canvas and its content
     */
    override fun onDraw(canvas : Canvas?) {
        super.onDraw(canvas)
        // Draw the black space surrounding and behind the stars
        canvas?.drawRect(this.x, this.y, this.x + this.width, this.y + this.height, Paint().apply { color = Color.argb(255,45, 52, 61 )})

        // Draw all the stars
        for ((key, value) in constellations.entries) {
            //constellations[key]!!.draw(canvas)
            for (star in constellations[key]!!.stars!!) {
                //Change position of star to correspond to offset
                star?.setOffset(offsetX, offsetY)
                //If the star is in the screen
                if (star?.drawX!! > this.x
                    && star?.drawY!! > this.y
                    && star?.drawX!! < this.x + this.width
                    && star?.drawY!! < this.y + this.height){
                        star?.draw(canvas)
                }
            }
        }
    }

    /**
     * Function used to refresh all the stars
     */
    fun refresh() {
        // Refresh all the stars in the constellation
        for ((key, value) in constellations.entries) {
            constellations[key]!!.constellationRefresh()
        }
        invalidate()
    }

    /**
     * Function used to get the json asset
     */
    private fun getJSONFromAssets(): String? {
        var json: String? = null
        val charset: Charset = Charsets.UTF_8
        try {
            val myUsersJSONFile = getContext().assets.open("stars.json")
            val size = myUsersJSONFile.available()
            val buffer = ByteArray(size)
            myUsersJSONFile.read(buffer)
            myUsersJSONFile.close()
            json = String(buffer, charset)
        } catch (ex: IOException) {
            ex.printStackTrace()
            return null
        }
        return json
    }

    /**
     * Function to click on the canvas
     */
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            //When the user clicks on the screen
            MotionEvent.ACTION_DOWN -> {
                //We save the initial x and y position
                x1 = event.x
                y1 = event.y
            }
            //When the user moves on the screen
            MotionEvent.ACTION_MOVE -> {
                //We save where the user is moving
                x2 = event.x
                y2 = event.y
                //We calculate a delta
                val deltaX: Float = x2 - x1
                val deltaY: Float = y2 - y1
                //Set the temporary offset
                offsetX = checkOffsetX + deltaX
                offsetY = checkOffsetY + deltaY
                invalidate()
            }
            //If we stop clicking
            MotionEvent.ACTION_UP -> {
                //We validate the offset
                checkOffsetX = offsetX
                checkOffsetY = offsetY
                //Recalculate the delta
                val deltaX: Float = x2 - x1
                val deltaY: Float = y2 - y1
                if (Math.abs(deltaX) > MIN_DISTANCE) {
                } else if(Math.abs(deltaY) > MIN_DISTANCE){

                }
                //If we did not scroll enough it is considered a click
                else {
                    for ((key, value) in constellations.entries) {
                        for (star in constellations[key]!!.stars!!) {
                            if (star?.drawX!! > event.x - DELTA_CLICK
                                && star?.drawY!! > event.y - DELTA_CLICK
                                && star?.drawX!! < event.x + DELTA_CLICK
                                && star?.drawY!! < event.y + DELTA_CLICK){
                                // Clique sur une étoile
                                var title : String = ""
                                var text : String = ""

                                if(star?.proper!! != "") {
                                    title = star?.proper!!
                                } else {
                                    if(star?.bayer != "") {
                                        title = star?.bayer!! + " "
                                    }
                                    if(star?.flam != Double.NaN) {
                                        title +=  star?.flam!!
                                    }
                                }
                                if(star?.con != null) {
                                    text = "L'étoile " + title + " est dans la constellation " + star?.con!!
                                } else {
                                    text = "L'étoile " + title + " n'est dans aucune constellation"
                                }

                                // Open the pop-up view of the star
                                listener?.OpenPopUp(title, text, "Ok", false)

                                break
                            }
                        }
                    }
                }
            }
        }
        return super.onTouchEvent(event)
    }
}