package com.example.brannvarsling.dialogFragments

import android.R
import android.app.Activity.RESULT_OK
import android.app.Dialog
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.brannvarsling.databinding.DialogWindowBinding
import com.google.firebase.firestore.FirebaseFirestore


class AddDialogFragment: DialogFragment() {

    private lateinit var binding: DialogWindowBinding
    private var db = FirebaseFirestore.getInstance()
    private var counter = ""
    private var formType = ""
    private var list = ArrayList<String>()


    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        binding = DialogWindowBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onCreateDialog(savedInstanceState)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        getNotifyCounter()
        spinnerForm()
        binding.avbryt.setOnClickListener{
            dismiss()
        }
        binding.button2.setOnClickListener{
            writeToDb()
            notificationCounter()
        }
        return dialog
    }

    // Henter ut Verdiene som brukeren skriver iinn i tekst feltene og lagrer de i databasen,
    // formtype hentes fra spinner og counter hentes fra et annet sted i databasen
    private fun writeToDb() {
        val user: MutableMap<String, Any> = HashMap()
        val title = binding.editTextTextPersonName.text.toString()
        val type = binding.editTextTextPersonName2.text.toString()
        val description = binding.editTextTextMultiLine.text.toString()

        // sørger for at feltene blir fylt ut, Vi trenger disse verdiene andre steder i koden
        if (title != "" && type != "") {

            user["Customer"] = title
            user["Type"] = type
            user["Description"] = description
            user["NotificationID"] = counter
            user["Form"] = formType
            // Vi setter date tom her slik at kalender funksjonen ikke får kjørt på grunn av null verdier
            user["Date"] = ""

            db.collection("Saker")
                    .add(user)
                    .addOnSuccessListener { documentReference -> Log.d(ContentValues.TAG, "DocumentSnapshot added with ID: " + documentReference.id) }
                    .addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error adding document", e) }
            dismiss()
        }
        else {
            Toast.makeText(context, "Fyll ut alle feltene", Toast.LENGTH_LONG).show()
        }

    }
    // øker countern med 1 slik at neste sak som blir laget ikke får det samme nr
    // funksjonen bruker counter verdien som alt er hentet fra databasen og skriver den nye verdien til databasen
    private fun notificationCounter() {
        val number: MutableMap<String, Any> = HashMap()
        val newCounter: Int = counter.toInt() + 1
        newCounter.toLong()
        number["Counter"] = newCounter
        db.collection("NotificationIds").document("qsK39UawP1XXeoTCrPcn").set(number)

    }

    //Henter ut notifikasjons iden som saken skal få tildelt
    private fun getNotifyCounter() {

        val ref = db.collection("NotificationIds").document("qsK39UawP1XXeoTCrPcn")

        ref.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                counter = snapshot.get("Counter").toString()
            }
        }
    }
    //Henter ut document id og legger de inn i en spinner
    // Dette er da alle skjemaene brukeren har opprettet som hentes ut her
    private fun spinnerForm() {
        db.collection("Skjema").get().addOnSuccessListener { documents ->
            for (document in documents) {
                val data = document.id
                list.add(data)
            }

            val arrayAdapter =
                    ArrayAdapter(requireContext(), R.layout.simple_dropdown_item_1line, list)
            binding.spinner.adapter = arrayAdapter
            arrayAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
            binding.spinner.onItemSelectedListener = object :
                    AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                ) {
                    // setter formtype verdien slik at vi kan lagre verdien i databasen
                    formType = parent?.getItemAtPosition(position).toString()
                    arrayAdapter.notifyDataSetChanged()
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                }
            }
        }
    }

}
