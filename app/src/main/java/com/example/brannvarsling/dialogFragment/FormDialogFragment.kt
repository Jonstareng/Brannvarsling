package com.example.brannvarsling.dialogFragments

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.brannvarsling.R
import com.example.brannvarsling.databinding.FormdialogWindowBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.example.brannvarsling.dataClass.SkjemaFirebase
import com.google.firebase.firestore.DocumentReference


class FormDialogFragment(sakerId: String) : DialogFragment() {

    private lateinit var binding: FormdialogWindowBinding
    private var db = FirebaseFirestore.getInstance()
    private var list = ArrayList<SkjemaFirebase>()
    private var documentId = sakerId


    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = FormdialogWindowBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onCreateDialog(savedInstanceState)
    }



    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        binding.savePdf.setOnClickListener{
            dismiss()
        }
        getFormData()

        return dialog
    }
    private fun addNewSpm() {
        val inflater = LayoutInflater.from(requireContext()).inflate(R.layout.row_add_spm, null)
        binding.formLayout.addView(inflater, binding.formLayout.childCount)
    }

    private fun getFormData(){

        val docRef = db.collection("Saker").document("Brannsystem")
        Toast.makeText(requireContext(), "$documentId", Toast.LENGTH_LONG).show()


        docRef.get().addOnSuccessListener { documentSnapshot ->
            val data = documentSnapshot.toObject(SkjemaFirebase::class.java)
            binding.adresseText.text = data?.Adresse
            binding.anleggText.text = data?.Anlegg
            binding.kundeText.text = data?.Kunde
            binding.overforingText.text = data?.Overforing
            binding.tittelText.text = data?.Tittel



        }
    }

}
