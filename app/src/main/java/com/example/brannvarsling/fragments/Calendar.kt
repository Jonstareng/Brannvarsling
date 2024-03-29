package com.example.brannvarsling.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.chintanpatel.materialeventcalendar.CalenderView
import com.chintanpatel.materialeventcalendar.CalenderView.*
import com.chintanpatel.materialeventcalendar.EventItem
import com.example.brannvarsling.R
import com.example.brannvarsling.databinding.FragmentCalenderBinding
import com.google.firebase.firestore.FirebaseFirestore


class Calendar: Fragment(){
    private lateinit var binding: FragmentCalenderBinding
    private val list: ArrayList<EventItem> = arrayListOf()
    private val listDb: ArrayList<String> = arrayListOf()
    private val listDn: ArrayList<String> = arrayListOf()
    private val listK: ArrayList<String> = arrayListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_calender, container, false)
        return binding.root
        // Inflate the layout for this fragment
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupCalendar()


    }
    /*
    //Henter ut data fra databasen, legger det i liste og legger dataen inn i kalenderen,
    // siden eventkalenderen er designet for å strekke seg over flere datoer trenger man to datoer for hvert eventitem
    // Derfor henter vi ut Date og DateNext som er dagen etter,
    // da vil bare første dagen vises i kalenderen og vi får oppsettet som vi ønsker
    */
    private fun setupCalendar() {
        val db = FirebaseFirestore.getInstance().collection("Saker")

        if (db.path.isNotEmpty())
            db.get().addOnSuccessListener {
                it.forEach { each ->
                    val date = each["Date"].toString()
                    val dateNext = each["DateNext"].toString()
                    val kunde = each["Customer"].toString()
                    if (date != "") {
                        listDb.add(date)
                        listDn.add(dateNext)
                        listK.add(kunde)

                    }
                }
                    for (i in 0 until listDb.size) {
                        list.add(EventItem(listDb[i], listDn[i], listK[i]))
                    }


                binding.calendarView.setCalenderEventClickListener(object :
                        CalenderEventClickListener {
                    override fun onEventClick(eventItem: EventItem) {
                        val id = eventItem.title
                        val builder = AlertDialog.Builder(activity)
                        builder.setTitle(id)
                                .setMessage("Varsel vil bli sendt ${eventItem.start}, du kan sette ny dato for varsling inne på saker siden.")
                                .setCancelable(false)
                                .setNegativeButton("Avbryt") { dialog, _ ->
                                    dialog.dismiss()
                                }
                        val alert = builder.create()
                        alert.show()
                    }
                })
                binding.calendarView.addEventList(list)

            }
    }

}