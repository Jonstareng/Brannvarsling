package com.example.brannvarsling.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.brannvarsling.R
import com.example.brannvarsling.databinding.FragmentCalenderBinding
import com.example.brannvarsling.databinding.RowAddSpmBinding

class Add_spm: Fragment() {
    private lateinit var binding: RowAddSpmBinding

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.row_add_spm, container, false)
        return binding.root
        // Inflate the layout for this fragment
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.buttonDelete.setOnClickListener{
            binding.addSpm.removeView(view)
        }
    }

}