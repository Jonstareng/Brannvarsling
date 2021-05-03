package com.example.brannvarsling

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.brannvarsling.databinding.ActivityLoginBinding
import com.example.brannvarsling.extensions.Extensions.toast
import com.example.brannvarsling.utils.FirebaseUtils.firebaseAuth
import com.google.firebase.auth.FirebaseUser


class LogInActivity : AppCompatActivity() {
    private lateinit var logInEmail: String
    private lateinit var logInPassword: String
    private lateinit var logInInputsArray: Array<EditText>
    private lateinit var binding: ActivityLoginBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_login)

        logInInputsArray = arrayOf(binding.logInemail, binding.logInpassword)
        binding.registrerButton.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
            finish()
        }

        binding.loginButton.setOnClickListener {
            signInUser()
        }
    }

    // Gjenkjenner bruker om han er registrert og ønsker velkommen tilbake samtidig som bruker
    // blir sendt til Home siden.
    override fun onStart() {
        super.onStart()
        val user: FirebaseUser? = firebaseAuth.currentUser
        user?.let {
            startActivity(Intent(this, MainActivity::class.java))
            toast("Velkommen tilbake! :)", Toast.LENGTH_LONG)
        }
    }

    private fun notEmpty(): Boolean = logInEmail.isNotEmpty() && logInPassword.isNotEmpty()

    // Logger inn bruker
    private fun signInUser() {
        logInEmail = binding.logInemail.text.toString().trim()
        logInPassword = binding.logInpassword.text.toString().trim()

        if (notEmpty()) {
            firebaseAuth.signInWithEmailAndPassword(logInEmail, logInPassword)
                    .addOnCompleteListener { signIn ->
                        if (signIn.isSuccessful) {
                            startActivity(Intent(this, MainActivity::class.java))
                            toast("Innlogging vellykket!", Toast.LENGTH_LONG)
                            finish()
                        } else {
                            toast("Innlogging mislykket", Toast.LENGTH_LONG)
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



