package ch.hearc.starc

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.*


class Star (val proper : String?, val ra : Double?, val dec : Double?,
            val dist : Double?, val mag : Double?, val ci : Double?,
            val x : Double?, val y : Double?, val z : Double?, val bayer : String?,
            val flam : Double?, val con : String?, context: Context?, attr: AttributeSet?) : View(context, attr) {
    //X and Y position of the star
    var canvasX: Double = 0.0
    var canvasY: Double = 0.0
    //X and Y position that will be drawn and change
    var drawX: Double = 0.0
    var drawY: Double = 0.0

    /**
     * Call to get the carthersian coordinates of the star
     */
    fun carthesianCoordinates() {
        val coordinates = AstronomicMath.calculateWithStar(this.ra!!, this.dec!!, AstronomicMath.lat, AstronomicMath.lge)
        canvasX = coordinates.first
        canvasY = coordinates.second
        drawX = coordinates.first
        drawY = coordinates.second
    }

    /**
     * Override of the onDraw function to draw the star
     */
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.drawText(this.proper!!, this.drawX?.toFloat()!!+10, this.drawY.toFloat()!!+10, Paint().apply { color = Color.WHITE; textSize = 30f });
        canvas?.drawCircle(this.drawX?.toFloat()!!, this.drawY.toFloat()!!, -this.mag?.toFloat()!!+7, Paint().apply { color = Color.WHITE })
    }

    /**
     * Set the offset from original position
     */
    fun setOffset(offsetX : Float, offsetY : Float) {
        this.drawX = this.canvasX + offsetX
        this.drawY = this.canvasY + offsetY
    }

    /**
     * The toString function used to show the data of the star
     */
    override fun toString(): String {
        return "$proper $con"
    }
}