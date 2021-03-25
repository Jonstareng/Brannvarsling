package com.example.brannvarsling.Fragments

import android.R.layout.simple_spinner_item
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.brannvarsling.R
import com.example.brannvarsling.databinding.FragmentCasesBinding
import com.google.firebase.firestore.FirebaseFirestore


class Cases: Fragment() {
    private lateinit var binding: FragmentCasesBinding
    private var firestoreDB : FirebaseFirestore? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firestoreDB = FirebaseFirestore.getInstance()

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_cases, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }


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

}
