package ch.hearc.starc

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity(), SensorEventListener, NavigationView.OnNavigationItemSelectedListener, LocationListener, CustomCanvas.PopUpListener {
    // LocationManager
    private lateinit var locationManager: LocationManager
    private val locationPermissionCode = 2

    // Accelerometer and Magnetic field
    var mGravity: FloatArray = FloatArray(3)
    var mGeomagnetic: FloatArray = FloatArray(3)

    // Sensor and SensorManager
    private lateinit var sensorManager: SensorManager
    private lateinit var sensor : Sensor

    // Drawable menu
    lateinit var drawerLayout : DrawerLayout
    lateinit var navigationView : NavigationView
    lateinit var toolBar : Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<CustomCanvas>(R.id.custom_canvas).listener = this

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        getLocation()

        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.activity_main_nav)
        toolBar = findViewById(R.id.toolbar)

        setSupportActionBar(toolBar)
        navigationView.setNavigationItemSelectedListener(this)
		
        val toggle = ActionBarDrawerToggle(this,drawerLayout,toolBar,R.string.open,R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
    }

    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(
            this,
            sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
            SensorManager.SENSOR_DELAY_NORMAL
        )
        sensorManager.registerListener(
            this,
            sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
            SensorManager.SENSOR_DELAY_NORMAL
        )
        getLocation()
    }

    /**
     * When the sensors trigger
     */
    override fun onSensorChanged(event: SensorEvent?) {
        // Get the user azimuth to the north and give it to the AstronomicMath class
        if(event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            mGravity = event.values
        }

        if(event?.sensor?.type == Sensor.TYPE_MAGNETIC_FIELD) {
            mGeomagnetic = event.values
        }

        if(mGravity != null && mGeomagnetic != null) {
            val R = FloatArray(9)
            val I = FloatArray(9)

            if (SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic)) {
                // Orientation contains azimut, pitch and roll
                val orientation = FloatArray(3)
                SensorManager.getOrientation(R, orientation)

                // Set the north in the AstronomicMath class
                AstronomicMath.userAzimuth = (-orientation[0] * 360 / (2 * Math.PI)).toFloat()
            }
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        // Method needed to be implemented --> no need for us so empty func
    }

    override fun onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START)
        }else{
            super.onBackPressed()
        }

    }

    /**
     * Navigate between the activites with the menu
     */
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.giroscope_menu ->
                Toast.makeText(this, "Gyro", Toast.LENGTH_SHORT).show()
            R.id.favorite ->
                startActivity(Intent(this, FavorisActivity::class.java))
            R.id.about_menu ->
                Toast.makeText(this, "HE-Arc | 2021-2022 | Bruno C., Diogo L., Loïc F. et Valentino I.", Toast.LENGTH_SHORT).show()
        }

        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    /**
     * Get the locationManager
     */
    private fun getLocation() {
        this.locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), locationPermissionCode)
        }else{
            this.locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 5000f, this)
            this.locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 5000f, this);
        }
    }

    /**
     * When the location change set the text
     */
    override fun onLocationChanged(location: Location) {
        AstronomicMath.lat = location.latitude
        AstronomicMath.lge = location.longitude
        // Refresh all the stars
        findViewById<CustomCanvas>(R.id.custom_canvas).refresh()
    }

    /**
     * Result after the request permissions
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == locationPermissionCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
            }
            else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Open the pop-up activity
     */
    override fun OpenPopUp(title: String, text: String, btnText: String, bg: Boolean) {
        val intent = Intent(this, StarPopUp::class.java)
        intent.putExtra("popuptitle", title)
        intent.putExtra("popuptext", text)
        intent.putExtra("popupbtn", btnText)
        intent.putExtra("darkstatusbar", bg)
        startActivity(intent)
    }
}