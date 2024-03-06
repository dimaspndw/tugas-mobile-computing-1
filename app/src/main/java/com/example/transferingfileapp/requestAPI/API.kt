package com.example.transferingfileapp.requestAPI

import com.example.transferingfileapp.MainActivity
import com.example.transferingfileapp.model.ResponseClass
import okhttp3.RequestBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.content.Intent
import android.content.Context
import android.util.Log
import com.example.transferingfileapp.HomeActivity
import com.example.transferingfileapp.utils.DialogUtils


import retrofit2.create

class API(private val context: Context) {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api-transfer-file.dimaspndw.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val apiService = retrofit.create(ApiService::class.java)

    fun checkUserAPI(pin: String) {
        val pinRequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), pin)

        val call = apiService.checkUser(
            pinRequestBody
        )

        call.enqueue(object : Callback<ResponseClass> {
            override fun onResponse(call: Call<ResponseClass>, response: Response<ResponseClass>) {
                if (response.isSuccessful) {
                    val yourResponse = response.body()
                    if (yourResponse?.message == "Success to login") {
                        val intent = Intent(context, HomeActivity::class.java)
                        // to save token or pin in local storage
                        val sharedPreferences = context.getSharedPreferences("TokenPreference", Context.MODE_PRIVATE)
                        val editor = sharedPreferences.edit()
                        editor.putString("token", pin)
                        editor.apply()
                        context.startActivity(intent)
                    } else {
                        DialogUtils.invalidPINDialog(context)
                    }
                } else {
                    DialogUtils.invalidPINDialog(context)
                }
            }

            override fun onFailure(call: Call<ResponseClass>, t: Throwable) {
                DialogUtils.invalidPINDialog(context)
            }
        })
    }
}

interface ApiService {
    @retrofit2.http.Multipart
    @retrofit2.http.POST("api/user/check")
    fun checkUser(
        @retrofit2.http.Part("pin") pin: RequestBody,
    ): Call<ResponseClass>
}