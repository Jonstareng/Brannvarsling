package com.example.brannvarsling

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
/*
private var channelID = "ChannelID"
private var notificationId = 101

  class NotificationUtils {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createNotificationChannel()
        sendNotification()
    }
    private fun sendNotification() {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, 0)
        val builder = this.let {
            NotificationCompat.Builder(it, channelID)
                    .setSmallIcon(R.drawable.assignment_24px)
                    .setContentTitle("Kontroll for Kunde 1")
                    .setContentText("kontroll må gjennomføres innen 1 uke")
                    .setContentIntent(pendingIntent)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        }

        with(NotificationManagerCompat.from(this)) {
                this.notify(notificationId, builder.build())

        }
    }
    private fun createNotificationChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val notificationName = "Case notification"
            val descriptionText = "Notification description"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel: NotificationChannel = NotificationChannel(channelID, notificationName, importance).apply {
                description= descriptionText
            }
            val notificationManager: NotificationManager = (this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)!!
            notificationManager.createNotificationChannel(channel)
        }

    }
}

 */