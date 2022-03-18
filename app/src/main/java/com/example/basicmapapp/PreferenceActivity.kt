package com.example.basicmapapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceFragmentCompat

class PreferenceActivity : AppCompatActivity() {

    override fun onCreate (savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportFragmentManager.beginTransaction().replace(android.R.id.content, MyPreferenceFragment()).commit()
    }
}

class MyPreferenceFragment: PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.preferences)
    }
}