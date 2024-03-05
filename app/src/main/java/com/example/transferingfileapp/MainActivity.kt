package com.example.transferingfileapp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.ComponentActivity
import com.example.transferingfileapp.handler.NetworkCheckHandler
import com.example.transferingfileapp.requestAPI.API

class MainActivity : ComponentActivity() {
    private lateinit var networkCheckHandler: NetworkCheckHandler
    private val api = API(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize and start network check
        networkCheckHandler = NetworkCheckHandler(this)
        networkCheckHandler.startChecking()

        val editTextPIN = findViewById<EditText>(R.id.editTextPIN)
        val btnSubmit = findViewById<Button>(R.id.btnSubmit)

        btnSubmit.setOnClickListener{
            val pin = editTextPIN.text.toString()
            api.checkUserAPI(pin)
        }
    }

    override fun onDestroy() {
        // Stop network check when the activity is destroyed
        networkCheckHandler.stopChecking()
        super.onDestroy()
    }
}