package com.example.brannvarsling

import android.R
import android.accessibilityservice.GestureDescription
import android.app.Notification
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat


class BroadcastReceiver: BroadcastReceiver(){

    override fun onReceive(context: Context, intent: Intent?) {

        val notification: Notification? = NotificationCompat.Builder(context, "id")
                .setSmallIcon(R.drawable.ic_delete)
                .setContentTitle(intent!!.getStringExtra("title"))
                .setContentText(intent.getStringExtra("text"))
                .build()
        // Show notification
        // Show notification
        val manager = context!!.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(42, notification)
    }
}

