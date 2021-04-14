package com.example.brannvarsling.dialogFragments

import android.app.Dialog
import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.brannvarsling.databinding.DialogWindowBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.example.brannvarsling.dataClass.FirebaseCases



class AddDialogFragment: DialogFragment() {

    private lateinit var binding: DialogWindowBinding
    private var db = FirebaseFirestore.getInstance()
    private var data = FirebaseCases()


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
        return dialog
    }
    private fun writeToDb() {
        val user: MutableMap<String, Any> = HashMap()
        val title = binding.textInputLayout2.editText?.text.toString()
        val type = binding.textInputLayout3.editText?.text.toString()


        if (title != "" || type != "") {

            user["Customer"] = title
            user["Type"] = type
            user["Year"] = ""
            user["Month"] = ""
            user["Day"] = ""


            db.collection("Test")
                    .add(user)
                    .addOnSuccessListener { documentReference -> Log.d(ContentValues.TAG, "DocumentSnapshot added with ID: " + documentReference.id) }
                    .addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error adding document", e) }
            dismiss()
        }
        else
            Toast.makeText(context, "Fyll ut alle feltene", Toast.LENGTH_LONG).show()
    }

}
