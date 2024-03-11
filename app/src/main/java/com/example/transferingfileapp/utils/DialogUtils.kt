package com.example.transferingfileapp.utils
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import com.example.transferingfileapp.HomeActivity

object DialogUtils {
    fun showNoInternetDialog(context: Context) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Koneksi Tidak Tersedia")
            .setMessage("Pastikan perangkat Anda terhubung ke internet.")
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }

        val alertDialog = builder.create()
        alertDialog.show()
    }

    fun invalidPINDialog(context: Context) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("PIN Anda tidak valid!!")
            .setMessage("PIN anda tidak terdaftar di dalam sistem.")
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }

        val alertDialog = builder.create()
        alertDialog.show()
    }

    fun successPostData(context: Context) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Berhasil!!")
            .setMessage("File berhasil dikirimkan.")
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
                // Mulai ulang HomeActivity
                val intent = Intent(context, HomeActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
            }

        val alertDialog = builder.create()
        alertDialog.show()
    }
}