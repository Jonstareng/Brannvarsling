package com.example.brannvarsling

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.brannvarsling.dataClass.FirebaseCases
import com.example.brannvarsling.dataClass.FirebaseNotification
import com.example.brannvarsling.fragments.Notifications
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions


class NotificationAdapter(options: FirestoreRecyclerOptions<FirebaseNotification>, private val listener: OnItemClickListnerN) :
    FirestoreRecyclerAdapter<FirebaseNotification, NotificationAdapter.ViewHolder>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationAdapter.ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_notification_item, parent, false))
    }
    override fun onBindViewHolder(holder: NotificationAdapter.ViewHolder, pos: Int, data: FirebaseNotification) {
        holder.tittel.text = data.Customer
        holder.date.text = data.Date
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        var tittel: TextView = itemView.findViewById(R.id.notify_title)
        var date: TextView = itemView.findViewById(R.id.notify_date)


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
     fun swipeDelete(position: Int){
        snapshots.getSnapshot(position).reference.delete()
    }
    interface OnItemClickListnerN{
        fun onItemClick(id: String)
    }




}