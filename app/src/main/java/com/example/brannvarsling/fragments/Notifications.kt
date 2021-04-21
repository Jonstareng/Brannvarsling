package com.example.brannvarsling.fragments

import android.app.AlertDialog
import android.content.ContentValues
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.brannvarsling.NotificationAdapter
import com.example.brannvarsling.R
import com.example.brannvarsling.RecyclerviewAdapter
import com.example.brannvarsling.SwipeToDeleteCallback
import com.example.brannvarsling.dataClass.FirebaseCases
import com.example.brannvarsling.dataClass.FirebaseNotification
import com.example.brannvarsling.databinding.FragmentNotificationBinding
import com.example.brannvarsling.dialogFragments.RecyclerviewDialogFragment
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore


class Notifications: Fragment(), NotificationAdapter.OnItemClickListnerN {
    private lateinit var binding: FragmentNotificationBinding
    private var db = FirebaseFirestore.getInstance()
    var adapterR: NotificationAdapter? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_notification, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        createRecyclerView()

    }
    private fun createRecyclerView() {
        val query = db.collection("Notifications")

        //Toast.makeText(context, "$", Toast.LENGTH_LONG).show()
        val option: FirestoreRecyclerOptions<FirebaseNotification> = FirestoreRecyclerOptions.Builder<FirebaseNotification>()
            .setQuery(query, FirebaseNotification::class.java).build()

        adapterR = NotificationAdapter(option, this)

        binding.recyclerviewNotify.layoutManager = LinearLayoutManager(context)
        binding.recyclerviewNotify.adapter = adapterR

    }

    override fun onStart() {
        super.onStart()
        adapterR?.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapterR?.stopListening()
    }

    override fun onItemClick(id: String) {
        val documentId = id
        swipeLeft(id)
        val builder = AlertDialog.Builder(activity)
        builder.setMessage("Notification")
            .setCancelable(false)
            .setPositiveButton("Slett", DialogInterface.OnClickListener { dialog, id ->
                val docRef = db.collection("Notifications").document(documentId)
                docRef.delete()
                dialog.dismiss()
            }).setNegativeButton("Avbryt", DialogInterface.OnClickListener { dialog, id ->
                dialog.dismiss()
            }).setNeutralButton("Utsett en dag", DialogInterface.OnClickListener{dialog, id ->
                dialog.dismiss()
            }).setNeutralButton("Utsett en Uke", DialogInterface.OnClickListener{dialog, id ->
                dialog.dismiss()
            })
        val alert = builder.create()
        alert.show()
    }
    private fun swipeLeft(documentID: String){
        val swipeToDeleteCallBack = object : SwipeToDeleteCallback(){
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val docRef = db.collection("Notifications").document(documentID)
                docRef.delete()
            }
        }
        val itemHelper = ItemTouchHelper(swipeToDeleteCallBack)
        itemHelper.attachToRecyclerView(binding.recyclerviewNotify)
    }
}


