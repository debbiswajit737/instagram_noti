package com.noti.teli

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.content.Context
import android.os.SystemClock
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import com.epaymark.mynotification.Common.Companion.lastClickTime1
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class YourNotificationListenerService : NotificationListenerService() {

    private val client = OkHttpClient() // Initialize OkHttp client

    @SuppressLint("NewApi")
    override fun onNotificationPosted(sbn: StatusBarNotification) {
        super.onNotificationPosted(sbn)

        // Handle the new notification
        val packageName = sbn.packageName
        //val notificationText = sbn.notification.extras.getCharSequence("android.text").toString()
        val sharedPreferences = applicationContext.getSharedPreferences("EPAY_SHAREDFREFFRENCE", Context.MODE_PRIVATE)

        // Retrieve values from SharedPreferences
        val value = sharedPreferences.getString("URL", "")

        // Use the retrieved value
        Log.d("MyService", "Retrieved value from SharedPreferences: $value")
        // Perform the API call on a background thread
        Thread {
            try {

               /* Log.d("TAG_getPostTime", "time: "+sbn.getPostTime())
                Log.d("TAG_getPostTime", ":ID "+sbn.getId())
                var timeString = millisecondsToTime(sbn.postTime)
                val notificationText =
                    sbn.notification.extras.getCharSequence("android.text").toString()

                val notificationTitle =
                    sbn.notification.extras.getCharSequence("android.title").toString()

                Log.d("TAG_getPostTime", "notificationText: "+notificationText)
                Log.d("TAG_getPostTime", ":notificationTitle "+notificationTitle)*/
                Log.d("TAG_packageName", "packageName: "+packageName)
                if (packageName=="org.telegram.messenger" || packageName=="org.telegram.messenger.web") {
                    Log.d("TAG_tele", "onNotificationPosted: 1")
                    val packageName = sbn.packageName

                    var timeString = millisecondsToTime(sbn.postTime)
                    val notificationText =
                        sbn.notification.extras.getCharSequence("android.text").toString()

                    val notificationTitle =
                        sbn.notification.extras.getCharSequence("android.title").toString()
                Log.d("TAG_noti", "--------------------")
                Log.d("TAG_noti", "onNotificationPosted:1 "+timeString)
                Log.d("TAG_noti", "onNotificationPosted:1 "+notificationText)
                Log.d("TAG_noti", "notificationTitle:1 "+notificationTitle)
                    Log.d("TAG_noti", "****************")
                    if (!(SystemClock.elapsedRealtime() - lastClickTime1 < 1500)) {
                        val response = makeApiCall(notificationText,notificationTitle,timeString,value) // Call your API function

                    }

                    //Log.d("NotificationListenersms", "API1 packageName: $packageName")
                    //Log.d("NotificationListenersms", "API2 packageName: $notificationText")
                    try {
                        val notification = sbn.notification
                        //Log.d("TAG_sss", "response: "+response)
                        // Get the NotificationManager
                        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

                        // Clear the notification by its ID
                        //notificationManager.cancel(notification.channelId)
                    }catch (e:Exception){
                        Log.d("TAG_tele", "onNotificationPosted: 1 "+e.message)
                    }
                    try {
                        val notificationId = sbn.getId()

                        // Get the NotificationManager
                        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

                        // Clear the notification by its ID
                        notificationManager.cancel(notificationId)
                    }catch (e:Exception){
                        //Log.d("TAG_error", "onNotificationPosted: "+e.message)
                    }


                    //val notificationExtras = sbn.notification.extras

                    /*for (key in notificationExtras.keySet()) {
                        val value = notificationExtras.get(key)
                        Log.d("NotificationListenersms", "Key: $key, Value: $value")
                    }*/

                    /*
                    API1 packageName: org.telegram.messenger
API2 packageName: Hello bondhu
Key: android.title, Value: Hafizur Da Bdas
Key: android.hiddenConversationTitle, Value: null
Key: android.reduced.images, Value: true
Key: android.subText, Value: null
Key: android.template, Value: android.app.Notification$MessagingStyle
Key: android.showChronometer, Value: false
Key: android.people.list, Value: [android.app.Person@c1e0a0f9]
Key: android.text, Value: Hello bondhu
Key: android.progress, Value: 0
Key: androidx.core.app.extra.COMPAT_TEMPLATE, Value: androidx.core.app.NotificationCompat$MessagingStyle
Key: android.progressMax, Value: 0
Key: android.selfDisplayName, Value:
Key: android.conversationUnreadMessageCount, Value: 0
Key: android.appInfo, Value: ApplicationInfo{da231eb org.telegram.messenger}
Key: android.messages, Value: [Landroid.os.Parcelable;@70b48
Key: android.showWhen, Value: true
Key: in_conference_scene_mode, Value: false
Key: android.largeIcon, Value: null
Key: android.messagingStyleUser, Value: Bundle[mParcelledData.dataSize=144]
Key: android.messagingUser, Value: android.app.Person@34e6d5e1
Key: android.infoText, Value: null
Key: android.wearable.EXTENSIONS, Value: Bundle[mParcelledData.dataSize=536]
Key: android.progressIndeterminate, Value: false
Key: android.remoteInputHistory, Value: null
Key: in_full_screen_mode, Value: false
Key: android.isGroupConversation, Value: false
                     */
                    /*val notificationText =
                        sbn.notification.extras.getCharSequence("android.text").toString()
                    val response = makeApiCall(notificationText) // Call your API function
                    val responseBody = response?.body?.string()
                   // val appContext: Context = applicationContext

                    // Create a Toast message using the app context
                    //Toast.makeText(appContext, "Notification from $packageName: $notificationText", Toast.LENGTH_SHORT).show()
                    Log.d("NotificationListener", "API Response: $responseBody")
                    Log.d("NotificationListener", "API Response: $responseBody")
                        //Log.d("NotificationListener", "API Response: $responseBody")*/
                }
                
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }.start()
    }

    // Function to make an API call
    private fun makeApiCall(
        notificationText: String,
        notificationTitle: String,
        timeString: String,
        value: String?
    ) {
        Log.d("TAG_noti", "API CAll "+notificationText)
        val jsonMediaType = "application/json; charset=utf-8".toMediaType()
        val  requestBody: JSONObject = JSONObject()
        requestBody.put("msg",notificationText)
        requestBody.put("time",""+timeString)
        requestBody.put("sender",notificationTitle)
        Log.d("TAG_calender_time", "timeString: "+timeString)



        // Create a Calendar instance
        val calendar = Calendar.getInstance()
        calendar.time= Date()


// Define the desired output format
        val teleDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

// Format the date from the Calendar instance into the desired output format
        val formattedDate: String = teleDate.format(calendar.time)
        Log.d("TAG_date", "makeApiCall: "+formattedDate)
// Output the formatted date string



        /*val calendar = Calendar.getInstance()

// Set the date and time in the Calendar instance (optional)
        calendar.set(Calendar.YEAR, 2022)
        calendar.set(Calendar.MONTH, Calendar.DECEMBER) // Note: Calendar.MONTH is zero-based, so December is represented by 11
        calendar.set(Calendar.DAY_OF_MONTH, 31)
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)*/

// Define the desired output format
        val outputFormat = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault())

// Format the date from the Calendar instance into the desired output format
        //val formattedDate: String = outputFormat.format(calendar.time)

// Output the formatted date string
       // println(formattedDate)

        val request = Request.Builder()
            .url("https://big9.co.in/telegram.php?msg=$notificationText*$notificationTitle*$formattedDate")
            //.url("https://big9.co.in/telegram.php?msg=$notificationText*$notificationTitle*$formattedDate")
            //.url("https://test.com")

            .post(requestBody.toString().toRequestBody(jsonMediaType))
            .build()

        //return client.newCall(request).execute()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // Handle failure
                Log.d("TAG_response", "onNotificationPosted: error "+e.message)
            }

            override fun onResponse(call: Call, response: Response) {

                val responseData = response.body?.string() // Get the response body as a string
                Log.d("TAG_response", "onNotificationPosted: response "+responseData.toString())
                // Process the response data
                // Note: This code runs on the background thread, so if you need to update UI components,
                // you should use runOnUiThread or another mechanism to switch to the main thread
            }
        })


    }

    override fun onListenerConnected() {
        super.onListenerConnected()
        Log.d("NotificationListener", "Listener connected")
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        super.onNotificationRemoved(sbn)

        // Handle the removed notification
        val packageName = sbn.packageName
        val notificationText = sbn.notification.extras.getCharSequence("android.text").toString()

       // Log.d("NotificationListener", "Notification removed from $packageName: $notificationText")
    }

    fun millisecondsToTime(milliseconds: Long): String {
        val seconds = (milliseconds / 1000).toInt()
        val days = seconds / (24 * 3600)
        val hours = (seconds % (24 * 3600)) / 3600
        val minutes = (seconds % 3600) / 60
        val remainingSeconds = seconds % 60

        return String.format("%d hours, %d minutes, %d seconds", days, hours, minutes, remainingSeconds)
    }
}