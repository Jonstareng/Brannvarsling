package com.example.brannvarsling

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth


class SignUpActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)


    }
}

    /*
    // Funksjon som oppretter bruker hvis vellykket og lagres i Firebase
    private fun registrerBruker() {
        val email = Email.text.toString()
        val password = Password.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Vennligst fyll inn email/passord", Toast.LENGTH_SHORT).show()
            return
        }

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (!it.isSuccessful) return@addOnCompleteListener

                // else if vellykket
                startActivity(Intent(this, LogInActivity::class.java))
                Log.d("Main", "Vellykket opprettelse med uid: ${it.result?.user?.uid}")

            }
            .addOnFailureListener {
                Log.d("Main", "Opprettelse av bruker feilet: ${it.message}")
                Toast.makeText(this, "Opprettelse av bruker feilet: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

}

class User(val uid: String, val Email: String, val Passord: String) {
    constructor() : this("", "", "")
}
     */