package com.example.brannvarsling.dialogFragment

import android.app.Dialog
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfDocument.PageInfo.Builder
import android.os.Build.VERSION_CODES.R
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.TextView
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import com.example.brannvarsling.dataClass.SkjemaFirebase
import com.example.brannvarsling.databinding.FormdialogWindowBinding
import com.google.firebase.firestore.FirebaseFirestore
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import com.example.brannvarsling.dataClass.Test


class FormDialogFragment(sakerId: String, formType: String) : DialogFragment() {

    private lateinit var binding: FormdialogWindowBinding
    private var db = FirebaseFirestore.getInstance()
    private var list = ArrayList<String>()
    private val documentId = formType

    private var editText: TextView? = null
    private var btnCreate: Button? = null

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        binding = FormdialogWindowBinding.inflate(inflater, container, false)
        return binding.root
    }
    @RequiresApi(R)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onCreateDialog(savedInstanceState)

        btnCreate = binding.savePdf
        this.editText = binding.tittelText

        btnCreate!!.setOnClickListener {
            createPdf(editText.toString())
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

/*
    private fun addNewSpm() {
        val inflater = LayoutInflater.from(requireContext()).inflate(R.layout.row_get_spm, null)
        binding.formLayout.addView(inflater, binding.formLayout.childCount)
    }
*/


    private fun getFormData() {
        val docRef = db.collection("Saker").document(documentId)
        val ref = db.collection("Saker").document(documentId).collection("Spørsmål")
        var count = 3


        docRef.get().addOnSuccessListener { documentSnapshot ->
            val data = documentSnapshot.toObject(SkjemaFirebase::class.java)
            binding.adresseText.text = data?.Adresse
            binding.anleggText.text = data?.Anlegg
            binding.kundeText.text = data?.Kunde
            binding.overforingText.text = data?.Overforing
            binding.tittelText.text = data?.Tittel

        }
        ref.get().addOnSuccessListener { document ->

            document.forEach {
                val data = it.data
                list.add(data.values.toString())
            }
            var f: View?
            for (i in 0 until list.size) {
                f = binding.formLayout
                val spm: TextView = f.findViewById(R.id.text_add_spm)
                var test = Test()
                test.spormal = list[i]
                spm.setText(test.spormal)
            }
        }

    }


    @RequiresApi(R)
    fun createPdf(sometext: String) {
        // create a new document
        val document = PdfDocument()
        // crate a page description
        val pageInfo: PdfDocument.PageInfo = Builder(300, 600, 1).create()
        // start a page
        val page: PdfDocument.Page = document.startPage(pageInfo)
        val canvas: Canvas = page.canvas
        val paint = Paint()
        paint.color = Color.RED
        paint.color = Color.BLACK
        canvas.drawText(sometext, 80F, 50F, paint)
        // finish the page
        document.finishPage(page)

        // write the document content
        val directoryPath = this.context?.getExternalFilesDir(null)?.path + "/mypdf/"
        val file = File(directoryPath)
        if (!file.exists()) {
            file.run {
                mkdirs() }
        }
        val targetPdf = directoryPath + "test-2.pdf"
        val filePath = File(targetPdf)
        try {
            document.writeTo(FileOutputStream(filePath))
            Toast.makeText(requireContext(), "Done  $directoryPath", Toast.LENGTH_LONG).show()
        } catch (e: IOException) {
            Log.e("main", "error $e")
            Toast.makeText(requireContext(), "Something wrong: $e", Toast.LENGTH_LONG).show()
        }
        // close the document
        document.close()
    }

}


