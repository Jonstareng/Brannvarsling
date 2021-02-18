package com.example.brannvarsling

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.brannvarsling.extensions.Extensions.toast
import com.example.brannvarsling.utils.FirebaseUtils.firebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LogInActivity : AppCompatActivity() {
    lateinit var logInEmail: String
    lateinit var logInPassword: String
    lateinit var logInInputsArray: Array<EditText>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        logInInputsArray = arrayOf(logInemail, logInpassword)
        registrerButton.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
            finish()
        }

        loginButton.setOnClickListener {
            signInUser()
        }
    }

    private fun notEmpty(): Boolean = logInEmail.isNotEmpty() && logInPassword.isNotEmpty()

    private fun signInUser() {
        logInEmail = logInemail.text.toString().trim()
        logInPassword = logInpassword.text.toString().trim()

        if (notEmpty()) {
            firebaseAuth.signInWithEmailAndPassword(logInEmail, logInPassword)
                .addOnCompleteListener { signIn ->
                    if (signIn.isSuccessful) {
                        startActivity(Intent(this, MainActivity::class.java))
                        toast("Innlogging vellykket!")
                        finish()
                    } else {
                        toast("Innlogging mislykket")
                    }
                }
        } else {
            logInInputsArray.forEach { input ->
                if (input.text.toString().trim().isEmpty()) {
                    input.error = "${input.hint} er nødvendig"
                }
            }
        }
    }
}


