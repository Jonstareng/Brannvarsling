package com.example.brannvarsling

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.brannvarsling.extensions.Extensions.toast
import com.example.brannvarsling.utils.FirebaseUtils.firebaseAuth
import com.example.brannvarsling.utils.FirebaseUtils.firebaseUser
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_sign_up.*


class SignUpActivity : AppCompatActivity() {
    lateinit var userEmail: String
    lateinit var userPassword: String
    lateinit var createAccountInputsArray: Array<EditText>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        createAccountInputsArray = arrayOf(Email, Password, bekreftPassword)

        OpprettBruker.setOnClickListener {
            startActivity(Intent(this, LogInActivity::class.java))
            signIn()
        }

    }

    override fun onStart() {
        super.onStart()
        val user: FirebaseUser? = firebaseAuth.currentUser
        user?.let {
            startActivity(Intent(this, MainActivity::class.java))
            toast("Velkommen tilbake! :)")
        }
    }

    private fun notEmpty(): Boolean = Email.text.toString().trim().isNotEmpty() &&
            Password.text.toString().trim().isNotEmpty() &&
            bekreftPassword.text.toString().trim().isNotEmpty()

    private fun identicalPassword(): Boolean {
        var identical = false
        if (notEmpty() && Password.text.toString().trim() == bekreftPassword.text.toString().trim()
        ) {
            identical = true
        } else if (!notEmpty()) {
            createAccountInputsArray.forEach { input ->
                if (input.text.toString().trim().isEmpty()) {
                    input.error = "${input.hint} er nødvendig"
                }
            }
        } else {
            toast("Passordene matcher ikke!")
        }
        return identical
    }

    private fun signIn() {
        if (identicalPassword()) {
            userEmail = Email.text.toString().trim()
            userPassword = Password.text.toString().trim()

            // Opprett bruker
            firebaseAuth.createUserWithEmailAndPassword(userEmail, userPassword)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        toast("Vellykket oppretting av bruker!")
                        sendEmailVerification()
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    } else {
                        toast("Autentisering feilet!")
                    }
                }
        }
    }

    private fun sendEmailVerification() {
        firebaseUser?.let {
            it.sendEmailVerification().addOnCompleteListener { task ->
                if(task.isSuccessful) {
                    toast(msg = "Email sendt til $userEmail")
                }
            }
        }
    }
}
