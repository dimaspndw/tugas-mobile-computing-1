package com.example.transferingfileapp.requestAPI

import android.app.Dialog
import android.app.ProgressDialog
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
import com.example.transferingfileapp.R
import com.example.transferingfileapp.model.DataItem
import com.example.transferingfileapp.model.DataResponseClass
import com.example.transferingfileapp.utils.DialogUtils
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor

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
    private val apiServiceDownloadData = retrofit.create(ApiServiceDownloadData::class.java)
    private var progressDialog: Dialog? = null

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
        showLoading()
        val file = DocumentFile.fromSingleUri(context, uri)
        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)

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
                    Log.d("Response", "Success: $message")
                    hideLoading()
                    DialogUtils.successPostData(context)
                } else {
                    Log.d("Response", "Error: ${response.code()} - ${response.message()}")
                    hideLoading()
                    DialogUtils.invalidPINDialog(context)
                }
            }

            override fun onFailure(call: Call<ResponseClass>, t: Throwable) {
                hideLoading()
                DialogUtils.invalidPINDialog(context)
            }
        })
    }

    // function to get data
    fun getData(code: Int, callback: (List<DataItem>?) -> Unit) {
        val call = apiServiceGetData.getData(code)
        call.enqueue(object : Callback<DataResponseClass> {
            override fun onResponse(call: Call<DataResponseClass>, response: Response<DataResponseClass>) {
                if (response.isSuccessful) {
                    val data = response.body()?.data
                    callback(data)
                } else {
                    DialogUtils.invalidPINDialog(context)
                    callback(null)
                }
            }

            override fun onFailure(call: Call<DataResponseClass>, t: Throwable) {
                DialogUtils.invalidPINDialog(context)
                callback(null)
            }
        })
    }

    fun downloadFile(id: Int, callback: (ResponseBody?, MediaType?) -> Unit) {
        val call = apiServiceDownloadData.downloadFile(id)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val contentType = response.body()?.contentType()
                    callback(response.body(), contentType)
                } else {
                    DialogUtils.invalidPINDialog(context)
                    callback(null, null)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                DialogUtils.invalidPINDialog(context)
                callback(null, null)
            }
        })
    }

    private fun showLoading() {
        progressDialog = Dialog(context)
        progressDialog?.setContentView(R.layout.layout_dialog)
        progressDialog?.setCancelable(false)
        progressDialog?.show()
    }

    private fun hideLoading() {
        progressDialog?.dismiss()
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
    @GET("api/file-list/{id}")
    fun getData(
        @Path("id") id: Int
    ): Call<DataResponseClass>
}

interface ApiServiceDownloadData {
    @GET("api/file-download/{id}")
    fun downloadFile(
        @Path("id") id: Int
    ): Call<ResponseBody>
}
