package com.example.brannvarsling.Fragments

import android.R.layout.simple_spinner_item
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import androidx.databinding.DataBindingUtil
import androidx.databinding.DataBindingUtil.setContentView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.brannvarsling.CasesModel
import com.example.brannvarsling.R
import com.example.brannvarsling.databinding.FragmentCasesBinding
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions


class Cases: Fragment() {
    private lateinit var binding: FragmentCasesBinding
    private lateinit var adapter: CaseAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_cases, container, false)
        return binding.root
    }

    // Inflate the layout for this fragment
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }


            private fun spinnerMenu() {
                val caseChoice = arrayOf("Brannvarsling", "Nødlys")
                val arrayAdapter = context?.let { ArrayAdapter(it, simple_spinner_item, caseChoice) }
                binding.spinner.adapter = arrayAdapter
                binding.spinner.onItemSelectedListener = object :
                        AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                            val text: String = parent?.getItemAtPosition(position).toString()



            }

                    override fun onNothingSelected(parent: AdapterView<*>?) {

                    }
                }


    }


    // Firestore recycleradapter
    class CaseAdapter(options: FirestoreRecyclerOptions<CasesModel>) :
            FirestoreRecyclerAdapter<CasesModel, CaseAdapter.CaseAdapterViewHolder>(options) {


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CaseAdapterViewHolder {
            return CaseAdapterViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_item, parent, false))

        }

        override fun onBindViewHolder(holder: CaseAdapterViewHolder, position: Int, model: CasesModel) {

        }

        class CaseAdapterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            // val Type = itemView(R.id.recyclerview_item_tittel)

        }

    }


}
