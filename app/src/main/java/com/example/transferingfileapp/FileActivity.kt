package com.example.transferingfileapp// MainActivity.kt
import CardAdapter
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.transferingfileapp.requestAPI.API

class FileActivity : ComponentActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CardAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.file_activity)

        recyclerView = findViewById(R.id.recyclerView)
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager

        // Call getData function to retrieve data from the API
        getDataFromApi()
    }

    private fun getDataFromApi() {
        val sharedPreferences = this.getSharedPreferences("TokenPreference", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", null)
        val intToken = token!!.toInt()
        // Call the getData function from your API class
        API(this).getData(intToken) { data ->
            // Update the adapter with the retrieved data
            if (data != null) {
                // Assuming data is a List<DataResponseClass>
                adapter = CardAdapter(data)
                recyclerView.adapter = adapter
            } else {
                // Handle the case when data retrieval fails
                // You may want to show an error message or take appropriate action
            }
        }
    }
}

