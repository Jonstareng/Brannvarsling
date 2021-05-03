package com.example.brannvarsling

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.brannvarsling.databinding.ActivityMainBinding
import com.example.brannvarsling.dialogFragment.AlertDelete
import com.example.brannvarsling.extensions.Extensions.toast
import com.example.brannvarsling.fragments.Home
import com.example.brannvarsling.fragments.Notifications
import com.example.brannvarsling.utils.FirebaseUtils.firebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_main)
        setSupportActionBar(binding.toolbar)
        // deklarerer de andre fregmentene
        val fragmentHome = Home()
        val fragmentNotification = Notifications()
        // kaller på setCurrentfragment og bytter fragment
        binding.bottomNavigator.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.ic_home -> setCurrentFragment(fragmentHome)
                R.id.ic_notification -> setCurrentFragment(fragmentNotification)
            }
            true
        }
        updateCount()
    }
    // En funksjon som blir kalt på når knappene i bottomnav blir trykket på
    private fun setCurrentFragment(fragment: Fragment)=
        supportFragmentManager.beginTransaction().apply {
            // bytter fragment med parameter fragmentet
            replace(R.id.my_nav_host_fragment,fragment)
            updateCount()
            commit()
        }
    // logger brukeren ut av appen
    private fun signOut(){
            firebaseAuth.signOut()
            startActivity(Intent(this, LogInActivity::class.java))
            toast("Du er nå logget ut")
            finish()
    }
    // inflater top menyen
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }
    // setter aksjonen på om item action_logout blir valgt, kaller på signOut funksjoen
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
         when (item.itemId) {
            R.id.action_logout -> signOut()
             R.id.action_slett_skjema -> deleteForm()
        }
        return true
    }
    private  fun deleteForm(){
        val dialogFragment = AlertDelete()
        dialogFragment.show(supportFragmentManager, "show")
    }


    // Denne klassen tar i bruk material som er et bibliotek som man kan bruke for å legge til et notifikasjons ikon på en drawable
    private fun updateCount(){
        binding.bottomNavigator.getOrCreateBadge(R.id.ic_notification).apply {
            val ref = FirebaseFirestore.getInstance().collection("Notifications")
            ref.get().addOnSuccessListener {
                // setter tallet som vises lik antall dokumenter vi finner i databasen
                val size = it.size()
                number = size
                backgroundColor = Color.CYAN
                badgeTextColor = Color.BLACK
                maxCharacterCount = 99
                // fjerner ikonet om det ikke er noen notifikasjoner
                isVisible = size != 0
            }
        }
    }
}

