package com.example.brannvarsling.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.brannvarsling.R
import com.example.brannvarsling.databinding.FragmentFormBinding

class Form: Fragment() {
    private lateinit var binding: FragmentFormBinding

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_form, container, false)
        return binding.root
    }
}