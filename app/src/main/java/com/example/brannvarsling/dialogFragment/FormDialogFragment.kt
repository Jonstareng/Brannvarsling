package com.example.brannvarsling.dialogFragment


import android.Manifest
import android.app.Dialog
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.fragment.app.DialogFragment
import com.example.brannvarsling.dataClass.SkjemaFirebase
import com.example.brannvarsling.dataClass.Test
import com.example.brannvarsling.databinding.FormdialogWindowBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.itextpdf.text.Document
import com.itextpdf.text.Paragraph
import com.itextpdf.text.pdf.PdfWriter
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class FormDialogFragment(sakerId: String, formType: String) : DialogFragment() {

    private val STORAGE_CODE: Int = 100;
    private lateinit var binding: FormdialogWindowBinding
    private var db = FirebaseFirestore.getInstance()
    private var list = ArrayList<String>()
    private var list2 = ArrayList<String>()
    private val documentId = formType




    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        binding = FormdialogWindowBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onCreateDialog(savedInstanceState)




        binding.savePdf.setOnClickListener {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                if (checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_DENIED) {
                    val permissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    requestPermissions(permissions, STORAGE_CODE)
                }
                else {
                    savePdf()
                }
            }
            else {
                savePdf()
            }
        }

    }

    private fun savePdf() {
        val mDoc = Document()
        val mFileName = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(System.currentTimeMillis())
        val mFilePath = this.context?.getExternalFilesDir(null)?.path + "/" + mFileName + ".pdf"

        try {
            PdfWriter.getInstance(mDoc, FileOutputStream(mFilePath))
            // åpne pdf dokumentet for skriving
            mDoc.open()

            // signatur av oppretter
            mDoc.addAuthor("Mr.Jensen")

            // Pdf innhold
            val mTittel =  Paragraph(binding.tittelText.text.toString())
            mDoc.add(mTittel)
            val mKunde = Paragraph(binding.kundeText.text.toString())
            mDoc.add(mKunde)
            val mAdresse = Paragraph(binding.adresseText.text.toString())
            mDoc.add(mAdresse)
            val mAnleggTekst = Paragraph(binding.anleggText.text.toString())
            mDoc.add(mAnleggTekst)
            val mOverforingTekst = Paragraph(binding.overforingText.text.toString())
            mDoc.add(mOverforingTekst)

            mDoc.close()
            //Sted Lagret
            Toast.makeText(requireContext(), "$mFileName.pdf \n er lagret i \n $mFilePath", Toast.LENGTH_LONG).show()
        }
        catch (e: Exception) {
            Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode) {
            STORAGE_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    savePdf()
                } else {
                    Toast.makeText(requireContext(), "Denied..", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        binding.savePdf.setOnClickListener{
            dismiss()
        }
        getFormData()

        return dialog
    }


    private fun addNewSpm() {
        val inflater = LayoutInflater.from(requireContext()).inflate(com.example.brannvarsling.R.layout.row_get_spm, null)
        binding.formLayout.addView(inflater, binding.formLayout.childCount)
    }



    private fun getFormData() {
        val docRef = db.collection("Saker").document(documentId)
        val ref = db.collection("Saker").document(documentId).collection("Spørsmål")
        var count = 1


        docRef.get().addOnSuccessListener { documentSnapshot ->
            val data = documentSnapshot.toObject(SkjemaFirebase::class.java)

            binding.tittelText.text = data?.Tittel
            binding.kundeText.text = data?.Kunde
            binding.adresseText.text = data?.Adresse
            binding.anleggText.text = data?.Anlegg
            binding.overforingText.text = data?.Overforing


        }

        ref.get().addOnSuccessListener { snapshot ->
            for(value in snapshot) {
                value.data.mapValues {
                    val values = it.value
                    list.add(values.toString())
                }
            }

            for (i in 0 until list.size) {
                val view: View = LayoutInflater.from(requireContext()).inflate(com.example.brannvarsling.R.layout.row_get_spm, null)
                binding.formLayout.addView(view, binding.formLayout.childCount)
                val spm: TextView = view.findViewById(com.example.brannvarsling.R.id.text_add_spm)
                val test = Test()
                val item = list[i]
                test.spormal = "$count. $item"
                spm.text = test.spormal
                count++
            }

        }
    }
}


