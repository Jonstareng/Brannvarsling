package com.example.brannvarsling.fragments

import android.app.AlarmManager
import android.app.AlertDialog
import android.app.PendingIntent
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.AlarmManagerCompat.setExactAndAllowWhileIdle
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.brannvarsling.*
import com.example.brannvarsling.dataClass.FirebaseNotification
import com.example.brannvarsling.databinding.FragmentNotificationBinding
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import java.time.LocalDate
import java.util.Calendar


class Notifications: Fragment(), NotificationAdapter.OnItemClickListnerN {
    private lateinit var binding: FragmentNotificationBinding
    private var db = FirebaseFirestore.getInstance()
    private var documentID = ""
    private var counter: Long = 0
    private var customer = ""
    private var date = ""
    private var type = ""
    var adapterR: NotificationAdapter? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_notification, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        createRecyclerView()
    }
    private fun createRecyclerView() {
        val query = db.collection("Notifications")

        //Toast.makeText(context, "$", Toast.LENGTH_LONG).show()
        val option: FirestoreRecyclerOptions<FirebaseNotification> = FirestoreRecyclerOptions.Builder<FirebaseNotification>()
            .setQuery(query, FirebaseNotification::class.java).build()

        adapterR = NotificationAdapter(option, this)

        binding.recyclerviewNotify.layoutManager = LinearLayoutManager(context)
        binding.recyclerviewNotify.adapter = adapterR

         ItemTouchHelper(
                object : ItemTouchHelper.SimpleCallback(0,
                        ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
                    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                        return false
                    }

                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                        val builder = AlertDialog.Builder(activity)
                        builder.setMessage("Er du sikker på at du vil slette varslingen")
                                .setCancelable(false)
                                .setPositiveButton("Slett", DialogInterface.OnClickListener { dialog, id ->
                                    adapterR?.swipeDelete(viewHolder.adapterPosition)
                                    dialog.dismiss()
                                }).setNegativeButton("Avbryt", DialogInterface.OnClickListener { dialog, id ->
                                    adapterR!!.notifyDataSetChanged()
                                    dialog.dismiss()
                                })
                        val alert = builder.create()
                        alert.show()

                    }
                }).attachToRecyclerView(binding.recyclerviewNotify)

    }

    override fun onStart() {
        super.onStart()
        adapterR?.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapterR?.stopListening()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onItemClick(id: String) {
        documentID = id
        getData()
        val builder = AlertDialog.Builder(activity)
        builder.setTitle("Notification")
            .setCancelable(false)
                .setMessage("Send ny varsel om en uke eller en dag")
                .setPositiveButton("Dag", DialogInterface.OnClickListener { dialog, id3 ->
                    db.collection("Notifications").document(id).delete()
                    scheduleNotificationDay()
                    dialog.dismiss()
                }).setNegativeButton("Uke", DialogInterface.OnClickListener { dialog, id4 ->
                    db.collection("Notifications").document(id).delete()
                    scheduleNotificationWeek()
                    dialog.dismiss()
                }).setNeutralButton("Avbryt", DialogInterface.OnClickListener { dialog, id2 ->
                    dialog.dismiss()
                })
        val alert = builder.create()
        alert.show()
    }
    @RequiresApi(Build.VERSION_CODES.M)
    private fun scheduleNotificationDay(){
        val intent = Intent(context, BroadcastReceiver::class.java)
        intent.putExtra("title", customer)
        intent.putExtra("text", type)
        intent.putExtra("notifyId", counter.toString())
        intent.putExtra("date", date)
        val pending = PendingIntent.getBroadcast(context, counter.toInt(), intent, PendingIntent.FLAG_UPDATE_CURRENT)
        // Schdedule notification
        val calendar: Calendar = Calendar.getInstance()
        calendar.add(Calendar.DATE, 1)
        val time = calendar.timeInMillis
        Toast.makeText(context, "Varsling satt til i morgen", Toast.LENGTH_LONG).show()
        val manager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        manager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time, pending)
    }
    @RequiresApi(Build.VERSION_CODES.M)
    private fun scheduleNotificationWeek(){
        val intent = Intent(context, BroadcastReceiver::class.java)
        intent.putExtra("title", customer)
        intent.putExtra("text", type)
        intent.putExtra("notifyId", counter.toString())
        intent.putExtra("date", date)
        val pending = PendingIntent.getBroadcast(context, counter.toInt(), intent, PendingIntent.FLAG_UPDATE_CURRENT)
        // Schdedule notification
        val calendar: Calendar = Calendar.getInstance()
        calendar.add(Calendar.DATE, 7)
        Toast.makeText(context, "Varsling utsatt en uke", Toast.LENGTH_LONG).show()
        val time = calendar.timeInMillis
        val manager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        manager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time, pending)
    }
    private fun getData() {

        val ref = db.collection("Notifications").document(documentID)
        ref.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                counter = snapshot.get("Counter") as Long
                customer = (snapshot.get("Customer")).toString()
                type = snapshot.get("Type").toString()
            }
        }
    }
}



