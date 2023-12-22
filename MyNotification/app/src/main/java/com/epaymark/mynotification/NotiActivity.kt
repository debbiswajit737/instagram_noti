package com.epaymark.mynotification

import android.app.ActivityManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.NotificationManagerCompat
import com.epaymark.mynotification.Common.Companion.isCallApi
import com.epaymark.mynotification.Common.Companion.savedUrl
import com.epaymark.mynotification.databinding.ActivityNotiBinding

class NotiActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNotiBinding
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ;//setContentView(R.layout.activity_noti)
        binding = ActivityNotiBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //setContentView(R.layout.activity_main)
        //notificationServiceIntent = Intent(this, NotificationService::class.java)
        val serviceIntent = Intent(this, ActivityNotiBinding::class.java)
        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        if (!isNotificationListenerEnabled()) {
            // Request permission if not enabled
            requestNotificationListenerPermission()
        }

        binding.btnStart.setOnClickListener {
            isCallApi=true
            isServiceRunning()
        }

        binding.btnStop.setOnClickListener {
            isCallApi=false
            isServiceRunning()
        }
        binding.saveButton.setOnClickListener {

            val url = binding.urlEditText.text.toString()

            // Save the URL to SharedPreferences
            with(sharedPreferences.edit()) {
                putString("savedUrl", url)
                apply()
            }




           /* binding.startServiceButton.setOnClickListener {
                startService(serviceIntent)
                //startService(serviceIntent)
                isServiceRunning()
            }

            binding.stopServiceButton.setOnClickListener {
                //stopService(serviceIntent)
                stopService(serviceIntent)
                isServiceRunning()
            }*/
            updateButtonsVisibility()

        }

        // Check if URL is already saved and update buttons visibility
        updateButtonsVisibility()

        isServiceRunning()

    }
    fun isServiceRunning(){
        // Check if the service is running
       // val isServiceRunning = isNotificationServiceRunning(ActivityNotiBinding::class.java)
        if (isCallApi) {
            binding.btnStart.background = getDrawable(R.drawable.button_background_3)
            binding.btnStop.background = getDrawable(R.drawable.button_background)
        } else {
            binding.btnStart.background = getDrawable(R.drawable.button_background)
            binding.btnStop.background = getDrawable(R.drawable.button_background_3)
        }
    }
    private fun updateButtonsVisibility() {
         savedUrl = sharedPreferences.getString("savedUrl", null).toString()
        if (!savedUrl.isNullOrBlank()) {
            binding.tvUrl.text=savedUrl
           // binding.serviceButtonsLayout.visibility = View.VISIBLE
        } else {
            binding.tvUrl.text="No Url"
           // binding.serviceButtonsLayout.visibility = View.GONE
        }
    }
    private fun isNotificationServiceRunning(serviceClass: Class<*>): Boolean {
        val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }

    private fun isNotificationListenerEnabled(): Boolean {
        val componentName = ComponentName(this, YourNotificationListenerService::class.java)
        val enabledListeners = NotificationManagerCompat.getEnabledListenerPackages(this)
        return enabledListeners.contains(componentName.packageName)
    }

    private fun requestNotificationListenerPermission() {
        val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
        startActivity(intent)
    }
}