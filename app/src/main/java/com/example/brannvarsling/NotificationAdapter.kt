package com.example.brannvarsling

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.brannvarsling.dataClass.FirebaseCases
import com.example.brannvarsling.fragments.Notifications
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions


class NotificationAdapter(options: FirestoreRecyclerOptions<FirebaseCases>, private val listener: Notifications) :
    FirestoreRecyclerAdapter<FirebaseCases, NotificationAdapter.ViewHolder>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationAdapter.ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_item, parent, false))
    }
    override fun onBindViewHolder(holder: NotificationAdapter.ViewHolder, pos: Int, data: FirebaseCases) {
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
                listener.onItemClick(positionId)
            }
        }
    }
    interface onItemClickListner{
        fun onItemClick(id: String)
    }




}