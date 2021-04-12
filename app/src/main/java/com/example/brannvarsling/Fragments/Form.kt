package com.example.brannvarsling.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.brannvarsling.CasesModel
import com.example.brannvarsling.R
import com.example.brannvarsling.databinding.FragmentFormBinding


class Form: Fragment() {
    private lateinit var binding: FragmentFormBinding
    private var skjemaList = ArrayList<CasesModel>()

    // FloatingActionBar animasjoner
    private val rotateOpen: Animation by lazy { AnimationUtils.loadAnimation(context, R.anim.rotate_open_anim) }
    private val rotateClose: Animation by lazy { AnimationUtils.loadAnimation(context, R.anim.rotate_close_anim) }
    private val fromBottom: Animation by lazy { AnimationUtils.loadAnimation(context, R.anim.from_bottom_anim) }
    private val toBottom: Animation by lazy { AnimationUtils.loadAnimation(context, R.anim.to_bottom_anim) }

    private var clicked = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?): View {

        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_form, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.floatingSpm.setOnClickListener {
            addNewSpm()
        }

        binding.floatingTitle.setOnClickListener {
            addNewTitle()
        }

        binding.buttonSubmit.setOnClickListener {

        }

        binding.floatingActionButton.setOnClickListener {
            onAddButtonClicked()
        }

        binding.buttonFjern.setOnClickListener {
            //removeViewButton()
        }
    }



    private fun onAddButtonClicked() {
        setVisibility(clicked)
        setAnimation(clicked)
        clicked = !clicked
    }

    private fun setVisibility(clicked: Boolean) {
        if(!clicked) {
            binding.floatingSpm.visibility = View.VISIBLE
            binding.floatingTitle.visibility = View.VISIBLE
        } else {
            binding.floatingSpm.visibility = View.INVISIBLE
            binding.floatingTitle.visibility = View.INVISIBLE
        }
    }

    private fun setAnimation(clicked: Boolean) {
        if (!clicked) {
            binding.floatingSpm.startAnimation(fromBottom)
            binding.floatingTitle.startAnimation(fromBottom)
            binding.floatingActionButton.startAnimation(rotateOpen)
        } else {
            binding.floatingSpm.startAnimation(toBottom)
            binding.floatingTitle.startAnimation(toBottom)
            binding.floatingActionButton.startAnimation(rotateClose)
        }
    }

    private fun addNewSpm() {
        val inflater = LayoutInflater.from(context).inflate(R.layout.row_add_spm, null)
        binding.parentLayout.addView(inflater, binding.parentLayout.childCount)
    }

    private fun addNewTitle() {
        val inflater = LayoutInflater.from(context).inflate(R.layout.row_add_titles, null)
        binding.parentLayout.addView(inflater, binding.parentLayout.childCount)
    }

}