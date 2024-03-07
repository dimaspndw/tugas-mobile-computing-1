package com.example.transferingfileapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.database.getStringOrNull
import androidx.documentfile.provider.DocumentFile
import com.example.transferingfileapp.handler.NetworkCheckHandler
import com.example.transferingfileapp.requestAPI.API

class HomeActivity : ComponentActivity() {
    private lateinit var networkCheckHandler: NetworkCheckHandler
    private val api = API(this)
    private lateinit var selectedUri: Uri

    private val chooseFileLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            // Gunakan URI file yang dipilih
            val selectedFile = DocumentFile.fromSingleUri(this, uri)
            val fileName = queryFileName(uri)
            val textChooseFile = findViewById<TextView>(R.id.namaFileTerpilih)

            textChooseFile.text = "Nama File: $fileName"

            selectedUri = uri
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_activity)

        // Initialize and start network check
        networkCheckHandler = NetworkCheckHandler(this)
        networkCheckHandler.startChecking()

        val btnChooseFile = findViewById<Button>(R.id.btnChooseFile)
        val nameFile = findViewById<EditText>(R.id.editTextFileName)
        val btnSubmit = findViewById<Button>(R.id.btnSubmit)
        val btnYourFile = findViewById<Button>(R.id.yourFile)

        btnChooseFile.setOnClickListener{
            chooseFileLauncher.launch("*/*")
        }

        btnSubmit.setOnClickListener{
            val nameFile = nameFile.text.toString()
            api.postData(selectedUri, 123, 123, nameFile)
        }

//        btnYourFile.setOnClickListener {
//            val intent = Intent(this, FileActivity::class.java)
//            startActivity(intent)
//        }
    }

    private fun queryFileName(uri: android.net.Uri): String? {
        val cursor = contentResolver.query(uri, null, null, null, null)
        val fileName = cursor?.use {
            it.moveToFirst()
            it.getStringOrNull(it.getColumnIndex(OpenableColumns.DISPLAY_NAME))
        }
        cursor?.close()
        return fileName
    }

    override fun onDestroy() {
        // Stop network check when the activity is destroyed
        networkCheckHandler.stopChecking()
        super.onDestroy()
    }
}