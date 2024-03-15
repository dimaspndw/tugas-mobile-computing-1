// FileActivity.kt
package com.example.transferingfileapp

import CardAdapter
import android.app.Dialog
import android.content.ContentValues
import android.content.Context
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.transferingfileapp.requestAPI.API
import com.example.transferingfileapp.utils.DialogUtils
import okhttp3.MediaType
import okhttp3.ResponseBody
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

class FileActivity : ComponentActivity() {
    // inisialisasi variable yang dibutuhkan
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CardAdapter
    private var progressDialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.file_activity)

        // RecyclerView merupakan bagian dari AndroidJetpack yang digunakan untuk menampilkan
        // daftar data dengan scrolling yang lebih efisien
        recyclerView = findViewById(R.id.recyclerView)
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager

        // memanggil data dari API
        getDataFromApi()
    }

    private fun getDataFromApi() {
        // melakukan get token sama seperti penjelasan sebelumnya
        val sharedPreferences = getSharedPreferences("TokenPreference", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", null)

        if (token != null) {
            // Panggil API dengan token yang diperoleh
            API(this).getData(token.toInt()) { data ->
                if (data != null) {
                    // memanggil cart adapter dengan membawa itemId untuk keperluan downloadFile
                    adapter = CardAdapter(data){ itemId ->
                        downloadFile(itemId)
                    }
                    // memasukan ke dalam recyclerView
                    recyclerView.adapter = adapter
                } else {
                    DialogUtils.invalidPINDialog(this)
                }
            }
        } else {
            DialogUtils.invalidPINDialog(this)
        }
    }

    private fun downloadFile(id: Int) {
        showLoading()
        API(this).downloadFile(id) { responseBody, mediaType, fileName ->
            Log.d("id", id.toString())
            hideLoading()
            if (responseBody != null && fileName != null) {
                if (mediaType != null) {
                    val fileExtension = getExtensionFromMediaType(mediaType)
                    val finalFileName = "$fileName.${fileExtension}"
                    saveFile(responseBody, finalFileName)
                }
            }
        }
    }

    fun saveFile(responseBody: ResponseBody?, fileName: String) {
        try {
            if (responseBody == null) {
                return
            }

            val inputStream: InputStream = responseBody.byteStream()

            val fileExtension = MimeTypeMap.getFileExtensionFromUrl(fileName)
            val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension.toLowerCase()) ?: "application/octet-stream"
            val resolver = contentResolver
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
            }

            val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)

            // Buka OutputStream dari URI MediaStore
            val outputStream = uri?.let { resolver.openOutputStream(it) }

            // Salin data dari InputStream ke OutputStream
            val buffer = ByteArray(1024)
            var bytesRead: Int

            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                outputStream?.write(buffer, 0, bytesRead)
            }

            // Tutup OutputStream dan InputStream
            outputStream?.close()
            inputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun getExtensionFromMediaType(mediaType: MediaType): String {
        // Mendapatkan subtype dari media type
        val subtype = mediaType.subtype

        // Tipe MIME dan ekstensi file yang sesuai
        val mimeToExtensionMap = mapOf(
            "pdf" to "pdf",
            "jpeg" to "jpg",
            "png" to "png",
        )

        // Mendapatkan ekstensi file dari tipe MIME
        return mimeToExtensionMap[subtype.toLowerCase()] ?: "unknown"
    }

    private fun showLoading() {
        progressDialog = Dialog(this)
        progressDialog?.setContentView(R.layout.layout_dialog)
        progressDialog?.setCancelable(false)
        progressDialog?.show()
    }

    private fun hideLoading() {
        progressDialog?.dismiss()
    }
}

