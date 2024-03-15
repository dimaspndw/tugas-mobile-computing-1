package com.example.transferingfileapp

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
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
    // inisialisai variable yang diperlukan untuk HomeActivity
    private lateinit var networkCheckHandler: NetworkCheckHandler
    private val api = API(this)
    private lateinit var selectedUri: Uri


    // Dengan menggunakan GetContent maka akan membuka file picker default dari android
    // kemudian ketika user telah memilih file akan menghasilkan uri ke dalam file yang dipilih
    private val chooseFileLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            // Gunakan URI file yang dipilih
            val selectedFile = DocumentFile.fromSingleUri(this, uri)
            // akan mengambil filename
            val fileName = queryFileName(uri)
            // mengambil view dengan type text view dengan id nameFileTerpilih kemudian
            // dimasukan ke dalam variable textChooseFile
            val textChooseFile = findViewById<TextView>(R.id.namaFileTerpilih)

            // mengubah value textChooseFile menjadi value dari hasil queryFileName
            textChooseFile.text = "$fileName"

            // mengisi variable selectedUri yang telah diinisialisasi diawal dengan uri
            selectedUri = uri
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_activity)

        // Melakukan checking network
        networkCheckHandler = NetworkCheckHandler(this)
        networkCheckHandler.startChecking()

        // mengambil beberapa view yang dibutuhkan dan memasuka ke dalam variabel
        val btnChooseFile = findViewById<Button>(R.id.btnChooseFile)
        val nameFile = findViewById<TextView>(R.id.namaFileTerpilih)
        val btnSubmit = findViewById<Button>(R.id.btnSubmit)
        val btnYourFile = findViewById<Button>(R.id.yourFile)
        val textCode = findViewById<TextView>(R.id.codeShare)

        // saat button choose file di klik maka akan memanggil properto chooseFileLauncher kemudian
        // akan melakukan launch file picker default dari android
        btnChooseFile.setOnClickListener{
            chooseFileLauncher.launch("*/*")
        }

        // Mengambil token yang telah disimpan di dalam share preferences android dengan nama TokenPreference
        // share preferences adalah mekanisme penyimpanan kecil di android yang terdiri dari key value
        val sharedPreferences = getSharedPreferences("TokenPreference", Context.MODE_PRIVATE)
        // mengambil token yang telah disimpan di dalam share preferences
        val token = sharedPreferences.getString("token", null)

        // saat button submit diklik maka
        btnSubmit.setOnClickListener{
            // mengambil nama file dan textCode {text code adalah code pin yang akan dituju}
            val nameFile = nameFile.text.toString()
            val textCode = textCode.text.toString()
            // jika token tidak null maka
            if (token != null) {
                // melakukan post data dengan membawa parameter
                // {selectedUri, token kita, pin yang dituju, dan nama file}
                api.postData(selectedUri, token.toInt(), textCode.toInt(), nameFile)
            }
        }

        // saat button yout file diklik maka
        btnYourFile.setOnClickListener {
            // start intent ke dalam FileActivity
            val intent = Intent(this, FileActivity::class.java)
            startActivity(intent)
        }
    }

    // function ini digunakan untuk mengambil filename dari file yang telah dipilih
    // kemudian akan mengembalikan nama file yang telah dipilih
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