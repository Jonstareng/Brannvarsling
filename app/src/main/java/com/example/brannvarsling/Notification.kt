package com.example.brannvarsling

import android.app.IntentService
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import java.util.*

private var channelID = "ChannelID"
private var notificationId = 101
private lateinit var mNotification: Notification
private const val mNotificationId: Int = 1000
private lateinit var builder: android.app.Notification


class Notification: IntentService("notification") {
      private fun createChannel() {


          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

              // Create the NotificationChannel, but only on API 26+ because
              // the NotificationChannel class is new and not in the support library

              val context = this.applicationContext
              val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

              val importance = NotificationManager.IMPORTANCE_HIGH
              val notificationChannel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance)
              notificationChannel.enableVibration(true)
              notificationChannel.setShowBadge(true)
              notificationChannel.enableLights(true)
              notificationChannel.lightColor = Color.parseColor("#e8334a")
              notificationChannel.description = getString(R.string.avbryt)
              notificationManager.createNotificationChannel(notificationChannel)
          }

      }

      companion object {

          const val CHANNEL_ID = "samples.notification.devdeeds.com.CHANNEL_ID"
          const val CHANNEL_NAME = "Sample Notification"
      }


      override fun onHandleIntent(intent: Intent?) {

          //Create Channel
          createChannel()


          var timestamp: Long = 0
          if (intent != null && intent.extras != null) {
              timestamp = intent.extras!!.getLong("timestamp")
          }




          if (timestamp > 0) {


              val context = this.applicationContext
              var notificationManager: NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
              val notifyIntent = Intent(this, MainActivity::class.java)

              val title = "Sample Notification"
              val message = "You have received a sample notification. This notification will take you to the details page."

              notifyIntent.putExtra("title", title)
              notifyIntent.putExtra("message", message)
              notifyIntent.putExtra("notification", true)

              notifyIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

              val calendar = Calendar.getInstance()
              calendar.timeInMillis = timestamp


              val pendingIntent = PendingIntent.getActivity(context, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT)
              val res = this.resources
              val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

              if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {


                  builder = NotificationCompat.Builder(this, CHANNEL_ID)
                          // Set the intent that will fire when the user taps the notification
                          .setContentIntent(pendingIntent)
                          .setSmallIcon(R.drawable.assignment_24px)
                          .setLargeIcon(BitmapFactory.decodeResource(res, R.mipmap.ic_launcher))
                          .setAutoCancel(true)
                          .setContentTitle(title)
                          .setContentText(message).build()
              } else {

                  builder = this.let {
                      NotificationCompat.Builder(it, CHANNEL_ID)
                              // Set the intent that will fire when the user taps the notification
                              .setContentIntent(pendingIntent)
                              .setSmallIcon(R.drawable.assignment_24px)
                              .setLargeIcon(BitmapFactory.decodeResource(res, R.mipmap.ic_launcher))
                              .setAutoCancel(true)
                              .setPriority((NotificationCompat.PRIORITY_DEFAULT))
                              .setContentTitle(title)
                              .setSound(uri)
                              .setContentText(message).build()

                  }
              }



              notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
              // mNotificationId is a unique int for each notification that you must define
              notificationManager.notify(mNotificationId, builder)
          }


      }

   /*
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

    */

  }
