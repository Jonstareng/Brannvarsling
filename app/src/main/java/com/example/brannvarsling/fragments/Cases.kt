package com.example.brannvarsling.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.brannvarsling.R
import com.example.brannvarsling.RecyclerviewAdapter
import com.example.brannvarsling.dataClass.FirebaseCases
import com.example.brannvarsling.databinding.FragmentCasesBinding
import com.example.brannvarsling.dialogFragments.AddDialogFragment
import com.example.brannvarsling.dialogFragments.RecyclerviewDialogFragment
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore


class Cases: Fragment(), RecyclerviewAdapter.onItemClickListner {
    private lateinit var binding: FragmentCasesBinding
    private var db = FirebaseFirestore.getInstance()
    var adapterR: RecyclerviewAdapter? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        //binding2 = DialogWindowBinding.inflate(inflater, container, false)
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_cases, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        createRecyclerView()
        binding.addCases.setOnClickListener {
            showAddDialog()
        }

    }
    private fun createRecyclerView() {
        val query = db.collection("Test")

        //Toast.makeText(context, "$", Toast.LENGTH_LONG).show()
        val option: FirestoreRecyclerOptions<FirebaseCases> = FirestoreRecyclerOptions.Builder<FirebaseCases>()
                .setQuery(query, FirebaseCases::class.java).build()


        adapterR = RecyclerviewAdapter(option, this)

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

    private fun showAddDialog() {
        val dialogFragment = AddDialogFragment()
        val fragmentManager = activity?.supportFragmentManager
        val transaction = fragmentManager?.beginTransaction()


        transaction?.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        transaction?.add(android.R.id.content, dialogFragment)?.addToBackStack(null)?.commit()
    }


    override fun onItemClick(id: String) {

        val dialogFragment = RecyclerviewDialogFragment(id)
        val fragmentManager = activity?.supportFragmentManager
        val transaction = fragmentManager?.beginTransaction()


        transaction?.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        transaction?.add(android.R.id.content, dialogFragment)?.addToBackStack(null)?.commit()
    }
}


