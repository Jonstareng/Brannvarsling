package com.example.brannvarsling.fragments

import android.annotation.SuppressLint
import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.view.children
import androidx.core.view.get
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.brannvarsling.R
import com.example.brannvarsling.R.layout.row_add_titles
import com.example.brannvarsling.dataClass.SkjemaFirebase
import com.example.brannvarsling.dataClass.Test
import com.example.brannvarsling.databinding.FragmentFormBinding
import com.google.firebase.firestore.FirebaseFirestore


class Form: Fragment() {
    private lateinit var binding: FragmentFormBinding
    private var db = FirebaseFirestore.getInstance()
    private var list = ArrayList<Test>()
    private var listTitle = ArrayList<SkjemaFirebase>()
    //private var skjemaList = ArrayList<CasesModel>()

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

        // Knapp som legger til nye spørsmål i et skjema
        binding.floatingSpm.setOnClickListener {
            addNewSpm()
        }

        // Knapp som legger til tittel med nødvendige untertitler
        binding.floatingTitle.setOnClickListener {
            addNewTitle()
        }

        // Lagre knapp. (Skal kunne lagre skjema som pdf, og i arraylist for å laste opp til firebase)
        binding.buttonSubmit.setOnClickListener {
            saveData()
        }

        // Bare en knapp for floatingbutton
        binding.floatingActionButton.setOnClickListener {
            onAddButtonClicked()
        }

        binding.buttonFjern.setOnClickListener {
            binding.scrollLayout.removeAllViews()
        }
    }


    private fun onAddButtonClicked() {
        setVisibility(clicked)
        setAnimation(clicked)
        clicked = !clicked
    }

    private fun setVisibility(clicked: Boolean) {
        if (!clicked) {
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

    @SuppressLint("InflateParams")
    private fun addNewSpm() {
        val inflater = LayoutInflater.from(requireContext()).inflate(R.layout.row_add_spm, null)
        binding.scrollLayout.addView(inflater, binding.scrollLayout.childCount)
    }

    @SuppressLint("InflateParams")
    private fun addNewTitle() {
        val inflater = LayoutInflater.from(requireContext()).inflate(R.layout.row_add_titles, null)
        binding.scrollLayout.addView(inflater, binding.scrollLayout.childCount)
    }

    private fun saveData() {
        val skjema: MutableMap<String, Any> = HashMap()
        list.clear()
        listTitle.clear()
        var pluss = 0
        var pluss2 = 0
        var f: View?
        val count = binding.scrollLayout.childCount
        val skjemaR = SkjemaFirebase()
        for (i in 0 until count) {
            f = binding.scrollLayout.getChildAt(i)
            var int = f.id.toString().toInt()

            if (int == R.id.add_spm) {
                val sporsmal: EditText = f.findViewById(R.id.text_spm)
                val sporsmalTest = Test()
                sporsmalTest.spormal = sporsmal.text.toString()
                if(sporsmal.editableText.isNotEmpty()) {
                    list.add(sporsmalTest)
                }
                else{
                    Toast.makeText(requireContext(), "Du mangler å fylle ut et spørsmåls feltene", Toast.LENGTH_LONG).show()
                }

            } else {
                val tittel: EditText = f.findViewById(R.id.tittel_edit_text)
                val kunde: EditText = f.findViewById(R.id.kunde_text_edit)
                val anlegg: EditText = f.findViewById(R.id.anlegg_text_edit)
                val overforing: EditText = f.findViewById(R.id.overforing_edit_text)
                val adresse: EditText = f.findViewById(R.id.adresse_edit_text)
                skjemaR.Tittel = tittel.text.toString()
                skjemaR.Kunde = kunde.text.toString()
                skjemaR.Anlegg = anlegg.text.toString()
                skjemaR.Overforing = overforing.text.toString()
                skjemaR.Adresse = adresse.text.toString()
                if(tittel.editableText.isEmpty() && kunde.editableText.isEmpty() && anlegg.editableText.isEmpty() && overforing.editableText.isEmpty() && adresse.editableText.isEmpty()) {
                    Toast.makeText(requireContext(), "Du mangler å fylle ut et tittel feltene", Toast.LENGTH_LONG).show()
                }
            }
        }
            if(list.isNotEmpty()) {

                for (i in 0 until list.size) {
                    skjema["$pluss Spørsmål"] = list[i].spormal.toString()
                    pluss++
                    Toast.makeText(requireContext(), list[i].spormal, Toast.LENGTH_LONG).show()
                }
                    skjema["Tittel"] = skjemaR.Tittel.toString()
                    skjema["Kunde"] = skjemaR.Kunde.toString()
                    skjema["Anlegg"] = skjemaR.Anlegg.toString()
                    skjema["Overforing"] = skjemaR.Overforing.toString()
                    skjema["Adresse"] = skjemaR.Adresse.toString()

                db.collection("Saker").document(skjemaR.Tittel.toString())
                        .set(skjema)
                        .addOnSuccessListener { documentReference -> Log.d(ContentValues.TAG, "Skjema lagt til med ID: ") }
                        .addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error adding form", e) }
            }
    }
}


