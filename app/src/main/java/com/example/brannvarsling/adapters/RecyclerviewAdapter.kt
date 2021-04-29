package com.example.brannvarsling.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.brannvarsling.R
import com.example.brannvarsling.dataClass.FirebaseCases
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore


class RecyclerviewAdapter(options: FirestoreRecyclerOptions<FirebaseCases>, private val listener: OnItemClickListener) :
        FirestoreRecyclerAdapter<FirebaseCases, RecyclerviewAdapter.ViewHolder>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, p1: Int, data: FirebaseCases) {
        holder.tittel.text = data.Customer
        holder.type.text = data.Type
        holder.date.text = data.Date
        val ref = FirebaseFirestore.getInstance().collection("Saker").document(snapshots.getSnapshot(p1).id).collection("Check")
            ref.get().addOnSuccessListener {
               if(it.isEmpty) {
                   holder.image.setImageResource(R.drawable.check_box_outline_blank_black_24dp)
               }
                else
                   holder.image.setImageResource(R.drawable.check_box_black_24dp)
            }

    }
     inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
       var tittel: TextView = itemView.findViewById(R.id.recyclerview_item_title)
         var type: TextView = itemView.findViewById(R.id.recyclerview_item_type)
         var date: TextView = itemView.findViewById(R.id.recyclerview_item_date)
         var image: ImageView = itemView.findViewById(R.id.recyclerview_item_checkbox)
         var imageDone: ImageView = itemView.findViewById(R.id.recyclerview_item_checkbox_fill)
         var imageDoneB: ImageView = itemView.findViewById(R.id.recyclerview_item_checkbox_backup)



         init {
             itemView.setOnClickListener(this)
             image.setOnClickListener {
                 val id = snapshots.getSnapshot(adapterPosition).id
                 showBox(image, imageDone, imageDoneB, id)
             }
             imageDone.setOnClickListener {
                 val id = snapshots.getSnapshot(adapterPosition).id
                 showBox(image, imageDone, imageDoneB, id)
             }
             imageDoneB.setOnClickListener {
                 val id = snapshots.getSnapshot(adapterPosition).id
                 showBox(image, imageDone, imageDoneB, id)
             }
         }


         override fun onClick(v: View?) {
             val position = adapterPosition
             val customer = tittel.text
             val pos = snapshots.getSnapshot(adapterPosition)
             val positionId = pos.id
             if (position != RecyclerView.NO_POSITION){
                 listener.onItemClick(positionId, customer)
             }
         }

     }
    interface OnItemClickListener{
        fun onItemClick(id: String, customer: CharSequence)
    }
    private fun showBox(image: ImageView, imageDone: ImageView, imageDoneB: ImageView, id: String) {
        val ref = FirebaseFirestore.getInstance().collection("Saker").document(id).collection("Check")
        ref.get().addOnSuccessListener {
            if (it.isEmpty) {
                image.visibility = View.INVISIBLE
                imageDone.visibility = View.VISIBLE
                val data: MutableMap<String, Any> = HashMap()
                val storageReference = FirebaseFirestore.getInstance()
                val refStorage =
                    storageReference.collection("Saker")
                        .document(id)
                        .collection("Check").document("document")
                data["Check"] = "Ja"
                refStorage.set(data)
            }
            else {
                image.visibility = View.INVISIBLE
                imageDone.visibility = View.INVISIBLE
                imageDoneB.visibility = View.VISIBLE
                val storageReference1 = FirebaseFirestore.getInstance()
                val refStorage =
                    storageReference1.collection("Saker")
                        .document(id)
                        .collection("Check").document("document")
                refStorage.delete()

            }
        }
    }

}

