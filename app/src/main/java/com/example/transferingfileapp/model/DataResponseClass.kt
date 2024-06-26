package com.example.transferingfileapp.model

data class DataResponseClass(
    val message: String = "",
    val data: List<DataItem> = emptyList(),
)

data class DataUserResponseClass(
    val message: String = "",
    val dataUser: DataUser
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

data class DataUser(
    val id: Int,
    val pin: String,
    val code: String,
    val email: String,
    val uid: String,
    val created_at: String,
    val updated_at: String
)
