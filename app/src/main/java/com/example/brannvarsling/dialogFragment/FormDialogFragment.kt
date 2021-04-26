package com.example.brannvarsling.dialogFragment


import android.Manifest
import android.app.Dialog
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.fragment.app.DialogFragment
import com.example.brannvarsling.R
import com.example.brannvarsling.dataClass.SkjemaFirebase
import com.example.brannvarsling.dataClass.Spm
import com.example.brannvarsling.databinding.FormdialogWindowBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.itextpdf.text.Document
import com.itextpdf.text.Image
import com.itextpdf.text.Paragraph
import com.itextpdf.text.pdf.PdfWriter
import java.io.ByteArrayOutputStream
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class FormDialogFragment(sakerId: String, formType: String) : DialogFragment() {

    private val STORAGE_CODE: Int = 100;
    private lateinit var binding: FormdialogWindowBinding
    private var db = FirebaseFirestore.getInstance()
    private var list = ArrayList<String>()
    private val formType = formType


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
        private fun takeScreenshotTitle(view: View): Bitmap{
            val bitmap = Bitmap.createBitmap(view.width, 400, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            canvas.scale(0.5F, 0.5F)
            view.draw(canvas)
            return bitmap
        }
    private fun takeScreenshotBenevnelse(view: View): Bitmap{
        val bitmap = Bitmap.createBitmap(view.width, 40, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.scale(0.5F, 0.5F)
        view.draw(canvas)
        return bitmap
    }
    private fun takeScreenshotSkjema(): Bitmap{
        val bt: Bitmap = Bitmap.createBitmap(binding.scrollView.getChildAt(0).height, binding.scrollView.getChildAt(0).width, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bt)
        val dw = binding.scrollView.background
        canvas.scale(0.5F, 0.4F)
        if (dw != null)
            dw.draw(canvas)
        else
        binding.scrollView.draw(canvas)
        return bt
    }
    private fun addImage(document: Document, byteArray: ByteArray, byteArray2: ByteArray, byteArray3: ByteArray){

        val image: Image = Image.getInstance(byteArray)
        val image2: Image = Image.getInstance(byteArray2)
        val image3: Image = Image.getInstance(byteArray3)

        document.add(image)
        document.add(image2)
        document.newPage()
        document.add(image3)
    }


    private fun savePdf() {
        val mDoc = Document()
        val mFileName = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(System.currentTimeMillis())
        val mFilePath = this.context?.getExternalFilesDir(null)?.path + "/" + mFileName + ".pdf"
        try {
            val screen = takeScreenshotTitle(binding.allForm)
            val screenOverskrift = takeScreenshotBenevnelse((binding.benevnelseOverskrift))
            val screenTitle = takeScreenshotSkjema()
            PdfWriter.getInstance(mDoc, FileOutputStream(mFilePath))
            // åpne pdf dokumentet for skriving
            mDoc.open()
            val stream = ByteArrayOutputStream()
            screen.compress(Bitmap.CompressFormat.PNG, 100,stream)
            val byte: ByteArray = stream.toByteArray()
            val stream2 = ByteArrayOutputStream()
            screenTitle.compress(Bitmap.CompressFormat.PNG, 100,stream2)
            val byte2: ByteArray = stream2.toByteArray()
            val stream3 = ByteArrayOutputStream()
            screenOverskrift.compress(Bitmap.CompressFormat.PNG, 100,stream3)
            val byte3: ByteArray = stream3.toByteArray()

            addImage(mDoc,byte, byte3, byte2)

            // signatur av oppretter
            mDoc.addAuthor("Mr.Jensen")

            // Pdf innhold

            //Add
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



    private fun getFormData() {
        val docRef = db.collection("Skjema").document(formType)
        val ref = db.collection("Skjema").document(formType).collection("Spørsmål")
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
                val view: View = LayoutInflater.from(requireContext()).inflate(R.layout.row_get_spm, null)
                binding.formLayout.addView(view, binding.formLayout.childCount)
                val spm: TextView = view.findViewById(R.id.text_add_spm)
                val test = Spm()
                val item = list[i]
                test.spormal = "$count. $item"
                spm.text = test.spormal
                count++
            }

        }
    }
}


