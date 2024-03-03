package com.example.transferingfileapp.utils
import android.app.AlertDialog
import android.content.Context

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
}