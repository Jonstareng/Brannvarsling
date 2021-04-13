package com.example.brannvarsling.dialogFragments

import android.app.Dialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentValues.TAG
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
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.DialogFragment
import com.example.brannvarsling.MainActivity
import com.example.brannvarsling.R
import com.example.brannvarsling.dataClass.FirebaseCases
import com.example.brannvarsling.databinding.RecyclerdialogWindowBinding
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*


class RecyclerviewDialogFragment(id: String) : DialogFragment() {

    private lateinit var binding: RecyclerdialogWindowBinding
    private lateinit var currentDateAndTime: String
    private lateinit var month: String
    private lateinit var case: Array<String>
    private var db = FirebaseFirestore.getInstance()
    private var data = FirebaseCases()
    private val documentId = id
    private var year = ""
    private var day = ""
    private val hour = " 15:50"
    private var notificationDate = ""
    private val channelID = "Cases ID"
    private val notificationId = 101


    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = RecyclerdialogWindowBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onCreateDialog(savedInstanceState)
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        getData()
        spinnerYear()
        spinnerMonth()
        spinnerDay(month)
        createNotificationChannel()
        getCurrentDate()
        if (currentDateAndTime == notificationDate) {
            sendNotification()
        }
        Toast.makeText(requireContext(), notificationDate, Toast.LENGTH_LONG).show()
        binding.close.setOnClickListener{
            dismiss()
        }
        binding.deleteRecyclerItem.setOnClickListener{
            deleteItem()
            dismiss()
        }
        binding.saveDate.setOnClickListener{
            setNotificationDate()
        }
        return dialog
    }

    private fun getData() {
        val docRef = db.collection("Test").document(documentId)

        docRef.get().addOnSuccessListener { documentSnapshot ->
            val data = documentSnapshot.toObject(FirebaseCases::class.java)
            binding.displayCustomer.text = data?.Customer
            binding.displayType.text = data?.Type
        }
    }
    private fun deleteItem(){
        val docRef = db.collection("Test").document(documentId)

        docRef.delete()
                .addOnSuccessListener{ Log.d(TAG, "DocumentSnapshot successfully deleted!")}
                .addOnSuccessListener{ e->Log.w(TAG, "Error deleting document")}
        Toast.makeText(requireContext(), "Sak nr " + documentId + " slettet", Toast.LENGTH_SHORT).show()

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
        month = ""
        val caseChoice = arrayOf("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12")
        val arrayAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, caseChoice)
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
    private fun createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val notificationName = "Case notification"
            val descriptionText = "Notification description"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel: NotificationChannel = NotificationChannel(channelID, notificationName, importance).apply {
                description= descriptionText
            }
            val notificationManager: NotificationManager = (requireActivity().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)!!
            notificationManager.createNotificationChannel(channel)
        }

    }
    private fun sendNotification(){
            val intent = Intent(requireContext(), MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            val pendingIntent: PendingIntent = PendingIntent.getActivity(requireContext(), 0, intent, 0)
            val builder = NotificationCompat.Builder(requireContext(), channelID)
                    .setSmallIcon(R.drawable.assignment_24px)
                    .setContentTitle("Kontroll for kunde 1")
                    .setContentText("kontroll må gjennomføres innen 1 uke")
                    .setContentIntent(pendingIntent)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)

            with(NotificationManagerCompat.from(requireContext())) {
                notify(notificationId, builder.build())
        }
    }
    private fun getCurrentDate(){
        val simpleDateFormat = SimpleDateFormat("yyyy.MM.dd", Locale.ENGLISH)
        currentDateAndTime = simpleDateFormat.format(Date())
    }
    private fun setNotificationDate(){
        notificationDate = year + "." + month + "." + day
        Toast.makeText(requireContext(), currentDateAndTime, Toast.LENGTH_LONG).show()
        Toast.makeText(requireContext(), notificationDate, Toast.LENGTH_LONG).show()


    }
}
