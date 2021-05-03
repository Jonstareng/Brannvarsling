package com.example.brannvarsling.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.brannvarsling.R
import com.example.brannvarsling.adapters.RecyclerviewAdapter
import com.example.brannvarsling.dataClass.FirebaseCases
import com.example.brannvarsling.databinding.FragmentCasesBinding
import com.example.brannvarsling.dialogFragments.AddDialogFragment
import com.example.brannvarsling.dialogFragment.RecyclerviewDialogFragment
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso


class Cases: Fragment(), RecyclerviewAdapter.OnItemClickListener {
    private lateinit var binding: FragmentCasesBinding
    private var db = FirebaseFirestore.getInstance()
    private var adapterR: RecyclerviewAdapter? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_cases, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        createRecyclerView()
        binding.addCases.setOnClickListener {
            showAddDialog()
        }
        binding.recyclerLayout

    }

    // kobler opp recyclerviewet med adapteret
    private fun createRecyclerView() {
        val query = db.collection("Saker")

        val option: FirestoreRecyclerOptions<FirebaseCases> = FirestoreRecyclerOptions.Builder<FirebaseCases>()
                .setQuery(query, FirebaseCases::class.java).build()

        adapterR = RecyclerviewAdapter(option, this )

        binding.recyclerviewCase.layoutManager = LinearLayoutManager(context)
        binding.recyclerviewCase.adapter = adapterR

    }
    // starter adapteret
    override fun onStart() {
        super.onStart()
        adapterR?.startListening()
    }
    // Slutter adapteret
    override fun onStop() {
        super.onStop()
        adapterR?.stopListening()
    }

    // åpner opp adddialog vinduet som er en egen klasse
    private fun showAddDialog() {
        val dialogFragment = AddDialogFragment()
        val fragmentManager = activity?.supportFragmentManager
        val transaction = fragmentManager?.beginTransaction()


        transaction?.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        transaction?.add(android.R.id.content, dialogFragment)?.addToBackStack(null)?.commit()
    }

    // sender deg inn i recyclerviewdialog vinduet, sender med id på dokumentet slik at riktig data blir vist i vinduet
    override fun onItemClick(id: String, customer: CharSequence) {
        val dialogFragment = RecyclerviewDialogFragment(id, customer)
        val fragmentManager = activity?.supportFragmentManager
        val transaction = fragmentManager?.beginTransaction()


        transaction?.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        transaction?.add(android.R.id.content, dialogFragment)?.addToBackStack(null)?.commit()
    }

}


