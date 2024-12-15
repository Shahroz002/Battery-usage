package com.example.batteryusage

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
//
//        // Load the fragment
//        if (savedInstanceState == null) {
//            supportFragmentManager.beginTransaction()
//                .replace(R.id.fragment_container, BatteryUsageFragment())
//                .commit()
//        }
//    }

override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    // Load the fragment
    if (savedInstanceState == null) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, BatteryUsageFragment())
            .commit()
    }
}
}
