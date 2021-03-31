package com.example.brannvarsling.dialogFragments

import android.app.Dialog
import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import com.example.brannvarsling.databinding.DialogWindowBinding
import com.google.firebase.firestore.FirebaseFirestore


class AddDialogFragment: DialogFragment() {

    private lateinit var binding: DialogWindowBinding
    private var db = FirebaseFirestore.getInstance()


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
            dismiss()
        }
        return dialog
    }
    private fun writeToDb() {
        //val costumer = binding.Costumer.text.toString()
        //val type = binding.Type.text.toString()

        val user: MutableMap<String, Any> = HashMap()
        user["Customer"] = "customer"
        user["Type"] = "type"


// Add a new document with a generated ID
        db.collection("Test")
                .add(user)
                .addOnSuccessListener { documentReference -> Log.d(ContentValues.TAG, "DocumentSnapshot added with ID: " + documentReference.id) }
                .addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error adding document", e) }
    }



}
