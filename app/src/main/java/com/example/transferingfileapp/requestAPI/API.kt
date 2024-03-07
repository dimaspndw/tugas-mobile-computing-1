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
import androidx.documentfile.provider.DocumentFile
import com.example.transferingfileapp.HomeActivity
import com.example.transferingfileapp.model.DataResponseClass
import com.example.transferingfileapp.utils.DialogUtils
import okhttp3.MediaType
import okhttp3.MultipartBody


import retrofit2.create
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import java.io.InputStream

class API(private val context: Context) {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api-transfer-file.dimaspndw.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val apiService = retrofit.create(ApiService::class.java)
    private val apiServicePost = retrofit.create(ApiServicePost::class.java)
    private val apiServiceGetData = retrofit.create(ApiServiceGetData::class.java)

    // function to check user API
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

    // function to post data
    fun postData(uri: android.net.Uri, from: Int, to: Int, name: String) {
        val file = DocumentFile.fromSingleUri(context, uri)
        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)

        // Baca data dari InputStream dan berikan nilai default jika null
        val requestData = inputStream?.readBytes() ?: ByteArray(0)

        val requestFile = RequestBody.create(context.contentResolver.getType(uri)?.toMediaTypeOrNull(), requestData)
        val filePart = MultipartBody.Part.createFormData("file", name, requestFile)
        val nameRequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), name)
        val toRequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), to.toString())
        val fromRequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), from.toString())

        val call = apiServicePost.postData(filePart, nameRequestBody, toRequestBody, fromRequestBody)

        call.enqueue(object : Callback<ResponseClass> {
            override fun onResponse(call: Call<ResponseClass>, response: Response<ResponseClass>) {
                // Handle response
                if (response.isSuccessful) {
                    val message = response.body()?.message
                    DialogUtils.successPostData(context)
                } else {
                    DialogUtils.invalidPINDialog(context)
                }
            }

            override fun onFailure(call: Call<ResponseClass>, t: Throwable) {
                DialogUtils.invalidPINDialog(context)
            }
        })
    }

    // function to get data
    fun getData(code: Int, callback: (List<DataResponseClass>?) -> Unit) {
        val call = apiServiceGetData.getData(code)

        call.enqueue(object : Callback<DataResponseClass> {
            override fun onResponse(call: Call<DataResponseClass>, response: Response<DataResponseClass>) {
                // Handle response
                if (response.isSuccessful) {
                    val data = response.body()?.data
                    // Panggil callback dengan data yang diperoleh
                    callback(data)
                } else {
                    callback(null)
                }
            }

            override fun onFailure(call: Call<DataResponseClass>, t: Throwable) {
                callback(null)
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

interface ApiServicePost {
    @Multipart
    @POST("api/file")
    fun postData(
        @Part file: MultipartBody.Part,
        @Part("name") name: RequestBody,
        @Part("to") to: RequestBody,
        @Part("from") from: RequestBody
    ): Call<ResponseClass>
}

interface ApiServiceGetData {
    @GET("api/file/{id}")
    fun getData(
        @Path("id") id: Int
    ): Call<DataResponseClass>
}
