package com.example.brannvarsling

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.brannvarsling.databinding.ActivityLoginBinding
import com.example.brannvarsling.extensions.Extensions.toast
import com.example.brannvarsling.utils.FirebaseUtils.firebaseAuth


class LogInActivity : AppCompatActivity() {
        lateinit var logInEmail: String
        lateinit var logInPassword: String
        lateinit var logInInputsArray: Array<EditText>
        private lateinit var binding: ActivityLoginBinding


        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            binding = DataBindingUtil.setContentView(this,R.layout.activity_login)



            binding.registrerButton.setOnClickListener {
                startActivity(Intent(this, SignUpActivity::class.java))
            }

            logInInputsArray = arrayOf(binding.logInemail, binding.logInpassword)
            binding.registrerButton.setOnClickListener {
                startActivity(Intent(this, SignUpActivity::class.java))
                finish()
            }

            binding.loginButton.setOnClickListener {
                signInUser()
            }
        }

        private fun notEmpty(): Boolean = logInEmail.isNotEmpty() && logInPassword.isNotEmpty()

        private fun signInUser() {
            logInEmail = binding.logInemail.text.toString().trim()
            logInPassword = binding.logInpassword.text.toString().trim()

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



