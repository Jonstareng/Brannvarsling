package com.example.brannvarsling.dialogFragments

import android.R
import android.app.AlertDialog
import android.app.Dialog
import android.content.ContentValues.TAG
import android.content.DialogInterface
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
import com.example.brannvarsling.databinding.RecyclerdialogWindowBinding
import com.example.brannvarsling.dialogFragment.AlertDateDialog
import com.example.brannvarsling.dialogFragment.FormDialogFragment
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.collections.ArrayList


class RecyclerviewDialogFragment(id: String) : DialogFragment() {

    private lateinit var binding: RecyclerdialogWindowBinding

    // private lateinit var month: String
    //private lateinit var case: Array<String>
    private var db = FirebaseFirestore.getInstance()
    private val documentId = id
    private var formOpen = ""
    private var counter: Long = 0
    private var customer = ""
    private var type = ""
    private var desc = ""
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
        getData()
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        getNotifyCounter()
        binding.close.setOnClickListener {
            dismiss()
        }
        binding.deleteRecyclerItem.setOnClickListener {
            slettDialog()
        }
        binding.saveDate.setOnClickListener {
            alertDialog()
        }
        binding.openForm.setOnClickListener {
            openForm()
        }
        return dialog
    }

    private fun getData() {
        val docRef = db.collection("Saker").document(documentId)

        docRef.get().addOnSuccessListener { documentSnapshot ->
            val data = documentSnapshot.toObject(DialogFragmentItems::class.java)
            binding.displayCustomer.text = data?.Customer
            binding.displayType.text = data?.Type
            binding.displayDate.text = data?.Date
            binding.displayDescription.text = data?.Description
            customer = data?.Customer.toString()
            type = data?.Type.toString()
            desc = data?.Description.toString()

        }
        docRef.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                formOpen = snapshot.get("Form").toString()
            }
        }
    }

    private fun deleteItem() {
        val docRef = db.collection("Saker").document(documentId)

        docRef.delete()
            .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully deleted!") }
            .addOnSuccessListener { e -> Log.w(TAG, "Error deleting document") }
        Toast.makeText(requireContext(), "Sak $customer slettet", Toast.LENGTH_SHORT).show()
    }


    private fun slettDialog() {
        val builder = AlertDialog.Builder(activity)
        builder.setMessage("Er du sikker på at du vil slette $customer")
            .setCancelable(false)
            .setPositiveButton("Slett", DialogInterface.OnClickListener { dialog, id ->
                deleteItem()
                dismiss()
            }).setNegativeButton("Avbryt", DialogInterface.OnClickListener { dialog, id ->
                dialog.dismiss()
            })
        val alert = builder.create()
        alert.show()
    }

    private fun alertDialog() {
        val builder = AlertDateDialog(documentId, customer, type, desc, formOpen)

        val fragmentManager = activity?.supportFragmentManager
        val transaction = fragmentManager?.beginTransaction()

        transaction?.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        transaction?.add(android.R.id.content, builder)?.addToBackStack(null)?.commit()

    }

    private fun openForm() {
        val dialogFragment = FormDialogFragment(sakerId, formOpen)

        val fragmentManager = activity?.supportFragmentManager
        val transaction = fragmentManager?.beginTransaction()

        transaction?.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        transaction?.add(android.R.id.content, dialogFragment)?.addToBackStack(null)?.commit()
    }

    private fun getNotifyCounter() {

        val ref = db.collection("NotificationIds").document("qsK39UawP1XXeoTCrPcn")

        ref.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                counter = snapshot.get("Counter") as Long
            }
        }
    }
}
