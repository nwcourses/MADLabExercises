package com.example.basicmapapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf

class SetLocationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_location)

        // Find the UI elements.
        // Note that element IDs need to be unique across the
        // whole application, which is why we have used "etLat2"
        // and "etLon2" rather than "etLat" and "etLon"
        val btn2 = findViewById<Button>(R.id.btn2)
        val etLat2 = findViewById<EditText>(R.id.etLat2)
        val etLon2 = findViewById<EditText>(R.id.etLon2)

        // When the button is pressed...
        btn2.setOnClickListener {
            // Read the latitude and longitude from the edit texts
            val lat = etLat2.text.toString().toDouble()
            val lon = etLon2.text.toString().toDouble()

            // Create a return intent
            val intent = Intent()

            // Create a bundle to send the data back. Note that,
            // unlike in MapChooseActivity, this bundle contains
            // TWO entries, one for the latitude and one for the
            // longitude. Each is indexed with a unique index which
            // we will use in the main activity when reading them back.

            val bundle = bundleOf(
                "com.example.basicmapapp.lat" to lat,
                "com.example.basicmapapp.lon" to lon
            )
            intent.putExtras(bundle)
            setResult(RESULT_OK, intent)
            finish()
        }

    }

}