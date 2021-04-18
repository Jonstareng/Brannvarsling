package com.example.brannvarsling

import android.R
import android.accessibilityservice.GestureDescription
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat


class BroadcastReceiver: BroadcastReceiver(){

    override fun onReceive(context: Context, intent: Intent?) {

        val intent2 = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0,intent2, 0)
        val notification: Notification? = NotificationCompat.Builder(context, "Cases ID")
                .setSmallIcon(R.drawable.ic_delete)
                .setContentTitle(intent!!.getStringExtra("title"))
                .setContentText(intent.getStringExtra("text"))
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build()
        // Show notification
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(0, notification)
    }
}

