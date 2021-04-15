package com.example.brannvarsling.dialogFragments

import android.app.Dialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentTransaction
import com.example.brannvarsling.MainActivity
import com.example.brannvarsling.R
import com.example.brannvarsling.dataClass.DialogFragmentItems
import com.example.brannvarsling.dataClass.FirebaseCases
import com.example.brannvarsling.databinding.RecyclerdialogWindowBinding
import com.example.brannvarsling.dialogFragment.AlertDateDialog
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*


class RecyclerviewDialogFragment(id: String) : DialogFragment() {

    private lateinit var binding: RecyclerdialogWindowBinding
    private lateinit var currentDateAndTime: String
   // private lateinit var month: String
    //private lateinit var case: Array<String>
    private var db = FirebaseFirestore.getInstance()
    private var data = FirebaseCases()
    private val documentId = id
    private var year = ""
    private var day = ""
    private var notificationDate = ""
    private val channelID = "Cases ID"
    private val notificationId = 101
    private var customer = ""
    private var type = ""


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
            alertDialog(documentId, customer, type)
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
        }
    }
    private fun deleteItem(){
        val docRef = db.collection("Test").document(documentId)

        docRef.delete()
                .addOnSuccessListener{ Log.d(TAG, "DocumentSnapshot successfully deleted!")}
                .addOnSuccessListener{ e->Log.w(TAG, "Error deleting document")}
        Toast.makeText(requireContext(), "Sak $customer slettet", Toast.LENGTH_SHORT).show()
    }

    private fun alertDialog(id: String, c: String, t: String) {
        val dialogFragment = AlertDateDialog(documentId, customer, type)
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
}
