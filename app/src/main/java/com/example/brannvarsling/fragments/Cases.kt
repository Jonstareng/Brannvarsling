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
        //binding2 = DialogWindowBinding.inflate(inflater, container, false)
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_cases, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        createRecyclerView()
        getImage()
        binding.addCases.setOnClickListener {
            showAddDialog()
        }
        binding.recyclerLayout

    }
    private fun createRecyclerView() {
        val query = db.collection("Saker")

        //Toast.makeText(context, "$", Toast.LENGTH_LONG).show()
        val option: FirestoreRecyclerOptions<FirebaseCases> = FirestoreRecyclerOptions.Builder<FirebaseCases>()
                .setQuery(query, FirebaseCases::class.java).build()

        adapterR = RecyclerviewAdapter(option, this )

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


    override fun onItemClick(id: String, customer: CharSequence) {
        val dialogFragment = RecyclerviewDialogFragment(id, customer)
        val fragmentManager = activity?.supportFragmentManager
        val transaction = fragmentManager?.beginTransaction()


        transaction?.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        transaction?.add(android.R.id.content, dialogFragment)?.addToBackStack(null)?.commit()
    }
    private fun getImage() {

        /*
        val storageReference = FirebaseStorage.getInstance()
        val refStorage = storageReference.getReferenceFromUrl("gs://varslingssystem.appspot.com/baseline_check_box_black_24dp.png")

        refStorage.downloadUrl.addOnSuccessListener { image ->
            val url = binding.recyclerLayout.findViewById<ImageView>(R.id.recyclerview_item_checkbox)
            Picasso.get().load(image).into(url)
            }
         */

    }

}


