package com.example.brannvarsling

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.brannvarsling.databinding.ActivityMainBinding
import com.example.brannvarsling.extensions.Extensions.toast
import com.example.brannvarsling.fragments.Home
import com.example.brannvarsling.fragments.Notifications
import com.example.brannvarsling.utils.FirebaseUtils.firebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_main)
        setSupportActionBar(binding.toolbar)

        val fragmentHome = Home()
        val fragmentNotification = Notifications()

        binding.bottomNavigator.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.ic_home -> setCurrentFragment(fragmentHome)
                R.id.ic_notification -> setCurrentFragment(fragmentNotification)
            }
            true
        }
        updateCount()
    }
    private fun setCurrentFragment(fragment: Fragment)=
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.my_nav_host_fragment,fragment)
            updateCount()
            commit()
        }
    private fun signOut(){
            firebaseAuth.signOut()
            startActivity(Intent(this, LogInActivity::class.java))
            toast("Du er nå logget ut")
            finish()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
         when (item.itemId) {
            R.id.action_logout -> signOut()
        }
        return true
    }

    private fun updateCount(){
        binding.bottomNavigator.getOrCreateBadge(R.id.ic_notification).apply {
            val ref = FirebaseFirestore.getInstance().collection("Notifications")
            ref.get().addOnSuccessListener {
                val size = it.size()
                number = size
                backgroundColor = Color.CYAN
                badgeTextColor = Color.BLACK
                maxCharacterCount = 99
                isVisible = size != 0
            }
        }
    }
}

