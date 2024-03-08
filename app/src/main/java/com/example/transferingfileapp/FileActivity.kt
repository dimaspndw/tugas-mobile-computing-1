// FileActivity.kt
package com.example.transferingfileapp

import CardAdapter
import android.content.Context
import android.os.Bundle
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
        API(this).getData(123) { data ->
            if (data != null) {
                adapter = CardAdapter(data)
                recyclerView.adapter = adapter
            } else {
                // Handle the case when data retrieval fails
                DialogUtils.invalidPINDialog(this)
            }
        }
    }


}
