package com.example.brannvarsling.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.brannvarsling.R
import com.example.brannvarsling.databinding.FragmentFormBinding


class Form: Fragment() {
    private lateinit var binding: FragmentFormBinding

    // FloatingActionBar animasjoner
    private val rotateOpen: Animation by lazy { AnimationUtils.loadAnimation(context, R.anim.rotate_open_anim) }
    private val rotateClose: Animation by lazy { AnimationUtils.loadAnimation(context, R.anim.rotate_close_anim) }
    private val fromBottom: Animation by lazy { AnimationUtils.loadAnimation(context, R.anim.from_bottom_anim) }
    private val toBottom: Animation by lazy { AnimationUtils.loadAnimation(context, R.anim.to_bottom_anim) }

    private var clicked = false

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_form, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.floatingEdit.setOnClickListener {
            Toast.makeText(context, "Edit clicked", Toast.LENGTH_SHORT).show()
        }

        binding.floatingImg.setOnClickListener {
            Toast.makeText(context, "Img clicked", Toast.LENGTH_SHORT).show()
        }

        binding.floatingActionButton.setOnClickListener {
            onAddButtonClicked()
        }
    }

    private fun onAddButtonClicked() {
        setVisibility(clicked)
        setAnimation(clicked)
        clicked = !clicked
    }

    private fun setVisibility(clicked: Boolean) {
        if(!clicked) {
            binding.floatingEdit.visibility = View.VISIBLE
            binding.floatingImg.visibility = View.VISIBLE
        } else {
            binding.floatingEdit.visibility = View.INVISIBLE
            binding.floatingImg.visibility = View.INVISIBLE
        }
    }

    private fun setAnimation(clicked: Boolean) {
        if (!clicked) {
            binding.floatingEdit.startAnimation(fromBottom)
            binding.floatingImg.startAnimation(fromBottom)
            binding.floatingActionButton.startAnimation(rotateOpen)
        } else {
            binding.floatingEdit.startAnimation(toBottom)
            binding.floatingImg.startAnimation(toBottom)
            binding.floatingActionButton.startAnimation(rotateClose)
        }
    }
}