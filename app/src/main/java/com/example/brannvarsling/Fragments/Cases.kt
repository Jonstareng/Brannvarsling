package com.example.brannvarsling.Fragments

import android.R.layout.simple_spinner_item
import android.app.AlertDialog
import android.app.Dialog
import android.content.ContentValues.TAG
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.databinding.DataBindingUtil.setContentView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.brannvarsling.CasesModel
import com.example.brannvarsling.R
import com.example.brannvarsling.RecyclerviewAdapter
import com.example.brannvarsling.dataClass.FirebaseCases
import com.example.brannvarsling.databinding.DialogWindowBinding
import com.example.brannvarsling.databinding.FragmentCasesBinding
import com.example.brannvarsling.dialogFragments.AddDialogFragment
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.NonCancellable.cancel


class Cases: Fragment() {
    private lateinit var binding: FragmentCasesBinding
    //private lateinit var binding2: DialogWindowBinding
    //private val bind get() = binding2!!

    private var db = FirebaseFirestore.getInstance()
    var adapterR: RecyclerviewAdapter? = null
    private var dialogFragment = AddDialogFragment()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        //binding2 = DialogWindowBinding.inflate(inflater, container, false)
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_cases, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        createRecyclerView()
        //spinnerMenu()
        binding.addCases.setOnClickListener {
            showDialog()
        }

    }

    /*
                private fun spinnerMenu() {
                    val caseChoice = arrayOf("Brannvarsling", "Nødlys")
                    val arrayAdapter = context?.let { ArrayAdapter(it, simple_spinner_item, caseChoice) }
                    binding.spinner.adapter = arrayAdapter
                    binding.spinner.onItemSelectedListener = object :
                            AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                                val text: String = parent?.getItemAtPosition(position).toString()
                }
                        override fun onNothingSelected(parent: AdapterView<*>?) {

                        }
                    }
        }
     */
    // Firestore recycleradapter
    private fun createRecyclerView() {
        val query = db.collection("Test")

        //Toast.makeText(context, "$", Toast.LENGTH_LONG).show()
        val option: FirestoreRecyclerOptions<FirebaseCases> = FirestoreRecyclerOptions.Builder<FirebaseCases>()
                .setQuery(query, FirebaseCases::class.java).build()


        adapterR = RecyclerviewAdapter(option)

        binding.recyclerviewCase.layoutManager = LinearLayoutManager(context)
        binding.recyclerviewCase.adapter = adapterR


    }

    override fun onStart() {
        super.onStart()
        adapterR?.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapterR?.stopListening()
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
                .addOnSuccessListener { documentReference -> Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.id) }
                .addOnFailureListener { e -> Log.w(TAG, "Error adding document", e) }
    }

    private fun showDialog() {
        val fragmentManager = activity?.supportFragmentManager
        val transaction = fragmentManager?.beginTransaction()

        transaction?.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        transaction?.add(android.R.id.content, dialogFragment)?.addToBackStack(null)?.commit()
    }
}


