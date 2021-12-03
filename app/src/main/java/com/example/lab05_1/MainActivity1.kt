package com.example.lab05_1

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity1 : AppCompatActivity() {
    private var secondsElapsed: Int = 0
    private lateinit var textSecondsElapsed: TextView
    private lateinit var sharedPref: SharedPreferences
    private val sharedPrefName = "SEC"
    private lateinit var backgroundThread : Thread

    private fun startThread() {
       backgroundThread = Thread {
            try {
                while (true) {
                    Log.d("MainActivity", "background thread is working (${Thread.currentThread()})")
                    textSecondsElapsed.post {
                        textSecondsElapsed.text = getString(R.string.sec_elapsed, secondsElapsed++)
                    }
                    Thread.sleep(1000)
                }
            } catch (e: InterruptedException) {
                Log.d("MainActivity", "background thread is interrupted ${Thread.currentThread()}")
            }
        }
        backgroundThread.start()
    }

    private fun stopThread() {
        backgroundThread.interrupt() // Доказать поток через //
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val tvAppName = findViewById<TextView>(R.id.tvAppName)
        tvAppName.text = getString(R.string.app_name1)

        textSecondsElapsed = findViewById(R.id.textSecondsElapsed)
        sharedPref = getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE)
    }

    override fun onResume() {
        Log.d("MainActivity", "resumed")
        secondsElapsed = sharedPref.getInt(sharedPrefName, 0)
        startThread()
        super.onResume()
    }

    override fun onPause() {
        Log.d("MainActivity", "paused")
        stopThread()
        val editor = sharedPref.edit()
        editor.putInt(sharedPrefName, secondsElapsed)
        editor.apply()
        super.onPause()
    }

}