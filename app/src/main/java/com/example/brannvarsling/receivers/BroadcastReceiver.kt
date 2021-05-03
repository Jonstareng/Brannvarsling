package com.example.brannvarsling.receivers

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.brannvarsling.MainActivity
import com.example.brannvarsling.R
import com.google.android.material.badge.BadgeUtils
import com.google.firebase.firestore.FirebaseFirestore


class BroadcastReceiver : BroadcastReceiver(){

    // funksjonen tar i mot intenter fra funksjonene i andre klasser som er koblet opp mot denne recieveren
    override fun onReceive(context: Context, intent: Intent?) {
        // henter ut intentene fra klassene som sender de
        val counter = intent?.getStringExtra("notifyId")
        val title = intent?.getStringExtra("title")
        // Legger tittelen som ønskes å vises i notifikasjonen på mobilen i en val
        val dataT = "Utfør kontoll hos: $title"
        val date = intent?.getStringExtra("date")
        val type = intent?.getStringExtra("text")
        // Legger teksten som ønskes å vises i notifikasjonen på mobilen i en val
        val dataTy = "Type kontroll: $type"
        // setter hvor brukeren ender opp når de trykker på notifikasjonen på mobilen
        val intent2 = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        // her setter vi iden på notifikasjonen, it.toInt() eller counter er iden  som blir send med via en intent
        val pendingIntent: PendingIntent? =
            counter?.let { PendingIntent.getActivity(context, it.toInt(),intent2, 0) }
        // Bygger opp notifikasjonen
        val notification: Notification? = NotificationCompat.Builder(context, "Cases ID")
                .setSmallIcon(R.drawable.assignment_24px)
                .setContentTitle(dataT)
                .setContentText(dataTy)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
                .setAutoCancel(true)
                .setBadgeIconType(NotificationCompat.BADGE_ICON_LARGE)
                .build()
        // Show notification
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(counter?.toInt()!!, notification)

        // lagrer intentene i databasen slik at vi kan hente ut verdiene og vise de i recyclerviewt i Notifications klassen
        val notify: MutableMap<String, Any> = HashMap()

        notify["Customer"] = title.toString()
        notify["Date"] = date.toString()
        notify["Counter"] = counter.toLong()
        notify["Type"] = type.toString()
        FirebaseFirestore.getInstance().collection("Notifications").add(notify)
    }
}

