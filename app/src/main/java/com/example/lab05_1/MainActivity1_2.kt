package com.example.lab05_1

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

// Улучшена точность подсчета времени за счет использования системного таймера (System.currentTimeMillis())
class MainActivity1_2 : AppCompatActivity() {
    private var secondsElapsedBeforePause: Int = 0
    private var secondsElapsed: Int = 0
    private var startTimeMillis: Long = 0
    lateinit var textSecondsElapsed: TextView
    private lateinit var sharedPref: SharedPreferences
    private val sharedPrefName = "SEC"
    private lateinit var backgroundThread : Thread

    private fun startThread() {
       backgroundThread = Thread {
            try {
                while (!Thread.interrupted()) {
                    Log.d("MainActivity", "background thread is working (${Thread.currentThread()})")
                    if (secondsElapsed != getSecondsElapsed()) {
                        secondsElapsed = getSecondsElapsed()
                        textSecondsElapsed.post {
                            textSecondsElapsed.text =
                                getString(R.string.sec_elapsed, secondsElapsed)
                        }
                    }
                    Thread.sleep(50)
                }
            } catch (e: InterruptedException) {
                Log.d("MainActivity", "background thread is interrupted ${Thread.currentThread()}")
            }
        }
        backgroundThread.start()
    }

    private fun stopThread() {
        backgroundThread.interrupt()
    }

    private fun getSecondsElapsed(): Int {
        return ((System.currentTimeMillis() - startTimeMillis)/1000 + secondsElapsedBeforePause).toInt()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val tvAppName = findViewById<TextView>(R.id.tvAppName)
        tvAppName.text = getString(R.string.app_name1)

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
        startThread()
        super.onResume()
    }

    override fun onPause() {
        Log.d("MainActivity", "paused")
        stopThread()
        val editor = sharedPref.edit()
        editor.putInt(sharedPrefName, getSecondsElapsed())
        editor.apply()
        super.onPause()
    }

}