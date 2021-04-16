package com.example.brannvarsling.dialogFragments

import android.Manifest
import android.R
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Camera
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.example.brannvarsling.databinding.DialogWindowBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.example.brannvarsling.dataClass.FirebaseCases



class AddDialogFragment: DialogFragment() {

    private lateinit var binding: DialogWindowBinding
    private var db = FirebaseFirestore.getInstance()
    private var data = FirebaseCases()
    private var CAMERA_PERMISSION_CODE = 1
    private var CAMERA_REQUEST_CODE = 2
    private var documentId = ""


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
        binding.avbryt.setOnClickListener{
            dismiss()
        }
        binding.button2.setOnClickListener{
            writeToDb()
        }
        binding.buttonVedlegg.setOnClickListener{
            dispatchTakePictureIntent()

        }
        return dialog
    }
    val REQUEST_IMAGE_CAPTURE = 1
    private fun dispatchTakePictureIntent() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent, CAMERA_REQUEST_CODE)
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_CODE
            )
        }
         fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
        ) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            if (requestCode == CAMERA_PERMISSION_CODE){
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    startActivityForResult(intent, CAMERA_REQUEST_CODE)
                }
            }
        }
        /*   val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        } catch (e: ActivityNotFoundException) {
            //
        }

      */
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

            db.collection("Test")
                    .add(user)
                    .addOnSuccessListener { documentReference -> Log.d(ContentValues.TAG, "DocumentSnapshot added with ID: " + documentReference.id) }
                    .addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error adding document", e) }
            dismiss()
        }
        else
            Toast.makeText(context, "Fyll ut alle feltene", Toast.LENGTH_LONG).show()
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
