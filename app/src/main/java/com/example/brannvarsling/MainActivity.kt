package com.example.brannvarsling

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.fragment.NavHostFragment
import com.example.brannvarsling.Fragments.Calendar
import com.example.brannvarsling.Fragments.Cases
import com.example.brannvarsling.Fragments.Home
import com.example.brannvarsling.databinding.ActivityMainBinding
import com.example.brannvarsling.extensions.Extensions.toast
import com.example.brannvarsling.utils.FirebaseUtils.firebaseAuth

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_main)
        setSupportActionBar(binding.toolbar)
        setCurrentFragment(Home())
        val homeFragment = Home()
        val casesFragment = Cases()
        val calendarFragment = Calendar()


        binding.bottomNavigator.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.ic_home->setCurrentFragment(homeFragment)
                R.id.ic_assignment->setCurrentFragment(casesFragment)
                R.id.ic_calendar->setCurrentFragment(calendarFragment)
            }
            true
        }

        binding.signOut.setOnClickListener {
            firebaseAuth.signOut()
            startActivity(Intent(this, LogInActivity::class.java))
            toast("Du er nå logget ut")
            finish()
    }

    }

    private fun setCurrentFragment(fragment: Fragment)=
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.frameLayout,fragment)
            commit()
        }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {

            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}