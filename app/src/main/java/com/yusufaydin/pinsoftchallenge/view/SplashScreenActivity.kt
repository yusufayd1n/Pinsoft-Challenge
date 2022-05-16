package com.yusufaydin.pinsoftchallenge.view

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import com.yusufaydin.pinsoftchallenge.databinding.ActivitySplashScreenBinding

class SplashScreenActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashScreenBinding
    lateinit var handler: Handler
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (isConnected() == false) {
            var builder = AlertDialog.Builder(this)
            builder.setTitle("No Internet Connection")
            builder.setMessage("Try to connect internet for this app")
            builder.setPositiveButton("Later") { dialogInterface, which ->
                finish()
                System.exit(0)
            }
            builder.setNegativeButton("Check Wifi") { dialogInterface, which ->
                startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
                finish()
                System.exit(0)
            }
            val alertDialog: AlertDialog = builder.create()
            alertDialog.setCancelable(false)
            alertDialog.show()
        } else {
            handler = Handler()
            handler.postDelayed({
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }, 1000)
        }


    }

    private fun isConnected(): Boolean {
        var connectivityManager: ConnectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        var network: NetworkInfo? = connectivityManager.activeNetworkInfo
        if (network != null) {
            if (network.isConnected) {
                return true
            }
        }
        return false
    }
}

