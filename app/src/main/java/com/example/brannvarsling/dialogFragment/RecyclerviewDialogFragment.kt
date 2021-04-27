package com.example.brannvarsling.dialogFragments

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.content.DialogInterface
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentTransaction
import com.example.brannvarsling.dataClass.DialogFragmentItems
import com.example.brannvarsling.databinding.RecyclerdialogWindowBinding
import com.example.brannvarsling.dialogFragment.AlertDateDialog
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import kotlin.collections.ArrayList


class RecyclerviewDialogFragment(id: String) : DialogFragment() {

    private lateinit var binding: RecyclerdialogWindowBinding
    private lateinit var currentDateAndTime: String
    private lateinit var storage: FirebaseStorage
    private lateinit var imageView: ImageView
    private lateinit var Bitmap: Bitmap
   // private lateinit var month: String

    // private lateinit var month: String
    //private lateinit var case: Array<String>
    private var db = FirebaseFirestore.getInstance()
    private val documentId = id
    private var formOpen = ""
    private val pickImage = 100
    private var imageUri: Uri? = null
    private var CAMERA_PERMISSION_CODE = 1
    private var CAMERA_REQUEST_CODE = 2
    private var counter: Long = 0
    private var customer = ""
    private var type = ""
    private var desc =""
    private lateinit var Uri: Uri
    var imagesRef: StorageReference? = FirebaseStorage.getInstance().reference.child("Images")
    private var formType = ""
    private lateinit var caseChoice: ArrayList<String>
    val sakerId = db.collection("Saker").document(documentId).id


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = RecyclerdialogWindowBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onCreateDialog(savedInstanceState)
        getData()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        getNotifyCounter()
        binding.close.setOnClickListener {
            dismiss()
        }
        binding.deleteRecyclerItem.setOnClickListener {
            slettDialog()
        }
        binding.saveDate.setOnClickListener {
            alertDialog()
        }
        binding.openForm.setOnClickListener {
            openForm()
        }
        binding.buttonVedlegg.setOnClickListener{
            val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            startActivityForResult(gallery, pickImage)
        }
        binding.buttonCamera.setOnClickListener{
            dispatchTakePictureIntent()
        }
        return dialog
    }

    private fun getData() {
        val docRef = db.collection("Saker").document(documentId)

        docRef.get().addOnSuccessListener { documentSnapshot ->
            val data = documentSnapshot.toObject(DialogFragmentItems::class.java)
            binding.displayCustomer.text = data?.Customer
            binding.displayType.text = data?.Type
            binding.displayDate.text = data?.Date
            binding.displayDescription.text = data?.Description
            customer = data?.Customer.toString()
            type = data?.Type.toString()
            desc = data?.Description.toString()

        }
        docRef.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                formOpen = snapshot.get("Form").toString()
            }
        }
    }

    private fun deleteItem() {
        val docRef = db.collection("Saker").document(documentId)

        docRef.delete()
            .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully deleted!") }
            .addOnSuccessListener { e -> Log.w(TAG, "Error deleting document") }
        Toast.makeText(requireContext(), "Sak $customer slettet", Toast.LENGTH_SHORT).show()
    }


    private fun slettDialog() {
        val builder = AlertDialog.Builder(activity)
        builder.setMessage("Er du sikker på at du vil slette $customer")
            .setCancelable(false)
            .setPositiveButton("Slett", DialogInterface.OnClickListener { dialog, id ->
                deleteItem()
                dismiss()
            }).setNegativeButton("Avbryt", DialogInterface.OnClickListener { dialog, id ->
                dialog.dismiss()
            })
        val alert = builder.create()
        alert.show()
    }

    private fun alertDialog() {
        val builder = AlertDateDialog(documentId, customer, type, desc, formOpen)

        val fragmentManager = activity?.supportFragmentManager
        val transaction = fragmentManager?.beginTransaction()

        transaction?.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        transaction?.add(android.R.id.content, builder)?.addToBackStack(null)?.commit()

    }

    private fun openForm() {
        val dialogFragment = FormDialogFragment(formOpen, type)

        val fragmentManager = activity?.supportFragmentManager
        val transaction = fragmentManager?.beginTransaction()

        transaction?.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        transaction?.add(android.R.id.content, dialogFragment)?.addToBackStack(null)?.commit()
    }

    private fun chooseImage() {
        val intent = Intent()
        intent.type = "Image/"
        intent.setAction(Intent.ACTION_GET_CONTENT)
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), pickImage)

    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == pickImage) {
            imageUri = data?.data

        }
    }
    private fun imageStorage () {
        storage = Firebase.storage
        val storageRef = storage.reference

    }



    private fun dispatchTakePictureIntent() {
        if (ContextCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        ) {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent, CAMERA_REQUEST_CODE)
        } else {
            ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.CAMERA),
                    CAMERA_PERMISSION_CODE
            )
        }
        fun onRequestPermissionsResult(
                requestCode: Int,
                permissions: Array<out String>,
                grantResults: IntArray
        ) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            if (requestCode == CAMERA_PERMISSION_CODE){
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    startActivityForResult(intent, CAMERA_REQUEST_CODE)

                }
            }
        }
    }

    private fun getNotifyCounter() {

        val ref = db.collection("NotificationIds").document("qsK39UawP1XXeoTCrPcn")

        ref.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                counter = snapshot.get("Counter") as Long
            }
        }
    }
}
