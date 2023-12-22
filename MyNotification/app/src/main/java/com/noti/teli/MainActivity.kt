package com.noti.teli

import android.app.ActivityManager
import android.content.ComponentName
import android.content.Context

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.noti.teli.databinding.ActivityMainBinding
import android.content.Intent
import android.provider.Settings
import androidx.core.app.NotificationManagerCompat

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var sharedPreferences: SharedPreferences
    //private lateinit var notificationServiceIntent: Intent
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //setContentView(R.layout.activity_main)
        //notificationServiceIntent = Intent(this, NotificationService::class.java)
        val serviceIntent = Intent(this, NotificationService::class.java)
        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        if (!isNotificationListenerEnabled()) {
            // Request permission if not enabled
            requestNotificationListenerPermission()
        }
        binding.saveButton.setOnClickListener {
            val url = binding.urlEditText.text.toString()

            // Save the URL to SharedPreferences
            with(sharedPreferences.edit()) {
                putString("savedUrl", url)
                apply()
            }

            binding.startServiceButton.setOnClickListener {

                startService(serviceIntent)
                isServiceRunning()
            }

            binding.stopServiceButton.setOnClickListener {
                stopService(serviceIntent)
                isServiceRunning()
            }
            updateButtonsVisibility()

        }

        // Check if URL is already saved and update buttons visibility
        updateButtonsVisibility()

        isServiceRunning()

    }
    fun isServiceRunning(){
        // Check if the service is running
        val isServiceRunning = isNotificationServiceRunning(NotificationService::class.java)
        if (isServiceRunning) {
            binding.startServiceButton.background = getDrawable(R.drawable.button_background_3)
            binding.stopServiceButton.background = getDrawable(R.drawable.button_background)
        } else {
            binding.startServiceButton.background = getDrawable(R.drawable.button_background)
            binding.stopServiceButton.background = getDrawable(R.drawable.button_background_3)
        }
    }
    private fun updateButtonsVisibility() {
        val savedUrl = sharedPreferences.getString("savedUrl", null)
        if (!savedUrl.isNullOrBlank()) {
            binding.tvUrl.text=savedUrl
            binding.serviceButtonsLayout.visibility = View.VISIBLE
        } else {
            binding.tvUrl.text="No Url"
            binding.serviceButtonsLayout.visibility = View.GONE
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
        val componentName = ComponentName(this, NotificationService::class.java)
        val enabledListeners = NotificationManagerCompat.getEnabledListenerPackages(this)
        return enabledListeners.contains(componentName.packageName)
    }

    private fun requestNotificationListenerPermission() {
        val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
        startActivity(intent)
    }
}