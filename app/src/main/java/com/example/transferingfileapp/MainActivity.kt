package com.example.transferingfileapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import com.example.transferingfileapp.handler.NetworkCheckHandler

class MainActivity : ComponentActivity() {
    private lateinit var networkCheckHandler: NetworkCheckHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize and start network check
        networkCheckHandler = NetworkCheckHandler(this)
        networkCheckHandler.startChecking()
    }

    override fun onDestroy() {
        // Stop network check when the activity is destroyed
        networkCheckHandler.stopChecking()
        super.onDestroy()
    }
}