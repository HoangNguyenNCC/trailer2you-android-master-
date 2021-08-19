package com.applutions.t2y.firebase

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.applutions.t2y.R
import com.applutions.t2y.SplashActivity
import com.applutions.t2y.utils.Constants
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.json.JSONObject
import java.util.*

class MyFirebaseNotificationService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteNotification: RemoteMessage) {
        super.onMessageReceived(remoteNotification)
        if (remoteNotification != null)
            remoteNotification.data["message"]?.let { setUpNotification(it) }
    }

    private fun setUpNotification(notificationData: String) {
        if (notificationData != null) {
            try {
                val notificationObject = JSONObject(notificationData)
                val intent: Intent
                val notificationId = Random().nextInt(1000)
                val id: String = notificationObject.getString("id")

                if (id == Constants.BOOK_RENTAL) {
                    intent = Intent(this, SplashActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    val pendingIntent = PendingIntent.getActivity(
                        this,
                        notificationId,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                    )
                    showSimpleTextNotification(
                        pendingIntent,
                        notificationObject.getString("message"),
                        notificationObject.getString("message"),
                        notificationId
                    )
                }


            } catch (e: Exception) {
                e.printStackTrace()
               /* HelperMethods.showDebugToastBar(
                    TrailerApplication.getApplicationInstance().applicationContext,
                    e.message
                )*/
            }
        }
    }

    @SuppressLint("ResourceAsColor")
    private fun showSimpleTextNotification(
        notificationIntent: PendingIntent,
        notificationTitle: String,
        notificationBody: String,
        notificationId: Int
    ) {

        val notificationManager =
            this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationBuilder =
            NotificationCompat.Builder(this, Constants.NOTIFICATION_CHANNEL_ID)

        notificationBuilder.setSmallIcon(R.mipmap.ic_launcher)
        notificationBuilder.setContentTitle(notificationTitle)
        notificationBuilder.setContentText(notificationBody)
        notificationBuilder.priority = NotificationCompat.PRIORITY_HIGH
        notificationBuilder.setAutoCancel(true)
        notificationBuilder.color = R.color.themeDark
        notificationBuilder.setDefaults(NotificationCompat.DEFAULT_ALL)
        notificationBuilder.setContentIntent(notificationIntent)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (notificationManager != null && notificationManager.getNotificationChannel(Constants.NOTIFICATION_CHANNEL_ID) == null) {

                val notificationChannel = NotificationChannel(
                    Constants.NOTIFICATION_CHANNEL_ID,
                    "Notification Channel",
                    NotificationManager.IMPORTANCE_HIGH
                )
                notificationChannel.enableLights(true)
                notificationChannel.enableVibration(true)
                notificationManager.createNotificationChannel(notificationChannel)
            }
        }
        notificationManager.notify(notificationId, notificationBuilder.build())
    }
}