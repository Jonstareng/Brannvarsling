package com.example.brannvarsling.dialogFragment

import android.R
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment
import com.example.brannvarsling.databinding.DeleteFormBinding
import com.google.firebase.firestore.FirebaseFirestore

class AlertDelete : DialogFragment() {
    private lateinit var binding: DeleteFormBinding
    private var db = FirebaseFirestore.getInstance()
    private var list = ArrayList<String>()
    private var formType = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DeleteFormBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onCreateDialog(savedInstanceState)
        getDataSpinner()
        setupDialog()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        setupDialog()
        return dialog

    }
     private fun setupDialog() {
         val builder = AlertDialog.Builder(activity)
         builder
             .setCancelable(false)
             .setTitle("Velg et skjema")
             .setPositiveButton("Slett") { dialog, _ ->
                 deleteSpinner()
                 dialog.dismiss()
             }.setNegativeButton("Avbryt") { dialog, _ ->
                 dialog.dismiss()
             }

         builder.create().show()
    }

    private fun deleteSpinner() {
        val docRef = db.collection("Skjema").document(formType)
        docRef.delete()
    }

    private fun getDataSpinner(){
        db.collection("Skjema").get().addOnSuccessListener { documents ->
            for (document in documents) {
                val data = document.id
                list.add(data)
            }
            val arrayAdapter =
                ArrayAdapter(requireContext(), R.layout.simple_dropdown_item_1line, list)
            binding.spinnerDelete.adapter = arrayAdapter
            arrayAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
            binding.spinnerDelete.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    // setter formtype verdien slik at vi kan lagre verdien i databasen
                    formType = parent?.getItemAtPosition(position).toString()
                    arrayAdapter.notifyDataSetChanged()
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                }
            }
        }
    }
}