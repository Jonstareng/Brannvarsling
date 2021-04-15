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
import android.widget.EditText
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.brannvarsling.R
import com.example.brannvarsling.dataClass.FirebaseCases
import com.example.brannvarsling.R.layout.row_add_titles
import com.example.brannvarsling.dataClass.SkjemaFirebase
import com.example.brannvarsling.dataClass.Test
import com.example.brannvarsling.databinding.FragmentFormBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class Form: Fragment() {
    private lateinit var binding: FragmentFormBinding
    private var db = FirebaseFirestore.getInstance()
    private var list = ArrayList<Test>()
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
            saveData(view)
        }

        // Bare en knapp for floatingbutton
        binding.floatingActionButton.setOnClickListener {
            onAddButtonClicked()
        }

        binding.buttonFjern.setOnClickListener {

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
        val inflater = LayoutInflater.from(requireContext()).inflate(row_add_titles, null)
        binding.scrollLayout.addView(inflater, binding.scrollLayout.childCount)
    }

    private fun saveData(v: View) {
        val skjema: MutableMap<String, Any> = HashMap()
        list.clear()
        val data = SkjemaFirebase()
        var fin: View?
        val count = binding.scrollLayout.childCount
        var pluss = 0

        for (i in 0 until count){
            fin = binding.scrollLayout.getChildAt(i)

            val sporsmal: EditText = fin.findViewById(R.id.text_spm)
            val sporsmalTest = Test()
            sporsmalTest.spormal = sporsmal.text.toString()
            list.add(sporsmalTest)
        }
        fin = binding.scrollLayout

        val tittel: EditText = fin.findViewById(R.id.tittel_edit_text)
        val kunde: EditText = fin.findViewById(R.id.kunde_text_edit)
        val anlegg: EditText = fin.findViewById(R.id.anlegg_text_edit)
        val adresse: EditText = fin.findViewById(R.id.editText4)
        val overforing: EditText = fin.findViewById(R.id.editText5)

        val checkboxJa = R.id.checkbox_ja.toString().toBoolean()
        val checkboxNei = R.id.checkbox_nei.toString().toBoolean()

        Toast.makeText(context, "${data.Tittel}", Toast.LENGTH_LONG).show()
        data.Tittel = tittel.text.toString()
        data.Kunde = kunde.text.toString()
        data.Anleggssted = anlegg.text.toString()
        data.Adresse = adresse.text.toString()
        data.overføring = overforing.text.toString()
        //data.spormal = sporsmal.text.toString()
        val tittelData = data.Tittel.toString()
        val kundeData = data.Kunde.toString()
        val anleggData = data.Anleggssted.toString()
        val adresseData = data.Adresse.toString()
        val overforingData = data.overføring.toString()
        val sporsmalData = data.spormal.toString()


        skjema["Tittel"] = tittelData
        skjema["Kunde"] = kundeData
        skjema["Anleggssted"] = anleggData
        skjema["Adresse"] = adresseData
        skjema["Alarmoverføring"] = overforingData
        for (i in 0 until list.size) {
            skjema["$pluss Innhold"] = list[i].spormal.toString()
            pluss++
        }
        db.collection("Saker")
                .add(skjema)
                .addOnSuccessListener { documentReference -> Log.d(ContentValues.TAG, "Skjema lagt til med ID: " + documentReference.id) }
                .addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error adding form", e) }


    }
}