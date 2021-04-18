package com.example.brannvarsling

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import kotlin.random.Random


class BroadcastReceiver : BroadcastReceiver(){
    override fun onReceive(context: Context, intent: Intent?) {

        val intent2 = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val id = 0
        val pendingIntent: PendingIntent = PendingIntent.getActivity(context, id,intent2, 0)
        val notification: Notification? = NotificationCompat.Builder(context, "Cases ID")
                .setSmallIcon(com.example.brannvarsling.R.drawable.assignment_24px)
                .setContentTitle(intent!!.getStringExtra("title"))
                .setContentText(intent.getStringExtra("text"))
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build()
        // Show notification
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(id, notification)
    }

}

