package ch.hearc.starc

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.animation.DecelerateInterpolator
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.graphics.ColorUtils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class StarPopUp : AppCompatActivity() {

    private var popupTitle = ""
    private var popupText = ""
    private var popupButton = ""
    private var darkStatusBar = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(0, 0)
        setContentView(R.layout.star_popup)

        // Get the data
        val title : AppCompatTextView = findViewById(R.id.popup_window_title)
        val text : AppCompatTextView = findViewById(R.id.popup_window_text)
        val button : Button = findViewById(R.id.popup_window_button)
        val bg : ConstraintLayout = findViewById(R.id.popup_window_background)
        val cardv : CardView = findViewById(R.id.popup_window_view_with_border)
        val favbutton : Button = findViewById(R.id.fav_button)

        val bundle = intent.extras
        popupTitle = bundle?.getString("popuptitle", "Title") ?: ""
        popupText = bundle?.getString("popuptext", "Text") ?: ""
        popupButton = bundle?.getString("popupbtn", "Button") ?: ""
        darkStatusBar = bundle?.getBoolean("darkstatusbar", false) ?: false

        // Set the data
        title.text = popupTitle
        text.text = popupText
        button.text = popupButton
        favbutton.text = "F"

        // Fade animation for the background of Popup Window
        val alpha = 100 //between 0-255
        val alphaColor = ColorUtils.setAlphaComponent(Color.parseColor("#000000"), alpha)
        val colorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), Color.TRANSPARENT, alphaColor)
        colorAnimation.duration = 500 // milliseconds
        colorAnimation.addUpdateListener { animator ->
            bg.setBackgroundColor(animator.animatedValue as Int)
        }
        colorAnimation.start()

        // Fade animation for the Popup Window
        cardv.alpha = 0f
        cardv.animate().alpha(1f).setDuration(500).setInterpolator(
            DecelerateInterpolator()
        ).start()

        // Close the Popup Window when you press the button
        button.setOnClickListener {
            onBackPressed()
        }

        favbutton.setOnClickListener {
            addFav(popupTitle)
        }
    }

    override fun onBackPressed() {
        val bg : ConstraintLayout = findViewById(R.id.popup_window_background)
        val cardv : CardView = findViewById(R.id.popup_window_view_with_border)

        // Fade animation for the background of Popup Window when you press the back button
        val alpha = 100 // between 0-255
        val alphaColor = ColorUtils.setAlphaComponent(Color.parseColor("#000000"), alpha)
        val colorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), alphaColor, Color.TRANSPARENT)
        colorAnimation.duration = 500 // milliseconds
        colorAnimation.addUpdateListener { animator ->
            bg.setBackgroundColor(
                animator.animatedValue as Int
            )
        }

        // Fade animation for the Popup Window when you press the back button
        cardv.animate().alpha(0f).setDuration(500).setInterpolator(
            DecelerateInterpolator()
        ).start()

        // After animation finish, close the Activity
        colorAnimation.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                finish()
                overridePendingTransition(0, 0)
            }
        })
        colorAnimation.start()
    }

    private fun addFav(title : String){
        val sharedPreferences = getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE)
        val constellationFavorisGSON = sharedPreferences.getString("favoris", null)
        val itemType = object : TypeToken<ArrayList<String>>() {}.type
        if (constellationFavorisGSON != null) {
            val list: ArrayList<String> = Gson().fromJson(constellationFavorisGSON, itemType)
            list.add(title)
            val sharedPreferences = getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE)
            with(sharedPreferences.edit()){
                val constellationFavorisGSON = Gson().toJson(list)
                putString("favoris", constellationFavorisGSON)
                apply()
            }
        }else{
            val list: ArrayList<String> = arrayListOf()
            list.add(title)
            val sharedPreferences = getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE)
            with(sharedPreferences.edit()){
                val constellationFavorisGSON = Gson().toJson(list)
                putString("favoris", constellationFavorisGSON)
                apply()
            }
        }
    }
}