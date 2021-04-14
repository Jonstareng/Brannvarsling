package com.example.brannvarsling.dialogFragment

import android.app.AlertDialog
import android.app.Dialog
import android.content.ContentValues
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.brannvarsling.R
import com.example.brannvarsling.RecyclerviewAdapter
import com.example.brannvarsling.databinding.AlertdateWindowBinding
import com.google.firebase.firestore.FirebaseFirestore

class AlertDateDialog(id: String, customer: String, type: String): DialogFragment() {
    private lateinit var binding: AlertdateWindowBinding
    private lateinit var month: String
    private lateinit var case: Array<String>
    private var db = FirebaseFirestore.getInstance()
    private var year = ""
    private var day = ""
    private val documentId = id
    private val customer = customer
    private val type = type
    var adapterR: RecyclerviewAdapter? = null

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = AlertdateWindowBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        spinnerYear()
        spinnerMonth()
        spinnerDay(month)
        onCreateDialog(savedInstanceState)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        binding.settDateButton.setOnClickListener{
            saveToDB()
            adapterR?.notifyDataSetChanged()
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
        month = ""
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
        val date: MutableMap<String, Any> = HashMap()

            date["Customer"] = customer
            date["Type"] = type
            date["Year"] = "$year."
            date["Month"] = "$month."
            date["Day"] = "$day"


            db.collection("Test")
                    .document(documentId)
                    .set(date)
                    .addOnSuccessListener { Log.d(ContentValues.TAG, "DocumentSnapshot added with ID: $documentId") }
                    .addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error adding document", e) }
            dismiss()
        }
}