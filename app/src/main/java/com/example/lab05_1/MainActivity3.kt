package com.example.lab05_1

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.*
import kotlinx.coroutines.NonCancellable.isCancelled

class MainActivity3 : AppCompatActivity() {
    private var secondsElapsed: Int = 0
    private lateinit var textSecondsElapsed: TextView
    private lateinit var sharedPref: SharedPreferences
    private val sharedPrefName = "SEC"
    private lateinit var job : Job

    private fun startCoroutine() {
        job = CoroutineScope(Dispatchers.Default).launch {
            Log.d("MainActivity", "coroutine launched")
            try {
                while (true) {
                    Log.d("MainActivity", "coroutine is working (${Thread.currentThread()}")
                    withContext(Dispatchers.Main) { // Передача значения в UI-поток
                        textSecondsElapsed.text = getString(R.string.sec_elapsed, secondsElapsed++)
                    }
                    delay(1000)
                }
            }
            catch (e: InterruptedException) {
                Log.d("MainActivity", "coroutine is interrupted (${Thread.currentThread()}")
            }
        }
    }

    private fun stopCoroutine() {
        job.cancel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val tvAppName = findViewById<TextView>(R.id.tvAppName)
        tvAppName.text = getString(R.string.app_name3)

        textSecondsElapsed = findViewById(R.id.textSecondsElapsed)
        sharedPref = getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE)
    }

    override fun onResume() {
        Log.d("MainActivity", "resumed")
        startCoroutine()
        secondsElapsed = sharedPref.getInt(sharedPrefName, 0)
        super.onResume()
    }

    override fun onPause() {
        Log.d("MainActivity", "paused")
        stopCoroutine()
        val editor = sharedPref.edit()
        editor.putInt(sharedPrefName, secondsElapsed)
        editor.apply()
        super.onPause()
    }

}