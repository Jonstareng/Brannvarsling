package com.example.brannvarsling.fragments

import android.app.AlertDialog
import android.graphics.Color
import android.icu.util.Calendar.SATURDAY
import android.icu.util.Calendar.SUNDAY
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.chintanpatel.materialeventcalendar.CalenderView
import com.chintanpatel.materialeventcalendar.EventItem
import com.example.brannvarsling.R
import com.example.brannvarsling.databinding.FragmentCalenderBinding
import com.google.firebase.database.collection.LLRBNode
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
                    CalenderView.CalenderEventClickListener {
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