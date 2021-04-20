package com.example.brannvarsling.dialogFragments

import android.R
import android.app.Dialog
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentTransaction
import com.example.brannvarsling.dataClass.DialogFragmentItems
import com.example.brannvarsling.dataClass.FirebaseCases
import com.example.brannvarsling.dataClass.Test
import com.example.brannvarsling.databinding.RecyclerdialogWindowBinding
import com.example.brannvarsling.dialogFragment.AlertDateDialog
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.collections.ArrayList


class RecyclerviewDialogFragment(id: String) : DialogFragment() {

    private lateinit var binding: RecyclerdialogWindowBinding
   // private lateinit var month: String
    //private lateinit var case: Array<String>
    private var db = FirebaseFirestore.getInstance()
    private var data = FirebaseCases()
    private val documentId = id
    private var list = ArrayList<String>()
    private var customer = ""
    private var type = ""
    private var desc =""
    private var formType = ""
    private lateinit var caseChoice: ArrayList<String>
    val sakerId = db.collection("Saker").document(documentId).id


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
        spinnerForm()
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
    private fun spinnerForm() {
        db.collection("Saker").get().addOnSuccessListener { documents ->
            for (document in documents) {
                val data = document.id
                list.add(data)
                // Toast.makeText(context, "$list", Toast.LENGTH_LONG).show()
            }

            val arrayAdapter = ArrayAdapter(requireContext(), R.layout.simple_dropdown_item_1line, list)
            binding.spinnerForm.adapter = arrayAdapter
            arrayAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
            binding.spinnerForm.onItemSelectedListener = object :
                    AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    formType = parent?.getItemAtPosition(position).toString()
                    arrayAdapter.notifyDataSetChanged()
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                }
            }
        }
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
        val dialogFragment = FormDialogFragment(sakerId, formType)

        val fragmentManager = activity?.supportFragmentManager
        val transaction = fragmentManager?.beginTransaction()

        transaction?.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        transaction?.add(android.R.id.content, dialogFragment)?.addToBackStack(null)?.commit()
    }
}
