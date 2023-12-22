package com.epaymark.mynotification

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val b1 = findViewById<Button>(R.id.button)
        val b2 = findViewById<Button>(R.id.button2)
        val editTexturl = findViewById<EditText>(R.id.url)

        if (!NotificationManagerCompat.getEnabledListenerPackages(this)
                .contains(packageName)
        ) {        //ask for permission
            val intent = Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS")
            startActivity(intent)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            if (!NotificationManagerCompat.getEnabledListenerPackages(this)
                    .contains(packageName)
            ) {        //ask for permission
                val intent = Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS")
                startActivity(intent)
            }
        }
        b1.setOnClickListener{
            if(!editTexturl.text.toString().isNullOrBlank()) {
                val settings = getSharedPreferences("EPAY_SHAREDFREFFRENCE", Context.MODE_PRIVATE)
                val editor = settings.edit()

                editor.putString("URL", editTexturl.text.toString())

                editor.apply()


            }

            val serviceIntent = Intent(this, YourNotificationListenerService::class.java)
            startService(serviceIntent)

        }
        b2.setOnClickListener {
            val serviceIntent = Intent(this, YourNotificationListenerService::class.java)
            stopService(serviceIntent)
        }


        //finish()
       /* val intent = Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS")
        startActivity(intent)*/
    }
}