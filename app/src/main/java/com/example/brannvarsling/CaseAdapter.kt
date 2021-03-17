package com.example.brannvarsling

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class CaseAdapter(options: FirestoreRecyclerOptions<CasesModel>) :
        FirestoreRecyclerAdapter<CasesModel, CaseAdapter.CaseAdapterViewHolder>(options){

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CaseAdapterViewHolder {
            return CaseAdapterViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_item, parent, false))

        }

        override fun onBindViewHolder(holder: CaseAdapterViewHolder, position: Int, model: CasesModel) {

        }

        class CaseAdapterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


        }
        }
