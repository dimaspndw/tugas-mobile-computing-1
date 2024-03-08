package com.example.transferingfileapp.model

data class DataResponseClass(
    val message: String = "",
    val data: List<DataItem> = emptyList()
)

data class DataItem(
    val id: Int,
    val from: String,
    val to: String,
    val name: String,
    val file: String,
    val created_at: String,
    val updated_at: String
)
