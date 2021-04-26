package com.example.brannvarsling.dialogFragments

import android.Manifest
import android.R
import android.app.Activity.RESULT_OK
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Camera
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.fragment.app.DialogFragment
import com.example.brannvarsling.databinding.DialogWindowBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.example.brannvarsling.dataClass.FirebaseCases
import kotlin.random.Random


class AddDialogFragment: DialogFragment() {

    private lateinit var binding: DialogWindowBinding
    private var db = FirebaseFirestore.getInstance()
    private var data = FirebaseCases()
    private var documentId = ""
    private var count = 0



    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = DialogWindowBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onCreateDialog(savedInstanceState)
    }



    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        count = Random(9999999999).nextInt()
        binding.avbryt.setOnClickListener{
            dismiss()
        }
        binding.button2.setOnClickListener{
            writeToDb()
        }

        return dialog
    }

    private fun writeToDb() {
        val user: MutableMap<String, Any> = HashMap()
        val title = binding.editTextTextPersonName.text.toString()
        val type = binding.editTextTextPersonName2.text.toString()
        val description = binding.editTextTextMultiLine.text.toString()


        if (title != "" || type != "") {

            user["Customer"] = title
            user["Type"] = type
            user["Description"] = description
            user["NotificationID"] = count.toString()

            db.collection("Test")
                    .add(user)
                    .addOnSuccessListener { documentReference -> Log.d(ContentValues.TAG, "DocumentSnapshot added with ID: " + documentReference.id) }
                    .addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error adding document", e) }
            count = Random(9999999999).nextInt()
            dismiss()
        }
        else {
            Toast.makeText(context, "Fyll ut alle feltene", Toast.LENGTH_LONG).show()
        }

    }
    private fun spinner() {
        val caseChoice = arrayOf("2021", "2022", "2023", "2024", "2025")
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.simple_dropdown_item_1line, caseChoice)
        binding.spinner.adapter = arrayAdapter
        binding.spinner.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                documentId = parent?.getItemAtPosition(position).toString()
                arrayAdapter.notifyDataSetChanged()

            }
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
    }

}
