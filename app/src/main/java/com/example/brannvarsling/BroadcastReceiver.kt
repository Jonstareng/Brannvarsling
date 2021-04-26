package com.example.brannvarsling

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.opengl.Visibility
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.google.firebase.firestore.FirebaseFirestore
import me.leolin.shortcutbadger.ShortcutBadger


class BroadcastReceiver : BroadcastReceiver(){
    override fun onReceive(context: Context, intent: Intent?) {
        var count = 0
        ShortcutBadger.applyCount(context,count)
        val counter = intent?.getStringExtra("notifyId")
        val title = intent?.getStringExtra("title")
        val date = intent?.getStringExtra("date")
        val type = intent?.getStringExtra("text")
        val intent2 = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent? =
            counter?.let { PendingIntent.getActivity(context, it.toInt(),intent2, 0) }
        val notification: Notification? = NotificationCompat.Builder(context, "Cases ID")
                .setSmallIcon(R.drawable.assignment_24px)
                .setContentTitle(intent!!.getStringExtra("title"))
                .setContentText(intent.getStringExtra("text"))
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
                .setAutoCancel(true)
                .build()
        // Show notification
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(counter?.toInt()!!, notification)


        val notify: MutableMap<String, Any> = HashMap()

        notify["Customer"] = title.toString()
        notify["Date"] = date.toString()
        notify["Counter"] = counter.toLong()
        notify["Type"] = type.toString()
        FirebaseFirestore.getInstance().collection("Notifications").add(notify)
    }
}

