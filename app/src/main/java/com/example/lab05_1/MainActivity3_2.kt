package com.example.lab05_1

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.*

// Улучшена точность подсчета времени за счет использования системного таймера (System.currentTimeMillis())
class MainActivity3_2 : AppCompatActivity() {
    private var secondsElapsedBeforePause: Int = 0
    private var secondsElapsed: Int = 0
    private var startTimeMillis: Long = 0
    private lateinit var textSecondsElapsed: TextView
    private lateinit var sharedPref: SharedPreferences
    private val sharedPrefName = "SEC"
    private lateinit var job : Job

    private fun startCoroutine() {
        job = CoroutineScope(Dispatchers.Default).launch {
            Log.d("MainActivity", "coroutine launched")
            try {
                while (true) {
                    Log.d("MainActivity", "coroutine is working (${Thread.currentThread()})")
                    if (secondsElapsed != getSecondsElapsed()) {
                        secondsElapsed = getSecondsElapsed()
                        withContext(Dispatchers.Main) { // Передача значения в UI-поток
                            textSecondsElapsed.text = getString(R.string.sec_elapsed, secondsElapsed)
                        }
                    }
                    delay(50)
                }
            }
            catch (e: InterruptedException) {
                Log.d("MainActivity", "coroutine is interrupted (${Thread.currentThread()}")
            }
        }
    }

    private fun stopCoroutine() {
        job.cancel()
        Log.d("MainActivity", "coroutine stopped")
    }

    private fun getSecondsElapsed(): Int {
        return ((System.currentTimeMillis() - startTimeMillis)/1000 + secondsElapsedBeforePause).toInt()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val tvAppName = findViewById<TextView>(R.id.tvAppName)
        tvAppName.text = getString(R.string.app_name3)

        textSecondsElapsed = findViewById(R.id.textSecondsElapsed)
        sharedPref = getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE)
    }

    override fun onStart() {
        startTimeMillis = System.currentTimeMillis()
        super.onStart()
    }

    override fun onResume() {
        Log.d("MainActivity", "resumed")
        secondsElapsedBeforePause = sharedPref.getInt(sharedPrefName, 0)
        startCoroutine()
        super.onResume()
    }

    override fun onPause() {
        Log.d("MainActivity", "paused")
        stopCoroutine()
        val editor = sharedPref.edit()
        editor.putInt(sharedPrefName, getSecondsElapsed())
        editor.apply()
        super.onPause()
    }

}