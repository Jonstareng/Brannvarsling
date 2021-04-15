package com.example.brannvarsling.fragments




import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import com.example.brannvarsling.R
import com.example.brannvarsling.R.layout.row_add_titles
import com.example.brannvarsling.databinding.FragmentFormBinding
import com.google.firebase.firestore.FirebaseFirestore


class Form: Fragment() {
    private lateinit var binding: FragmentFormBinding
    private var db = FirebaseFirestore.getInstance()
    //private var skjemaList = ArrayList<CasesModel>()
    private val STORAGE_CODE: Int = 100;


    // FloatingActionBar animasjoner
    private val rotateOpen: Animation by lazy { AnimationUtils.loadAnimation(context, R.anim.rotate_open_anim) }
    private val rotateClose: Animation by lazy { AnimationUtils.loadAnimation(context, R.anim.rotate_close_anim) }
    private val fromBottom: Animation by lazy { AnimationUtils.loadAnimation(context, R.anim.from_bottom_anim) }
    private val toBottom: Animation by lazy { AnimationUtils.loadAnimation(context, R.anim.to_bottom_anim) }

    private var clicked = false


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?): View {

        // Inflate the layout for this fragment
        binding = FragmentFormBinding.inflate(inflater, container, false)
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

        }

        // Bare en knapp for floatingbutton
        binding.floatingActionButton.setOnClickListener {
            onAddButtonClicked()
        }

        binding.buttonFjern.setOnClickListener {

        }

        /*
        binding.buttonLagrePdf.setOnClickListener {
            // Sjekk runtime permission
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                // Om system OS er mindre eller lik Marshmallow 6.0, check permission
                if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                    // permission not granted, request it
                    val permissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    requestPermissions(permissions, STORAGE_CODE)
                }
                else {
                    // permission granted, call savePdf()
                    savePdf()
                }
            }
            else {
                savePdf()
            }
        }
        */
    }

    private fun savePdf() {

    }

    /*
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode) {
            STORAGE_CODE - > {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                }
            }
        }
    }
    */
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
    
    @SuppressLint("InflateParams")
    private fun addNewSpm() {
        val inflater = LayoutInflater.from(requireContext()).inflate(R.layout.row_add_spm,null)
        binding.scrollLayout.addView(inflater, binding.scrollLayout.childCount)
    }


    @SuppressLint("InflateParams")
    private fun addNewTitle() {
        val inflater = LayoutInflater.from(requireContext()).inflate(row_add_titles, null)
        binding.scrollLayout.addView(inflater, binding.scrollLayout.childCount)

    }




}