package com.example.transferingfileapp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.ComponentActivity
import com.example.transferingfileapp.handler.NetworkCheckHandler
import com.example.transferingfileapp.requestAPI.API
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    // inisialisasi variabel yang akan digunakan
    private lateinit var networkCheckHandler: NetworkCheckHandler
    private val api = API(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // mengambil content dari layout activity main
        setContentView(R.layout.activity_main)

        // Melakukan pengecekan apakah jaringan tersedia
        networkCheckHandler = NetworkCheckHandler(this)
        networkCheckHandler.startChecking()

        // mengambil view berdasarkan ID kemudian dimasukan ke dalam variable
        val editTextPIN = findViewById<EditText>(R.id.editTextPIN)
        val btnSubmit = findViewById<Button>(R.id.btnSubmit)

        // kondisi saat button submit di klik maka akan melakukan
        // inisialisasi variable pin yang diperoleh dari value editTextPin
        // kemudian pin akan dibawa ketika melakukan request ke API
        btnSubmit.setOnClickListener{
            // convert ke dalam string
            val pin = editTextPIN.text.toString()
            // melakukan request ke API
            api.checkUserAPI(pin)
        }
    }

    override fun onDestroy() {
        // Saat Main Activity tidak diakses lagi maka akan network checking akan di stop
        networkCheckHandler.stopChecking()
        super.onDestroy()
    }
}