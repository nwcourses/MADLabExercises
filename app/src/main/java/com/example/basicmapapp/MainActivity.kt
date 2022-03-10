package com.example.basicmapapp

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.ItemizedIconOverlay
import org.osmdroid.views.overlay.OverlayItem

class MainActivity : AppCompatActivity(), LocationListener {

    // Make the map a "lateinit" variable.
    //
    // A "lateinit" is a variable that will never be null, however we
    // cannot initialise it immediately, when the object is first created.
    //
    // As the map can only be accessed when the XML layout has been loaded (i.e. in
    // onCreate), we can only actually assign it to the map object in onCreate().
    //
    // Also, we make "map1" an attribute so that it can be accessed from all the class's methods.
    lateinit var map1: MapView

    // Make the LocationManager an attribute so that it can be accessed from multiple methods
    var lMgr: LocationManager? = null

    // Launchers for MapChooseActivity and SetLocationActivity

    val mapChooseLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        it.data?.apply {
            val opentopo = this.getBooleanExtra("com.example.basicmapapp.opentopomap", false)
            map1.tileProvider.tileSource = if (opentopo) TileSourceFactory.OpenTopo else TileSourceFactory.MAPNIK
        }
    }
    val setLocationLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        it.data?.apply {
            val lat = this.getDoubleExtra("com.example.basicmapapp.lat", 51.05)
            val lon = this.getDoubleExtra("com.example.basicmapapp.lon", -0.72)
            map1.controller.setCenter(GeoPoint(lat, lon))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // This line sets the user agent, a requirement to download OSM maps
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));

        setContentView(R.layout.activity_main)
        map1 = findViewById<MapView>(R.id.map1)
        map1.controller.setZoom(16.0)
        map1.controller.setCenter(GeoPoint(51.05, -0.72))

        val btn1 = findViewById<Button>(R.id.btn1)
        val etLat = findViewById<EditText>(R.id.etLat)
        val etLon = findViewById<EditText>(R.id.etLon)
        btn1.setOnClickListener {
            // Remove GPS updates so that they don't interfere with setting the location
            lMgr?.removeUpdates(this)
            val lat = etLat.text.toString().toDouble()
            val lon = etLon.text.toString().toDouble()
            map1.controller.setCenter(GeoPoint(lat, lon))
        }

        // try to start GPS
        tryToStartGPS()
    }

    override fun onCreateOptionsMenu(menu: Menu) : Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    // Handle menu choices
    override fun onOptionsItemSelected(menuItem: MenuItem) : Boolean {
        var result = true
        when(menuItem.itemId) {
            R.id.menuItemSetMapStyle -> {
                // Launch the map style choose activity using the launcher
                val intent = Intent(this, MapChooseActivity::class.java)
                mapChooseLauncher.launch(intent)
            }
            R.id.menuItemSetLocation -> {
                // For the set location menu option, remove GPS updates so that they don't
                // interfere with setting the location
                lMgr?.removeUpdates(this)

                // Launch the set location activity using the launcher
                val intent = Intent(this, SetLocationActivity::class.java)
                setLocationLauncher.launch(intent)
            }
            else -> {
                result = false
            }
        }
        return result
    }

    // Try to start GPS, and request permission if not already granted.
    fun tryToStartGPS() {
        // Has the GPS permission been granted yet?
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // If so, start the GPS listener
            lMgr = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            lMgr?.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, this)

        } else {
            // If not, request GPS permission from the user
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                0
            )
        }
    }

    // Runs as soon as the user has either granted or denied permission.
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // Check the request code matches the code used when requesting the permission
        when (requestCode) {
            0 -> {
                // If the user did grant the permission, call tryToStartGPS() again
                // When tryYoStartGPS() runs a second time, the permission will be granted,
                // so the GPS listening will start.
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    tryToStartGPS()
                }
            }
        }
    }

    override fun onLocationChanged(loc: Location) {
        map1.controller.setCenter(GeoPoint(loc.latitude, loc.longitude))
    }

    override fun onProviderDisabled(provider: String) {
        Toast.makeText (this, "Provider disabled", Toast.LENGTH_LONG).show()
    }

    override fun onProviderEnabled(provider: String) {
        Toast.makeText (this, "Provider enabled", Toast.LENGTH_LONG).show()
    }

    // Deprecated at API level 29, but must still be included, otherwise your
    // app will crash on lower-API devices as their API will try and call it
    override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {

    }

}
