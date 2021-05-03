package com.example.brannvarsling.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.brannvarsling.R
import com.example.brannvarsling.dataClass.FirebaseNotification
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions


class NotificationAdapter(options: FirestoreRecyclerOptions<FirebaseNotification>, private val listener: OnItemClickListnerN) :
    FirestoreRecyclerAdapter<FirebaseNotification, NotificationAdapter.ViewHolder>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_notification_item, parent, false))
    }
    // setter verdien som skal vises i recyclerviewet
    override fun onBindViewHolder(holder: ViewHolder, pos: Int, data: FirebaseNotification) {
        holder.tittel.text = data.Customer
        holder.date.text = data.Date
    }
    //Henter views som skal vise en verdi i recyclerviewet
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        var tittel: TextView = itemView.findViewById(R.id.notify_title)
        var date: TextView = itemView.findViewById(R.id.notify_date)

        init {
            itemView.setOnClickListener(this)
        }
        // setter opp verdiene som skal sendes med onItemClick funksjonen som brukes i Notifikasjons klassen.
        override fun onClick(v: View?) {
            val position = adapterPosition
            val pos = snapshots.getSnapshot(adapterPosition)
            val positionId = pos.id
            if (position != RecyclerView.NO_POSITION){
                listener.onItemClick(positionId)
            }
        }
    }
    // Det var lettere å sette opp delete funksjonen i denne klassen og heller kalle på klassen i Notifications klassen'
    // så denne klassen fjerner daten fra databasen
     fun swipeDelete(position: Int){
        snapshots.getSnapshot(position).reference.delete()
    }
    // setter opp onclick så den kan brukes i andre klasser
    interface OnItemClickListnerN{
        fun onItemClick(id: String)
    }




}