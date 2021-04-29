package com.example.brannvarsling.dialogFragment


import android.Manifest
import android.app.Dialog
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
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
import com.itextpdf.text.*
import com.itextpdf.text.pdf.BaseFont
import com.itextpdf.text.pdf.PdfWriter
import com.itextpdf.text.pdf.draw.LineSeparator
import com.itextpdf.text.pdf.draw.VerticalPositionMark
import java.io.ByteArrayOutputStream
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class FormDialogFragment(private var formType: String, private var type: String) : DialogFragment() {

    private val storageCode: Int = 100
    private lateinit var binding: FormdialogWindowBinding
    private var db = FirebaseFirestore.getInstance()
    private var list = ArrayList<String>()


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
                    requestPermissions(permissions, storageCode)
                } else {
                    savePdf()
                }
            } else {
                savePdf()
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

        private fun takeScreenshotTitle(view: View): Bitmap {
            val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            canvas.scale(0.5F, 0.5F)
            view.draw(canvas)
            return bitmap
        }


    private fun addImage(mDoc: Document, byteArray: ByteArray){

        val image: Image = Image.getInstance(byteArray)
        val rightChunk = Chunk(image, 90F, 0F, true)
        val p = Paragraph(rightChunk)
        p.alignment = Element.ALIGN_RIGHT

        mDoc.add(p)

    }


    private fun savePdf() {
        val mDoc = Document()
        val mFileName = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(System.currentTimeMillis())
        val mFilePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).path + "/" + mFileName + ".pdf"
        try {

            // åpne pdf dokumentet for skriving
            mDoc.open()


            // signatur av oppretter
            PdfWriter.getInstance(mDoc, FileOutputStream(mFilePath))
            mDoc.open()
            mDoc.pageSize = PageSize.A4
            mDoc.addAuthor("Mr.Jensen")
            mDoc.addCreationDate()
            mDoc.addCreator("Henningsen")

            // font
            val title = Font(BaseFont.createFont(), 36.0f, Font.NORMAL, BaseColor.RED)
            val tekst = Font(BaseFont.createFont(), 25.0f, Font.NORMAL, BaseColor.BLACK)
            val overskrift2 = Font(BaseFont.createFont(), 30.0f, Font.NORMAL, BaseColor.RED)
            val overskrift = Font(BaseFont.createFont(), 30.0f, Font.NORMAL, BaseColor.BLACK)


            // title
            addNewItem(mDoc, binding.tittelText.text.toString(), Element.ALIGN_CENTER, title)
            addNewItem(mDoc, type, Element.ALIGN_CENTER, title)

            addLineSeparator(mDoc)


            // innhold
            addNewItemLR(mDoc, binding.kundeText.text.toString(),binding.kundeTextEdit.text.toString(), overskrift, tekst)
            addNewItemLR(mDoc, binding.adresseText.text.toString(),binding.adresseEditText.text.toString(), overskrift, tekst)
            addNewItemLR(mDoc, binding.anleggText.text.toString(),binding.anleggTextEdit.text.toString(), overskrift, tekst)
            addNewItemLR(mDoc, binding.overforingText.text.toString(),binding.overforingEditText.text.toString(), overskrift, tekst)

            //Spaceing
            addLineSpace(mDoc)
            //Overskrift
            addNewItemLR(mDoc, "Benevnelse","Ja/Nei", overskrift2, overskrift2)
            addLineSpace(mDoc)


            // Pdf innhold
            val count = binding.formLayout.childCount
            for (i in 0 until count) {
                val item = list[i]
                val data = "$i.$item"
                val v = binding.formLayout.getChildAt(i)
                val screen = takeScreenshotTitle(v.findViewById(R.id.checkbox_layout))
                val stream = ByteArrayOutputStream()
                screen.compress(Bitmap.CompressFormat.PNG, 100,stream)
                val byte: ByteArray = stream.toByteArray()
                addNewItem(mDoc, data, Element.ALIGN_LEFT, tekst)
                addImage(mDoc, byte)
                addLineSeparator(mDoc)
            }

            //Add
            mDoc.close()

            //Sted Lagret
            Toast.makeText(requireContext(), "$mFileName.pdf \n er lagret i \n $mFilePath", Toast.LENGTH_LONG).show()
        }
        catch (e: Exception) {
            Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun addNewItem(mDoc: Document, text: String, align: Int, style: Font) {
        val chunk = Chunk(text, style)
        val p = Paragraph(chunk)
        p.alignment = align
        mDoc.add(p)
    }

    private fun addLineSeparator(mDoc: Document) {
        val line = LineSeparator()
        line.lineColor = BaseColor.BLACK
        mDoc.add(Chunk(line))
    }

    private fun addLineSpace(mDoc: Document) {
        mDoc.add(Paragraph("\n"))
    }

    private fun addNewItemLR(
            mDoc: Document,
            textLeft: String,
            textRight: String,
            styleLeft: Font,
            styleRight: Font
    ) {
        val chunkLeft = Chunk(textLeft, styleLeft)
        val chunkRight = Chunk(textRight, styleRight)
        val p = Paragraph(chunkLeft)
        p.add(Chunk(VerticalPositionMark()))
        p.add(chunkRight)
        mDoc.add(p)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode) {
            storageCode -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    savePdf()
                } else {
                    Toast.makeText(requireContext(), "Denied..", Toast.LENGTH_SHORT).show()
                }
            }
        }
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


