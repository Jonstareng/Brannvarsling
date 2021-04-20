package com.example.brannvarsling.dialogFragment

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
import androidx.fragment.app.FragmentTransaction
import com.example.brannvarsling.dataClass.DialogFragmentItems
import com.example.brannvarsling.dataClass.FirebaseCases
import com.example.brannvarsling.dataClass.Test
import com.example.brannvarsling.databinding.RecyclerdialogWindowBinding
import com.google.firebase.firestore.FirebaseFirestore


class RecyclerviewDialogFragment(id: String) : DialogFragment() {

    private lateinit var binding: RecyclerdialogWindowBinding
    private lateinit var currentDateAndTime: String
   // private lateinit var month: String
    //private lateinit var case: Array<String>
    private var db = FirebaseFirestore.getInstance()
    private var data = FirebaseCases()
    private val documentId = id
    private var list = ArrayList<Test>()
    private var customer = ""
    private var type = ""
    private var desc =""
    private val sakerId = db.collection("Saker").document(documentId).id


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
        binding.saveDate.setOnClickListener {
            alertDialog()
        }
        binding.openForm.setOnClickListener{
            openForm()
        }
        return dialog
    }

    private fun getData() {
        val docRef = db.collection("Test").document(documentId)

        docRef.get().addOnSuccessListener { documentSnapshot ->
            val data = documentSnapshot.toObject(DialogFragmentItems::class.java)
            binding.displayCustomer.text = data?.Customer
            binding.displayType.text = data?.Type
            binding.displayDate.text = data?.date
            customer = data?.Customer.toString()
            type = data?.Type.toString()
            desc = data?.Description.toString()
            binding.displayDescription.text =data?.Description
        }
    }
    private fun deleteItem(){
        val docRef = db.collection("Test").document(documentId)

        docRef.delete()
                .addOnSuccessListener{ Log.d(TAG, "DocumentSnapshot successfully deleted!")}
                .addOnSuccessListener{ e->Log.w(TAG, "Error deleting document")}
        Toast.makeText(requireContext(), "Sak $customer slettet", Toast.LENGTH_SHORT).show()
    }

    private fun alertDialog() {
        val dialogFragment = AlertDateDialog(documentId, customer, type, desc)
        /*val manager = activity?.supportFragmentManager
        if (manager != null) {
            dialogFragment.show(manager, "Varslings dato")
        }

         */
        val fragmentManager = activity?.supportFragmentManager
        val transaction = fragmentManager?.beginTransaction()

        transaction?.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        transaction?.add(android.R.id.content, dialogFragment)?.addToBackStack(null)?.commit()
    }
    private fun openForm(){
        val dialogFragment = FormDialogFragment(sakerId)

        val fragmentManager = activity?.supportFragmentManager
        val transaction = fragmentManager?.beginTransaction()

        transaction?.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        transaction?.add(android.R.id.content, dialogFragment)?.addToBackStack(null)?.commit()
    }
}
