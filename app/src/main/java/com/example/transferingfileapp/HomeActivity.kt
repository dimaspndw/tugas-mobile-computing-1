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
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.database.getStringOrNull
import androidx.documentfile.provider.DocumentFile
import com.example.transferingfileapp.handler.NetworkCheckHandler
import com.example.transferingfileapp.requestAPI.API
import com.google.firebase.auth.FirebaseAuth

class HomeActivity : ComponentActivity() {
    // inisialisai variable yang diperlukan untuk HomeActivity
    private lateinit var networkCheckHandler: NetworkCheckHandler
    private val api = API(this)
    private lateinit var selectedUri: Uri
    lateinit var auth: FirebaseAuth


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
        auth = FirebaseAuth.getInstance()

        // Melakukan checking network
        networkCheckHandler = NetworkCheckHandler(this)
        networkCheckHandler.startChecking()

        // mengambil beberapa view yang dibutuhkan dan memasuka ke dalam variabel
        val btnChooseFile = findViewById<Button>(R.id.btnChooseFile)
        val nameFile = findViewById<TextView>(R.id.namaFileTerpilih)
        val btnSubmit = findViewById<Button>(R.id.btnSubmit)
        val btnYourFile = findViewById<Button>(R.id.yourFile)
        val textCode = findViewById<TextView>(R.id.codeShare)
        val btnUpdateAccount = findViewById<Button>(R.id.btnToUpdateAccount)
        val btnLogout = findViewById<Button>(R.id.btnLogout)
        var code = ""

        val user = auth.currentUser
        val uid = user?.uid
        // panggil api
        if (uid != null) {
            // Panggil API untuk mendapatkan data user
            API(this).getDataUser(uid) { dataUser ->
                dataUser?.let {
                    Log.d("HomeActivity", "Data user: ${it.email}")
                    // Lakukan sesuatu dengan data user, misalnya menampilkan email di TextView
                    val tvUserEmail: TextView = findViewById(R.id.tvUserEmail)
                    val tvUserPin: TextView = findViewById(R.id.tvUserPin)
                    tvUserEmail.text = it.email
                    tvUserPin.text = "Pin anda : " + it.code
                    val editor = getSharedPreferences("TokenPreference", Context.MODE_PRIVATE).edit()
                    editor.putString("token", it.code)
                    editor.apply()
                    val sharedPreferences = getSharedPreferences("TokenPreference", Context.MODE_PRIVATE)
                    // mengambil token yang telah disimpan di dalam share preferences
                    val token = sharedPreferences.getString("token", null)
                    Log.d("Token dari storage API", token.toString())
                } ?: run {
                    Log.w("HomeActivity", "Data user null ${uid}")
                    // Tangani kasus ketika data user null
                    Toast.makeText(this, "Data user tidak ditemukan", Toast.LENGTH_SHORT).show()
                }
            }
        }

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
        Log.d("tokn", token.toString())
        // saat button submit diklik maka
        btnSubmit.setOnClickListener{
            val sharedPreferences = getSharedPreferences("TokenPreference", Context.MODE_PRIVATE)
            // mengambil token yang telah disimpan di dalam share preferences
            val token = sharedPreferences.getString("token", null)
            // mengambil nama file dan textCode {text code adalah code pin yang akan dituju}
            val nameFile = nameFile.text.toString()
            val textCode = textCode.text.toString()
            Log.d("tokn click", token.toString())
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

        btnUpdateAccount.setOnClickListener {
            val intent = Intent(this, UpdateAccountActivity::class.java)
            startActivity(intent)
        }

        btnLogout.setOnClickListener {
            auth.signOut()

            val sharedPreferences = getSharedPreferences("TokenPreference", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.remove("token")
            editor.apply()

            val intent = Intent(this, LoginActivity::class.java)
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