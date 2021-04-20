package com.example.brannvarsling.dialogFragment

import android.app.*
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import com.example.brannvarsling.BroadcastReceiver
import com.example.brannvarsling.R
import com.example.brannvarsling.dataClass.FirebaseCases
import com.example.brannvarsling.databinding.AlertdateWindowBinding
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*
import kotlin.collections.HashMap


class AlertDateDialog(id: String, customer: String, type: String, desc: String): DialogFragment() {
    private lateinit var binding: AlertdateWindowBinding
    private var month = ""
    private lateinit var case: Array<String>
    private lateinit var pendingIntent: PendingIntent
    private var db = FirebaseFirestore.getInstance()
    private var year = ""
    private var day = ""
    private val documentId = id
    private val customer = customer
    private val type = type
    private var desc = desc
    private var count = ""
    private val channelID = "Cases ID"


    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = AlertdateWindowBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        spinnerYear()
        spinnerMonth()
        spinnerDay(month)
        getData()
        createNotificationChannel()
        onCreateDialog(savedInstanceState)
    }


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        getData()
        binding.settDateButton.setOnClickListener{
            saveToDB()
            cancelNotification()
            scheduleNotification()
            dismiss()
        }
        return dialog
    }
    private fun spinnerYear() {
        val caseChoice = arrayOf("2021", "2022", "2023", "2024", "2025")
        val arrayAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, caseChoice)
        binding.spinnerYear.adapter = arrayAdapter
        binding.spinnerYear.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                year = parent?.getItemAtPosition(position).toString()
                arrayAdapter.notifyDataSetChanged()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
    }
    private fun spinnerMonth() {
        val caseChoice = arrayOf("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12")
        val arrayAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, caseChoice)
        binding.spinnerMonth.adapter = arrayAdapter
        binding.spinnerMonth.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                month = parent?.getItemAtPosition(position).toString()
                spinnerDay(month)

            }
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
    }
    private fun spinnerDay(month: String) {
        case = arrayOf("")
        case = if (month == "02"){
            arrayOf("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13",
                    "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28")
        }
        else if(month == "04" || month == "06" || month == "09" || month == "11") {
            arrayOf("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13",
                    "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30")
        }
        else arrayOf("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13",
                "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31")
        val arrayAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, case)
        binding.spinnerDay.adapter = arrayAdapter
        binding.spinnerDay.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                day = parent?.getItemAtPosition(position).toString()
                arrayAdapter.notifyDataSetChanged()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
    }
    private fun saveToDB() {
        val data: MutableMap<String, Any> = HashMap()
        val date = "$year$month$day"


        data["Customer"] = customer
        data["Type"] = type
        data["Date"] = date
        data["Description"] = desc
        data["NotificationID"] = count



            db.collection("Test")
                    .document(documentId)
                    .set(data)
                    .addOnSuccessListener { Log.d(ContentValues.TAG, "DocumentSnapshot added with ID: $documentId") }
                    .addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error adding document", e) }
            dismiss()
        }
    private fun getData() {
        val docRef = db.collection("Test").document(documentId)

        docRef.get().addOnSuccessListener { documentSnapshot ->
            val data = documentSnapshot.toObject(FirebaseCases::class.java)
            count = data?.NotificationID.toString()

        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun scheduleNotification(){
        var id = count.toInt()
        val intent = Intent(context, BroadcastReceiver::class.java)
        intent.putExtra("title", customer)
        intent.putExtra("text", type)
        val pending = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        // Schdedule notification
        val calendar: Calendar = Calendar.getInstance()
        calendar.set(year.toInt(),month.toInt() - 1,day.toInt(),13,0, 0)
        val time = calendar.timeInMillis
        Toast.makeText(context, "Varsling satt $year.$month.$day", Toast.LENGTH_LONG).show()
        val manager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time, pending)
    }
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.app_name)
            val descriptionText = getString(R.string.app_name)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                    activity?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun cancelNotification() {
        var count = 0
        val intent = Intent(context, BroadcastReceiver::class.java)
        intent.putExtra("title", customer)
        intent.putExtra("text", type)
        val pending = PendingIntent.getBroadcast(context, count, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        // Cancel notification
        val manager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        manager.cancel(pending)
        count++
    }
}