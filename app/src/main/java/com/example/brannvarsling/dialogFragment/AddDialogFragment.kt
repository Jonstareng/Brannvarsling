package com.example.brannvarsling.dialogFragments

import android.R
import android.app.Activity.RESULT_OK
import android.app.Dialog
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.brannvarsling.databinding.DialogWindowBinding
import com.google.firebase.firestore.FirebaseFirestore


class AddDialogFragment: DialogFragment() {

    private lateinit var binding: DialogWindowBinding
    private lateinit var imageView: ImageView
    private val pickImage = 100
    private var imageUri: Uri? = null
    private var db = FirebaseFirestore.getInstance()
    private var counter = ""
    private var formType = ""
    private var list = ArrayList<String>()


    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
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
        getNotifyCounter()
        spinnerForm()
        binding.avbryt.setOnClickListener{
            dismiss()
        }
        binding.button2.setOnClickListener{
            writeToDb()
            notificationCounter()
        }
        return dialog
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == pickImage) {
            imageUri = data?.data
            imageView.setImageURI(imageUri)
                }
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
            user["NotificationID"] = counter
            user["Form"] = formType
            user["Date"] = ""

            db.collection("Saker")
                    .add(user)
                    .addOnSuccessListener { documentReference -> Log.d(ContentValues.TAG, "DocumentSnapshot added with ID: " + documentReference.id) }
                    .addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error adding document", e) }
            dismiss()
        }
        else {
            Toast.makeText(context, "Fyll ut alle feltene", Toast.LENGTH_LONG).show()
        }

    }

    private fun notificationCounter() {
        val number: MutableMap<String, Any> = HashMap()
        val newCounter: Int = counter.toInt() + 1
        newCounter.toLong()
        number["Counter"] = newCounter
        db.collection("NotificationIds").document("qsK39UawP1XXeoTCrPcn").set(number)

    }
    private fun getNotifyCounter() {

        val ref = db.collection("NotificationIds").document("qsK39UawP1XXeoTCrPcn")

        ref.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                counter = snapshot.get("Counter").toString()
            }
        }
    }
    private fun spinnerForm() {
        db.collection("Skjema").get().addOnSuccessListener { documents ->
            for (document in documents) {
                val data = document.id
                list.add(data)
                // Toast.makeText(context, "$list", Toast.LENGTH_LONG).show()
            }

            val arrayAdapter =
                    ArrayAdapter(requireContext(), R.layout.simple_dropdown_item_1line, list)
            binding.spinner.adapter = arrayAdapter
            arrayAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
            binding.spinner.onItemSelectedListener = object :
                    AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                ) {
                    formType = parent?.getItemAtPosition(position).toString()
                    arrayAdapter.notifyDataSetChanged()
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                }
            }
        }
    }

}
