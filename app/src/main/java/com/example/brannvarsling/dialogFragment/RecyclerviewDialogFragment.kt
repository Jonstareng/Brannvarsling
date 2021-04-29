package com.example.brannvarsling.dialogFragment

import android.Manifest
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.*
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Point
import android.graphics.Rect
import android.graphics.RectF
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.animation.DecelerateInterpolator
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentTransaction
import com.example.brannvarsling.receivers.BroadcastReceiver
import com.example.brannvarsling.R
import com.example.brannvarsling.dataClass.DialogFragmentItems
import com.example.brannvarsling.databinding.RecyclerdialogWindowBinding
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import java.net.URI
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap


class RecyclerviewDialogFragment(id: String, private var customer: CharSequence) : DialogFragment() {

    private lateinit var binding: RecyclerdialogWindowBinding
    private var db = FirebaseFirestore.getInstance()
    private val documentId = id
    private var formOpen = ""
    private val pickImage = 100
    private var imageUrl: Uri? = null
    private var counter: Long = 0
    private var type = ""
    private var desc =""
    private var filePath: Uri? = null
    private var requestCode = 1
    private var year = ""
    private var month = ""
    private var day = ""
    private var cancelNotify = ""
    private val channelID = "Cases ID"
    private var animation: Animator? = null
    private var aniDuration: Int = 0


    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        binding = RecyclerdialogWindowBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        aniDuration = resources.getInteger(android.R.integer.config_shortAnimTime)
        getImage()
        onCreateDialog(savedInstanceState)
        getData()
        checkPermission()
        requestPermission()
        createNotificationChannel()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        getNotifyCounter()
        getNotifyData()
        getImage()
        binding.close.setOnClickListener {
            dismiss()
        }
        binding.deleteRecyclerItem.setOnClickListener {
            deleteDialog()
        }
        binding.saveDate.setOnClickListener {
            alertDialog()
        }
        binding.openForm.setOnClickListener {
            openForm()
        }
        binding.buttonVedlegg.setOnClickListener{
            openGallery()
        }
        binding.vedlegg.setOnClickListener{
            zoomImage(binding.vedlegg, imageUrl!!)
        }
        return dialog
    }

    private fun openGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), pickImage)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?){
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == pickImage && resultCode == Activity.RESULT_OK){

            filePath = data?.data
            Toast.makeText(context, "$filePath", Toast.LENGTH_LONG).show()

            val bt = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, filePath)
            binding.vedlegg.setImageBitmap(bt)
            uploadImage()
        }
    }
    private fun uploadImage() {
        val storageReference = FirebaseStorage.getInstance().reference
        val ref = storageReference.child("$customer.jpg")
        ref.putFile(filePath!!)
        Toast.makeText(context, "Bilde lagret", Toast.LENGTH_LONG).show()

    }
    private fun getImage() {
        val storageReference = FirebaseStorage.getInstance()
        val refStorage = storageReference.getReferenceFromUrl("gs://varslingssystem.appspot.com/$customer.jpg")

        refStorage.downloadUrl.addOnSuccessListener { image ->
            Picasso.get().load(image).into(binding.vedlegg)
            imageUrl = image
        }
    }
    private fun zoomImage(imageView: View, imageId: Uri){

        animation?.cancel()

        val bigImage = binding.zoomImage
        Picasso.get().load(imageId).into(bigImage)


        val startB = Rect()
        val endB = Rect()
        val global = Point()

        imageView.getGlobalVisibleRect(startB)
        binding.consLayout.getGlobalVisibleRect(endB, global)
        startB.offset(-global.x, -global.y)
        endB.offset(-global.x, -global.y)

        val startBounds = RectF(startB)
        val endBounds = RectF(endB)

        val startScale: Float
        if ((endBounds.width() / endBounds.height() > startBounds.width() / startBounds.height())) {
            // Extend start bounds horizontally
            startScale = startBounds.height() / endBounds.height()
            val startWidth: Float = startScale * endBounds.width()
            val deltaWidth: Float = (startWidth - startBounds.width()) / 2
            startBounds.left -= deltaWidth.toInt()
            startBounds.right += deltaWidth.toInt()
        } else {
            // Extend start bounds vertically
            startScale = startBounds.width() / endBounds.width()
            val startHeight: Float = startScale * endBounds.height()
            val deltaHeight: Float = (startHeight - startBounds.height()) / 2f

            startBounds.top -= deltaHeight.toInt()
            startBounds.bottom += deltaHeight.toInt()
        }
        imageView.alpha = 0f
        bigImage.visibility = View.VISIBLE

        // Set the pivot point for SCALE_X and SCALE_Y transformations
        // to the top-left corner of the zoomed-in view (the default
        // is the center of the view).
        bigImage.pivotX = 0f
        bigImage.pivotY = 0f

        // Construct and run the parallel animation of the four translation and
        // scale properties (X, Y, SCALE_X, and SCALE_Y).
        animation = AnimatorSet().apply {
            play(
                ObjectAnimator.ofFloat(
                bigImage,
                View.X,
                startBounds.left,
                endBounds.left)
            ).apply {
                with(ObjectAnimator.ofFloat(bigImage, View.Y, startBounds.top, endBounds.top))
                with(ObjectAnimator.ofFloat(bigImage, View.SCALE_X, startScale, 1f))
                with(ObjectAnimator.ofFloat(bigImage, View.SCALE_Y, startScale, 1f))
            }
            duration = aniDuration.toLong()
            interpolator = DecelerateInterpolator()
            addListener(object : AnimatorListenerAdapter() {

                override fun onAnimationEnd(ani: Animator) {
                    animation = null
                }

                override fun onAnimationCancel(ani: Animator) {
                    animation = null
                }
            })
            start()
        }
        bigImage.setOnClickListener {
            animation?.cancel()

            // Animate the four positioning/sizing properties in parallel,
            // back to their original values.
            animation = AnimatorSet().apply {
                play(ObjectAnimator.ofFloat(bigImage, View.X, startBounds.left)).apply {
                    with(ObjectAnimator.ofFloat(bigImage, View.Y, startBounds.top))
                    with(ObjectAnimator.ofFloat(bigImage, View.SCALE_X, startScale))
                    with(ObjectAnimator.ofFloat(bigImage, View.SCALE_Y, startScale))
                }
                duration = aniDuration.toLong()
                interpolator = DecelerateInterpolator()
                addListener(object : AnimatorListenerAdapter() {

                    override fun onAnimationEnd(ani: Animator) {
                        imageView.alpha = 1f
                        bigImage.visibility = View.GONE
                        animation = null
                    }

                    override fun onAnimationCancel(ani: Animator) {
                        imageView.alpha = 1f
                        bigImage.visibility = View.GONE
                        animation = null
                    }
                })
                start()
            }
        }
    }

    private fun checkPermission(): Boolean {
        val result = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
        return result == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(requireContext(), "Write External Storage permission allows us to do store images. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show()
        } else {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), requestCode)
        }
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
        val ref = db.collection("ImageUrls").document(documentId)
        val storageRef = FirebaseStorage.getInstance().getReference("uploads/$customer")
        storageRef.delete()
        ref.delete()
        docRef.delete()
        val storageReference = FirebaseFirestore.getInstance()
        val refStorage =
            storageReference.collection("Saker")
                .document(documentId)
                .collection("Check").document("document")
        refStorage.delete()
        Toast.makeText(requireContext(), "Sak $customer slettet", Toast.LENGTH_SHORT).show()
    }


    private fun deleteDialog() {
        val builder = AlertDialog.Builder(activity)
        builder.setMessage("Er du sikker på at du vil slette $customer")
            .setCancelable(false)
            .setPositiveButton("Slett") { _, _ ->
                deleteItem()
                dismiss()
            }.setNegativeButton("Avbryt") { _, _ ->
            }
        val alert = builder.create()
        alert.show()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun alertDialog() {

        val layout = layoutInflater.inflate(R.layout.alertdate_window, null)

        val builder = AlertDialog.Builder(activity)
        builder.setView(layout)
                .setCancelable(false)
                .setPositiveButton("Sett dato") { _, _ ->
                    val format = SimpleDateFormat("yyyyMMdd", Locale.ENGLISH)
                    val currentDate = format.format(Date())
                    val date = "$year$month$day"

                    if (date.toInt() >= currentDate.toInt()) {
                        cancelNotification()
                        saveToDB()
                        scheduleNotification()
                    } else

                        Toast.makeText(
                            context,
                            "Varsling ble ikke satt! Datoen har allerede vært",
                            Toast.LENGTH_SHORT
                        ).show()

                }.setNegativeButton("Avbryt") { _, _ ->
            }
        val alert = builder.create()
        val caseChoiceY = arrayOf("2021", "2022", "2023", "2024", "2025")
        val arrayAdapterY = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, caseChoiceY)
        val spinnerY = layout.findViewById<Spinner>(R.id.spinnerYear)
        spinnerY.adapter = arrayAdapterY
        spinnerY.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                year = parent?.getItemAtPosition(position).toString()
                arrayAdapterY.notifyDataSetChanged()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
        val caseChoiceM = arrayOf("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12")
        val arrayAdapterM = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, caseChoiceM)
        val spinnerM = layout.findViewById<Spinner>(R.id.spinnerMonth)
        spinnerM.adapter = arrayAdapterM
        spinnerM.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                month = parent?.getItemAtPosition(position).toString()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

        val case = arrayOf("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13",
            "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31")
        val arrayAdapterD = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, case)
        val spinnerD = layout.findViewById<Spinner>(R.id.spinnerDay)
        spinnerD.adapter = arrayAdapterD
        spinnerD.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                day = parent?.getItemAtPosition(position).toString()
                arrayAdapterD.notifyDataSetChanged()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
        alert.show()
    }


    private fun openForm() {
        val dialogFragment = FormDialogFragment(formOpen, type)

        val fragmentManager = activity?.supportFragmentManager
        val transaction = fragmentManager?.beginTransaction()

        transaction?.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        transaction?.add(android.R.id.content, dialogFragment)?.addToBackStack(null)?.commit()
    }


    private fun downloadToDb(download: String) {
        val db = FirebaseFirestore.getInstance()

        val data = HashMap<String, Any>()

        data["ImageUrl"] = download
                db.collection("ImageUrls").document(documentId)
                        .set(data)
                        .addOnSuccessListener {
                            Toast.makeText(context, "Bilde lagret", Toast.LENGTH_LONG).show()
                        }
                        .addOnFailureListener{
                            Toast.makeText(context, "Kunne ikke lagre bildet", Toast.LENGTH_LONG).show()
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
    private fun saveToDB() {
        val data: MutableMap<String, Any> = HashMap()
        val date = "$day.$month.$year"


        data["Customer"] = customer
        data["Type"] = type
        data["Date"] = date
        data["Description"] = desc
        data["NotificationID"] = cancelNotify
        data["Form"] = formOpen

        db.collection("Saker")
            .document(documentId)
            .set(data)
            .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot added with ID: $documentId") }
            .addOnFailureListener { e -> Log.w(TAG, "Error adding document", e) }
    }
    private fun getNotifyData() {
        val ref = db.collection("Saker").document(documentId)

        ref.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                cancelNotify = snapshot.get("NotificationID").toString()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun scheduleNotification(){
        val date = "$day.$month.$year"
        val intent = Intent(context, BroadcastReceiver::class.java)
        intent.putExtra("title", customer)
        intent.putExtra("text", type)
        intent.putExtra("notifyId", cancelNotify)
        intent.putExtra("date", date)
        val pending = PendingIntent.getBroadcast(context, cancelNotify.toInt(), intent, PendingIntent.FLAG_UPDATE_CURRENT)
        // Schdedule notification
        val calendar: Calendar = Calendar.getInstance()
            calendar.set(year.toInt(), month.toInt() - 1, day.toInt(), 12, 0, 0)
            val time = calendar.timeInMillis
            Toast.makeText(context, "Varsling satt $year.$month.$day", Toast.LENGTH_LONG).show()
            val manager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
            manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time, pending)
    }
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.app_name)
            val descriptionText = getString(R.string.app_name)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                activity?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun cancelNotification() {
        val intent = Intent(context, BroadcastReceiver::class.java)
        intent.putExtra("title", customer)
        intent.putExtra("text", type)
        val pending = PendingIntent.getBroadcast(context, cancelNotify.toInt(), intent, PendingIntent.FLAG_UPDATE_CURRENT)
        // Cancel notification
        val manager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        manager.cancel(pending)
    }
}
