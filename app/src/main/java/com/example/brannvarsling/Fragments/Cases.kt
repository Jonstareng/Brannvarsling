package com.example.brannvarsling.Fragments

import android.R.layout.simple_spinner_item
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.brannvarsling.R
import com.example.brannvarsling.databinding.FragmentCasesBinding

class Cases: Fragment() {
    private lateinit var binding: FragmentCasesBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
       /* val caseChoice = arrayOf("Brannvarsling","Nødlys")
        val arrayAdapter = context?.let { ArrayAdapter(it, simple_spinner_item, caseChoice) }
        spinner.adapter = arrayAdapter
        spinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            when(position){
            }
        }

    }
*/
}