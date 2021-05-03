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
import android.graphics.Matrix
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
import com.itextpdf.kernel.xmp.PdfConst.Date
import com.squareup.picasso.Picasso
import java.net.URI
import java.text.SimpleDateFormat
import java.util.*
import java.util.Collections.rotate
import kotlin.collections.HashMap


class RecyclerviewDialogFragment(id: String, private var customer: CharSequence) : DialogFragment() {

    private lateinit var binding: RecyclerdialogWindowBinding
    private var db = FirebaseFirestore.getInstance()
    private val documentId = id
    private var formOpen = ""
    private val pickImage = 100
    private var imageUrl: Uri? = null
    private var type = ""
    private var desc =""
    private var filePath: Uri? = null
    private var year = ""
    private var month = ""
    private var day = ""
    private var next = ""
    private var cancelNotify = ""
    private val channelID = "JensenNotifikasjoner"


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
        getImage()
        onCreateDialog(savedInstanceState)
        getData()
        createNotificationChannel()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        getNotifyData()
        getImage()
        // Sender deg tilbake til Cases siden
        binding.close.setOnClickListener {
            dismiss()
        }
        // åpner dialog vinduet for å dobbeltsjekka at man vil slette saken
        binding.deleteRecyclerItem.setOnClickListener {
            deleteDialog()
        }
        // åpnner vinduet hvor man kan sette varslings dato
        binding.saveDate.setOnClickListener {
            alertDialog()
        }
        // setter knappen til å åpne skjema
        binding.openForm.setOnClickListener {
            openForm()
        }
        // setter knappen til å åpne galleri
        binding.buttonVedlegg.setOnClickListener{
            openGallery()
        }
        binding.vedlegg.setOnClickListener{
            getImage()
            if (imageUrl != null)
            zoomImage(imageUrl!!)
        }
        binding.zoomImage.setOnClickListener {
            binding.zoomImage.visibility = View.INVISIBLE
        }
        return dialog
    }

    // Åpner galleriet og sender bilde som er valgt til onActivityResult
    private fun openGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), pickImage)
    }
    // Tar i mot bildet og laster det opp i imageviewt i klassen
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?){
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == pickImage && resultCode == Activity.RESULT_OK){

            filePath = data?.data
            imageUrl = data?.data

            Picasso.get().load(filePath).rotate(90f).into(binding.vedlegg)
            uploadImage()
        }
    }
    // laster opp bildet som er valgt til firebase storage
    private fun uploadImage() {
        val storageReference = FirebaseStorage.getInstance().reference
        val ref = storageReference.child("$customer.jpg")
        ref.putFile(filePath!!)
        Toast.makeText(context, "Bilde lagret", Toast.LENGTH_LONG).show()

    }
    //Henter ut bildet fra storage om det er der
    private fun getImage() {
        val storageReference = FirebaseStorage.getInstance()
        val refStorage = storageReference.getReferenceFromUrl("gs://varslingssystem.appspot.com/$customer.jpg")
        if(refStorage.storage.reference.path.isNotEmpty())
        refStorage.downloadUrl.addOnSuccessListener { image ->
            Picasso.get().load(image).rotate(90F).into(binding.vedlegg)
            imageUrl = image
        }
    }
    // Gjør et annet imageview synlig og setter uri tildet samme som det lille bildet som vises
    private fun zoomImage(imageId: Uri){

        val bigImage = binding.zoomImage
        Picasso.get().load(imageId).rotate(90f).into(bigImage)
        bigImage.visibility = View.VISIBLE

    }
    // Henter ut data fra databasen og setter de inn i riktig textviews
    private fun getData() {
        val docRef = db.collection("Saker").document(documentId)

        docRef.get().addOnSuccessListener { documentSnapshot ->
            val data = documentSnapshot.toObject(DialogFragmentItems::class.java)
            binding.displayCustomer.text = data?.Customer
            binding.displayType.text = data?.Type
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
    // sletter alt som er lagret på en sak, bildet i storage og alt under saken
    private fun deleteItem() {
        val docRef = db.collection("Saker").document(documentId)
        val storageRef = FirebaseStorage.getInstance().getReference("$customer.jpg")
        storageRef.delete()
        docRef.delete()
        val reference = FirebaseFirestore.getInstance()
        val refStorage =
            reference.collection("Saker")
                .document(documentId)
                .collection("Check").document("document")
        refStorage.delete()
    }

    // åpner en alertdialog og kaller på deleteItem om man trykker på slett knappen
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

    //oppretter alerdialogen som setter varslingen, denne klassen kaller på tre andre klaser og kjører de om sett dato knappen blir trykket
    @RequiresApi(Build.VERSION_CODES.M)
    private fun alertDialog() {

        val layout = layoutInflater.inflate(R.layout.alertdate_window, null)

        val builder = AlertDialog.Builder(activity)
        builder.setView(layout)
                .setCancelable(false)
                .setTitle("Sett dato for varsling")
                .setPositiveButton("Sett dato") { _, _ ->
                    // sørger for at man ikke kan sette en dato som allerede har vært
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
        // Legger inn år i spinneren
        val caseChoiceY = arrayOf("2021", "2022", "2023", "2024", "2025", "2026", "2027", "2028", "2029", "2030")
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
        // legger inn alle månedene i en spinner
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
        // Legger alle dager i en måned inn i spinneren, har ikke funnet en god løsning på å få riktig dager til en måned
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

    // sender brukren inn i skjema siden, og sender verdiene formOpen og type til klassen
    private fun openForm() {
        val dialogFragment = FormDialogFragment(formOpen, type)

        val fragmentManager = activity?.supportFragmentManager
        val transaction = fragmentManager?.beginTransaction()

        transaction?.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        transaction?.add(android.R.id.content, dialogFragment)?.addToBackStack(null)?.commit()
    }

    // Lagrer dataen til databasen,
    //Siden man setter en verdi så vil all dataen som allerede er der bli overkjørt,
    // Derfor bruker vi de allerede uthenta verdiene fra databasen og setter de på nytt
    private fun saveToDB() {
        val data: MutableMap<String, Any> = HashMap()
        val date = "$day-$month-$year"
        next = (day.toInt() + 1).toString()
        // fordi vi skal øke dag med en for å få neste dag må vi sjekka om den er under 10,
        // for å legge til en null forran sånn at vi får riktig format
        val dateNext: String = if (next.toInt()<10) {
            "0$next-$month-$year"
        } else{
            "$next-$month-$year"
        }

        data["Customer"] = customer
        data["Type"] = type
        data["Date"] = date
        data["DateNext"] = dateNext
        data["Description"] = desc
        data["NotificationID"] = cancelNotify
        data["Form"] = formOpen

        db.collection("Saker")
            .document(documentId)
            .set(data)
            .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot added with ID: $documentId") }
            .addOnFailureListener { e -> Log.w(TAG, "Error adding document", e) }
    }
    // Henter ut notifikasjons iden som ble lagret til saken når man opprettet den
    private fun getNotifyData() {
        val ref = db.collection("Saker").document(documentId)

        ref.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                cancelNotify = snapshot.get("NotificationID").toString()
            }
        }
    }
    // Samme funksjon som i notifications, starter en alarm og setter den til datoen fra spinnerene
    @RequiresApi(Build.VERSION_CODES.M)
    private fun scheduleNotification(){
        val date = "$day-$month-$year"
        // sender intents til BroadcastReceiver
        val intent = Intent(context, BroadcastReceiver::class.java)
        intent.putExtra("title", customer)
        intent.putExtra("text",  type)
        intent.putExtra("notifyId", cancelNotify)
        intent.putExtra("date", date)
        val pending = PendingIntent.getBroadcast(context, cancelNotify.toInt(), intent, PendingIntent.FLAG_UPDATE_CURRENT)
        // Schdedule notification
        val calendar: Calendar = Calendar.getInstance()
        // setter calender verdien til verdiene fra spinnerene
            calendar.set(year.toInt(), month.toInt() - 1, day.toInt(), 12, 0, 0)
        val time = calendar.timeInMillis
            Toast.makeText(context, "Varsling satt $year.$month.$day", Toast.LENGTH_LONG).show()
            val manager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
            manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time, pending)
    }
    // Alle notifikasjoner må ha en kanal de blir sendt på,
    // Denne funksjonen oppretter da kanalen som alle notifikasjonene fra denne appen skal bli sendt på
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.app_name)
            val descriptionText = getString(R.string.app_name)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelID, name, importance).apply {
                description = descriptionText
            }
            // Registrerer canalen i systemet
            val notificationManager: NotificationManager =
                activity?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
    // canselerer en notifikasjon hvis det allerede er satt en,
    // denne funksjonen blir kalt før man setter en ny notifikasjon
    private fun cancelNotification() {
        val intent = Intent(context, BroadcastReceiver::class.java)
        intent.putExtra("title", customer)
        intent.putExtra("text", type)
        val pending = PendingIntent.getBroadcast(context, cancelNotify.toInt(), intent, PendingIntent.FLAG_UPDATE_CURRENT)
        val manager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        manager.cancel(pending)
    }
}
