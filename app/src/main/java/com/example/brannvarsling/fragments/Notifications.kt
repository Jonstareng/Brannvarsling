package com.example.brannvarsling.fragments

import android.app.AlarmManager
import android.app.AlertDialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.brannvarsling.receivers.BroadcastReceiver
import com.example.brannvarsling.adapters.NotificationAdapter
import com.example.brannvarsling.R
import com.example.brannvarsling.dataClass.FirebaseNotification
import com.example.brannvarsling.databinding.FragmentNotificationBinding
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
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
    // Samme som i cases klassen, kobler adapteret opp mot recyclerviewet
    private fun createRecyclerView() {
        val query = db.collection("Notifications")

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
                    // Åpner en alertdialog når man swiper for å kontrollere at brukeren faktisk ønsker å slette notifikasjonen.
                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                        val builder = AlertDialog.Builder(activity)
                        builder.setMessage("Er du sikker på at du vil slette varslingen")
                                .setCancelable(false)
                                .setPositiveButton("Slett") { dialog, _ ->
                                    adapterR?.swipeDelete(viewHolder.adapterPosition)
                                    dialog.dismiss()
                                }
                            .setNegativeButton("Avbryt") { dialog, _ ->
                                adapterR!!.notifyDataSetChanged()
                                dialog.dismiss()
                            }
                        val alert = builder.create()
                        alert.show()

                    }
                }).attachToRecyclerView(binding.recyclerviewNotify)

    }
    // starter adapteret
    override fun onStart() {
        super.onStart()
        adapterR?.startListening()
    }
    //Stopper adapteret
    override fun onStop() {
        super.onStop()
        adapterR?.stopListening()
    }

    // alert dialog som kaller på klassene for å utsette notifikasjonene
    // Siden dennee funksjonen kaller på en klasse som har @RequiresApi(Build.VERSION_CODES.M), må også den stå her
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onItemClick(id: String) {
        documentID = id
        getData()
        val builder = AlertDialog.Builder(activity)
        builder
                .setCancelable(false)
                .setMessage("Send ny varsel om en uke eller en dag")
                .setPositiveButton("Dag") { dialog, _ ->
                    db.collection("Notifications").document(id).delete()
                    scheduleNotificationDay()
                    dialog.dismiss()
                }.setNegativeButton("Uke") { dialog, _ ->
                db.collection("Notifications").document(id).delete()
                scheduleNotificationWeek()
                dialog.dismiss()
            }.setNeutralButton("Avbryt") { dialog, _ ->
                dialog.dismiss()
            }
        val alert = builder.create()
        alert.show()
    }

    // Starter en ny alarm med samme notifikasjons id som notifikasjonen allerede hadde,
    // sender de samme intentene som notifikasjonen opprinnelig hadde
    // Alarmen er satt til en dag etter at funksjonen blir kalt på
    // Koblet opp mot samme Broadcast receiver som alle andre notifikasjoner
    @RequiresApi(Build.VERSION_CODES.M)
    private fun scheduleNotificationDay(){
        val intent = Intent(context, BroadcastReceiver::class.java)
        intent.putExtra("title", customer)
        intent.putExtra("text", type)
        intent.putExtra("notifyId", counter.toString())
        intent.putExtra("date", date)
        val pending = PendingIntent.getBroadcast(context, counter.toInt(), intent, PendingIntent.FLAG_UPDATE_CURRENT)
        val calendar: Calendar = Calendar.getInstance()
        // setter alarmen til om en dag
        calendar.add(Calendar.DATE, 1)
        val time = calendar.timeInMillis
        Toast.makeText(context, "Varsling satt til i morgen", Toast.LENGTH_LONG).show()
        val manager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        manager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time, pending)
    }

    // Samme kode som funksjonen over, bortsett fra at alarmen går av om en uke
    @RequiresApi(Build.VERSION_CODES.M)
    private fun scheduleNotificationWeek(){
        val intent = Intent(context, BroadcastReceiver::class.java)
        intent.putExtra("title", customer)
        intent.putExtra("text", type)
        intent.putExtra("notifyId", counter.toString())
        intent.putExtra("date", date)
        val pending = PendingIntent.getBroadcast(context, counter.toInt(), intent, PendingIntent.FLAG_UPDATE_CURRENT)
        val calendar: Calendar = Calendar.getInstance()
        // setter alarmen til om en uke
        calendar.add(Calendar.DATE, 7)
        Toast.makeText(context, "Varsling utsatt en uke", Toast.LENGTH_LONG).show()
        val time = calendar.timeInMillis
        val manager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        manager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time, pending)
    }
    // Henter ut data fra en spesefik posisjon i databasen
    private fun getData() {

        val ref = db.collection("Notifications").document(documentID)
        ref.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                // henter verdiene som ligger under Stringene
                counter = snapshot.get("Counter") as Long
                customer = (snapshot.get("Customer")).toString()
                type = snapshot.get("Type").toString()
            }
        }
    }
}



