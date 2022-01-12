package com.example.lab05_1

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future

class MyApp : Application() {
    val executorService : ExecutorService = Executors.newSingleThreadExecutor()
}

class MainActivity2 : AppCompatActivity() {
    private var secondsElapsed: Int = 0
    private lateinit var textSecondsElapsed: TextView
    private lateinit var sharedPref: SharedPreferences
    private val sharedPrefName = "SEC"
    private lateinit var executor: ExecutorService
    private lateinit var task: Future<*>

    private fun startExecution() {
        task = executor.submit {
            Log.d("MainActivity", "execution launched")
            try {
                while (!executor.isShutdown) {
                    Log.d("MainActivity", "execution is working (${Thread.currentThread()})")
                    textSecondsElapsed.post {
                        textSecondsElapsed.text = getString(R.string.sec_elapsed, secondsElapsed++)
                    }
                    Thread.sleep(1000)
                }
            }
            catch (e: InterruptedException) {
                Log.d("MainActivity", "execution is interrupted (${Thread.currentThread()})")
            }
        }
    }

    private fun stopExecution() {
        task.cancel(true)
        Log.d("MainActivity", "execution stopped")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val tvAppName = findViewById<TextView>(R.id.tvAppName)
        tvAppName.text = getString(R.string.app_name2)

        textSecondsElapsed = findViewById(R.id.textSecondsElapsed)
        sharedPref = getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE)

        executor = (application as MyApp).executorService
    }

    override fun onResume() {
        Log.d("MainActivity", "resumed")
        secondsElapsed = sharedPref.getInt(sharedPrefName, 0)
        startExecution()
        super.onResume()
    }

    override fun onPause() {
        Log.d("MainActivity", "paused")
        stopExecution()
        val editor = sharedPref.edit()
        editor.putInt(sharedPrefName, secondsElapsed)
        editor.apply()
        super.onPause()
    }

}