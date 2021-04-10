package com.example.brannvarsling

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.brannvarsling.dataClass.FirebaseCases
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions


class RecyclerviewAdapter(options: FirestoreRecyclerOptions<FirebaseCases>, private val listner: OnItemClickListner) :
        FirestoreRecyclerAdapter<FirebaseCases, RecyclerviewAdapter.ViewHolder>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, p1: Int, data: FirebaseCases) {
        holder.tittel.text = data.Customer
        holder.type.text = data.Type

    }
     inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
       var tittel: TextView = itemView.findViewById(R.id.recyclerview_item_title)
         var type: TextView = itemView.findViewById(R.id.recyclerview_item_type)

         init {
             itemView.setOnClickListener(this)
         }

         override fun onClick(v: View?) {
             val position = adapterPosition
             val pos = snapshots.getSnapshot(adapterPosition)
             val positionId = pos.id
             if (position != RecyclerView.NO_POSITION){
                 listner.onItemClick(positionId)
             }

         }

     }
    interface OnItemClickListner{
        fun onItemClick(id: String)
    }


}