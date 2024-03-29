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
import android.widget.LinearLayout
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.brannvarsling.R
import com.example.brannvarsling.R.layout.row_add_titles
import com.example.brannvarsling.dataClass.SkjemaFirebase
import com.example.brannvarsling.dataClass.Spm
import com.example.brannvarsling.databinding.FragmentFormBinding
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.collections.set


class Form: Fragment() {
    private lateinit var binding: FragmentFormBinding
    private var db = FirebaseFirestore.getInstance()
    private var list = ArrayList<Spm>()
    private var listTitle = ArrayList<SkjemaFirebase>()

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

        // Lagre knapp. (Skal kunne lagre skjema som pdf)
        binding.buttonSubmit.setOnClickListener {
            saveData()
        }

        // Bare en knapp for floatingbutton
        binding.floatingActionButton.setOnClickListener {
            onAddButtonClicked()
        }
        // Fjerner alle views i scrollLayout
        binding.buttonFjern.setOnClickListener {
            binding.scrollLayout.removeAllViews()
        }
    }

    // Kjører setVisibility funksjon og setanimation funksjon
    private fun onAddButtonClicked() {
        setVisibility(clicked)
        setAnimation(clicked)
        clicked = !clicked
    }

    // // Endrer synligheten til underknapper når man trykker på floatingActionBar knapp
    private fun setVisibility(clicked: Boolean) {
        if (!clicked) {
            binding.floatingSpm.visibility = View.VISIBLE
            binding.floatingTitle.visibility = View.VISIBLE
            binding.buttonSubmit.visibility = View.VISIBLE
            binding.buttonFjern.visibility = View.VISIBLE
        } else {
            binding.floatingSpm.visibility = View.INVISIBLE
            binding.floatingTitle.visibility = View.INVISIBLE
            binding.buttonSubmit.visibility = View.INVISIBLE
            binding.buttonFjern.visibility = View.INVISIBLE
        }
    }

    // Setter animasjon på floatingAction knapper når man åpner og lukker
    private fun setAnimation(clicked: Boolean) {
        if (!clicked) {
            binding.floatingSpm.startAnimation(fromBottom)
            binding.floatingTitle.startAnimation(fromBottom)
            binding.buttonFjern.startAnimation(fromBottom)
            binding.buttonSubmit.startAnimation(fromBottom)
            binding.floatingActionButton.startAnimation(rotateOpen)
        } else {
            binding.floatingSpm.startAnimation(toBottom)
            binding.floatingTitle.startAnimation(toBottom)
            binding.buttonFjern.startAnimation(toBottom)
            binding.buttonSubmit.startAnimation(toBottom)
            binding.floatingActionButton.startAnimation(rotateClose)
        }
    }

    // Legger til nytt spørsmål i skjema
    @SuppressLint("InflateParams")
    private fun addNewSpm() {
        val inflater: View = LayoutInflater.from(requireContext()).inflate(R.layout.row_add_spm, null)
        binding.scrollLayout.addView(inflater, binding.scrollLayout.childCount)
        val buttonRemove: Button = inflater.findViewById(R.id.btn_remove)
        buttonRemove.setOnClickListener {
            (inflater.parent as LinearLayout)
                    .removeView(inflater)
        }
    }

    // Legger til ny tittel i skjema
    @SuppressLint("InflateParams")
    private fun addNewTitle() {
        val inflater = LayoutInflater.from(requireContext()).inflate(row_add_titles, null)
        binding.scrollLayout.addView(inflater, binding.scrollLayout.childCount)
        val buttonRemove: Button = inflater.findViewById(R.id.btn_remove_title)
        buttonRemove.setOnClickListener {
            (inflater.parent as LinearLayout)
                    .removeView(inflater)
        }
    }
    /*
    // Lagrer alle feltene som er opprettet i skemaet
    // Vi bruker lister, dataklasser og for løkker som hjelpemiddel til å lagre dataen i databasen
    // Vi har også sørget for at alle feltene må være fylt ut for å kunne lagre
     */
    private fun saveData() {
        val skjema: MutableMap<String, Any> = HashMap()
        val skjemaS: MutableMap<String, Any> = HashMap()

        list.clear()
        listTitle.clear()
        var pluss = 0
        var f: View?
        val count = binding.scrollLayout.childCount
        val skjemaR = SkjemaFirebase()
        for (i in 0 until count) {
            f = binding.scrollLayout.getChildAt(i)
            val int = f.id.toString().toInt()

            if (int == R.id.add_spm) {
                val sporsmal: EditText = f.findViewById(R.id.text_spm)
                val sporsmalValue = Spm()
                sporsmalValue.spormal = sporsmal.text.toString()
                if(sporsmal.editableText.isNotEmpty()) {
                    list.add(sporsmalValue)
                }
                else{
                    Toast.makeText(requireContext(), "Du mangler å fylle ut et av spørsmåls feltene", Toast.LENGTH_LONG).show()
                }

            } else {
                val tittel: EditText = f.findViewById(R.id.tittel_edit_text)
                val kunde: EditText = f.findViewById(R.id.kunde_text_edit)
                val anlegg: EditText = f.findViewById(R.id.anlegg_text_edit)
                val overforing: EditText = f.findViewById(R.id.overforing_edit_text)
                val adresse: EditText = f.findViewById(R.id.adresse_edit_text)
                skjemaR.Tittel = tittel.text.toString()
                listTitle.add(skjemaR)
                skjemaR.Kunde = kunde.text.toString()
                skjemaR.Anlegg = anlegg.text.toString()
                skjemaR.Overforing = overforing.text.toString()
                skjemaR.Adresse = adresse.text.toString()
                if(tittel.editableText.isEmpty() && kunde.editableText.isEmpty() && anlegg.editableText.isEmpty() && overforing.editableText.isEmpty() && adresse.editableText.isEmpty()) {
                    Toast.makeText(requireContext(), "Du mangler å fylle ut et tittel feltene", Toast.LENGTH_LONG).show()
                }
            }
        }
            if(list.isNotEmpty() && listTitle.isNotEmpty()) {

                for (i in 0 until list.size) {
                    skjemaS["$pluss Spørsmål"] = list[i].spormal.toString()
                    pluss++
                }
                    skjema["Tittel"] = skjemaR.Tittel.toString()
                    skjema["Kunde"] = skjemaR.Kunde.toString()
                    skjema["Anlegg"] = skjemaR.Anlegg.toString()
                    skjema["Overforing"] = skjemaR.Overforing.toString()
                    skjema["Adresse"] = skjemaR.Adresse.toString()

                db.collection("Skjema").document(skjemaR.Tittel.toString())
                        .set(skjema)
                        .addOnSuccessListener { Log.d(ContentValues.TAG, "Skjema lagt til med ID: ") }
                        .addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error adding form", e) }

                db.collection("Skjema").document(skjemaR.Tittel.toString()).collection("Spørsmål").document("Items")
                        .set(skjemaS)
                        .addOnSuccessListener { Log.d(ContentValues.TAG, "Skjema lagt til med ID: ") }
                        .addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error adding form", e) }
                Toast.makeText(requireContext(), "Skjema lagret", Toast.LENGTH_LONG).show()
            }
        else
                Toast.makeText(requireContext(), "Du må ha tittel og spørsmål for å kunne lagre", Toast.LENGTH_LONG).show()
    }
}


