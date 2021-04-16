package com.example.brannvarsling.dialogFragments

import android.app.Dialog
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.brannvarsling.databinding.DialogWindowBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.example.brannvarsling.dataClass.FirebaseCases
import com.example.brannvarsling.databinding.RecyclerdialogWindowBinding


class RecyclerviewDialogFragment(id: String) : DialogFragment() {

    private lateinit var binding: RecyclerdialogWindowBinding
    private var db = FirebaseFirestore.getInstance()
    private var data = FirebaseCases()
    private val documentId = id


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
        binding.close.setOnClickListener{
            dismiss()
        }
        binding.deleteRecyclerItem.setOnClickListener{
            deleteItem()
            dismiss()
        }
        return dialog
    }

    private fun getData() {
        val docRef = db.collection("Test").document(documentId)

        docRef.get().addOnSuccessListener { documentSnapshot ->
            val data = documentSnapshot.toObject(FirebaseCases::class.java)
            binding.displayCustomer.text = data?.Customer
            binding.displayType.text = data?.Type
            binding.displayDescription.text =data?.Description
        }
    }
    private fun deleteItem(){
        val docRef = db.collection("Test").document(documentId)

        docRef.delete()
                .addOnSuccessListener{ Log.d(TAG,"DocumentSnapshot successfully deleted!")}
                .addOnSuccessListener{e->Log.w(TAG,"Error deleting document")}
        Toast.makeText(requireContext(), "Sak nr " + documentId + " slettet", Toast.LENGTH_SHORT).show()

    }
}
