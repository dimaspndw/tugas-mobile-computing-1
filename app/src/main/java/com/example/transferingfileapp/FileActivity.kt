// FileActivity.kt
package com.example.transferingfileapp

import CardAdapter
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.transferingfileapp.requestAPI.API
import com.example.transferingfileapp.utils.DialogUtils

class FileActivity : ComponentActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CardAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.file_activity)

        recyclerView = findViewById(R.id.recyclerView)
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager

        getDataFromApi()
    }

    private fun getDataFromApi() {
        // get token disini
        val sharedPreferences = getSharedPreferences("TokenPreference", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", null)

        if (token != null) {
            // Panggil API dengan token yang diperoleh
            API(this).getData(token.toInt()) { data ->
                if (data != null) {
                    adapter = CardAdapter(data){ itemId ->
                        downloadFile(itemId)
                    }
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
        Log.d("code", id.toString())
    }
}
