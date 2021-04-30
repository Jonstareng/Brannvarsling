package com.example.brannvarsling.fragments

import android.icu.util.Calendar.SATURDAY
import android.icu.util.Calendar.SUNDAY
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.events.calendar.utils.EventsCalendarUtil.today
import com.events.calendar.views.EventsCalendar
import com.example.brannvarsling.R
import com.example.brannvarsling.databinding.FragmentCalenderBinding

 class Calendar: Fragment(), EventsCalendar.Callback {
    private lateinit var binding: FragmentCalenderBinding

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
    override fun onDayLongPressed(selectedDate: java.util.Calendar?) {
        Log.e("LONG", "CLICKED")
    }

    override fun onMonthChanged(monthStartDate: java.util.Calendar?) {
        Log.e("MON", "CHANGED")
    }

    override fun onDaySelected(selectedDate: java.util.Calendar?) {
        Log.e("SHORT", "CLICKED")
    }
    private fun setupCalendar(){
        val eventsCalendar: EventsCalendar? = null
        eventsCalendar?.setSelectionMode(eventsCalendar.MULTIPLE_SELECTION)
            ?.setToday(today)
            ?.setMonthRange(eventsCalendar.mMinMonth, eventsCalendar.mMaxMonth)
            ?.build()

    }

}