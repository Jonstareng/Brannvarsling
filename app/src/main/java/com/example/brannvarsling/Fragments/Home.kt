package com.example.brannvarsling.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.example.brannvarsling.R
import com.example.brannvarsling.databinding.ActivityMainBinding
import com.example.brannvarsling.databinding.FragmentHomeBinding

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class Home : Fragment() {
    private lateinit var binding: FragmentHomeBinding


    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonCases.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_cases)
        }
        binding.buttonCalendar.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_calendar)
        }
    }

}