package com.example.transferingfileapp.handler
import android.content.Context
import android.os.Handler
import android.os.Looper
import com.example.transferingfileapp.utils.DialogUtils
import com.example.transferingfileapp.utils.NetwokUtils

class NetworkCheckHandler(private val context: Context) {
    private val handler = Handler(Looper.getMainLooper())
    private val checkInterval = 5000L // 5 seconds (adjust as needed)

    private val checkRunnable = object : Runnable {
        override fun run() {
            if (!NetwokUtils.isInternetAvailable(context)) {
                DialogUtils.showNoInternetDialog(context)
            }

            // Schedule the next check
            handler.postDelayed(this, checkInterval)
        }
    }

    fun startChecking() {
        // Initial check
        handler.post(checkRunnable)
    }

    fun stopChecking() {
        handler.removeCallbacks(checkRunnable)
    }
}