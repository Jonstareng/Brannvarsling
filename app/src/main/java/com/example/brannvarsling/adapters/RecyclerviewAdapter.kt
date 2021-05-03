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
    // setter verdien som skal vises i recyclerviewet, i tillegg til å endre bilde til check sånn av det ser ut som man avhuker boksen
    override fun onBindViewHolder(holder: ViewHolder, p1: Int, data: FirebaseCases) {
        holder.tittel.text = data.Customer
        holder.type.text = data.Type
        holder.date.text = data.Date
        // sjekker om det er en verdi i databasen, setter riktig bilde utifra dataene i databasen
        val ref = FirebaseFirestore.getInstance().collection("Saker").document(snapshots.getSnapshot(p1).id).collection("Check")
            ref.get().addOnSuccessListener {
               if(it.isEmpty) {
                   holder.image.setImageResource(R.drawable.check_box_outline_blank_black_24dp)
               }
                else
                   holder.image.setImageResource(R.drawable.check_box_black_24dp)
            }

    }
    // setter opp verdiene som skal sendes med onItemClick funksjonen som brukes i Notifikasjons klassen.

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
       var tittel: TextView = itemView.findViewById(R.id.recyclerview_item_title)
         var type: TextView = itemView.findViewById(R.id.recyclerview_item_type)
         var date: TextView = itemView.findViewById(R.id.recyclerview_item_date)
         var image: ImageView = itemView.findViewById(R.id.recyclerview_item_checkbox)
         var imageDone: ImageView = itemView.findViewById(R.id.recyclerview_item_checkbox_fill)
         var imageDoneB: ImageView = itemView.findViewById(R.id.recyclerview_item_checkbox_backup)


        // Får alle 3 knappene til å utføre showBox funksjonen
        // Grunnen til at alle 3 gjør det samme er fordi vi bytter på hvilke imageviews som vises.
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
            // Grunnen for at vi har en tredje knapp er fordi det gikk ikke å bytte tilbake til det bildet som var etter at man først hadde byttet
            // dette er fordi vi endrer verdien på image utifra dataen i databasen
             imageDoneB.setOnClickListener {
                 val id = snapshots.getSnapshot(adapterPosition).id
                 showBox(image, imageDone, imageDoneB, id)
             }
         }

        // setter opp verdiene som skal sendes med onItemClick funksjonen som brukes i Cases klassen.
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
    // setter opp onItemClick så den kan hentes ut i en annen klasse
    interface OnItemClickListener{
        fun onItemClick(id: String, customer: CharSequence)
    }
    // funksjonen sletter eller legger til data i databasen for at jeg kan sjekke om det ligger verdier der.
    // Målet med det er å kunne vise riktig bilde i recyclerviewet
    // Vi bytter også på 3 bilder som vises for at brukeren faktisk huker av boksen
    private fun showBox(image: ImageView, imageDone: ImageView, imageDoneB: ImageView, id: String) {
        val ref = FirebaseFirestore.getInstance().collection("Saker").document(id).collection("Check")
        ref.get().addOnSuccessListener {
            if (it.isEmpty) {
                // bytter hvilket bilde som vises
                image.visibility = View.INVISIBLE
                imageDone.visibility = View.VISIBLE
                // skriver data til databasen
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
                // bytter til bilde nr 3 som vises siden det ikke gikk å vise bilde nr 1 igjen etter første trykk
                image.visibility = View.INVISIBLE
                imageDone.visibility = View.INVISIBLE
                imageDoneB.visibility = View.VISIBLE
                // sletter data i databasen
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

