package com.example.brannvarsling

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.brannvarsling.databinding.ActivitySignUpBinding
import com.example.brannvarsling.extensions.Extensions.toast
import com.example.brannvarsling.utils.FirebaseUtils.firebaseAuth
import com.example.brannvarsling.utils.FirebaseUtils.firebaseUser


class SignUpActivity : AppCompatActivity() {
    private lateinit var userEmail: String
    private lateinit var userPassword: String
    private lateinit var createAccountInputsArray: Array<EditText>
    lateinit var binding: ActivitySignUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_sign_up)
        createAccountInputsArray = arrayOf(binding.Email, binding.Password, binding.bekreftPassword)

        binding.OpprettBruker.setOnClickListener {
            signIn()
        }

    }

    // simpel override funksjon for å gå tilbake til login når man trykker på pil på mobilen
    override fun onBackPressed() {
        startActivity(Intent(this, LogInActivity::class.java))
    }

    // Enkel validering som sjekker om tekstfelt er fylt ut eller ikke.
    // Gir error beskjed om ikke fylt ut.
    private fun notEmpty(): Boolean = binding.Email.text.toString().trim().isNotEmpty() &&
            binding.Password.text.toString().trim().isNotEmpty() &&
            binding.bekreftPassword.text.toString().trim().isNotEmpty()

    // Hvis passord ikke er like, gi feilmelding
    private fun identicalPassword(): Boolean {
        var identical = false
        if (notEmpty() && binding.Password.text.toString().trim() == binding.bekreftPassword.text.toString().trim()
        ) {
            identical = true
        } else if (!notEmpty()) {
            createAccountInputsArray.forEach { input ->
                if (input.text.toString().trim().isEmpty()) {
                    input.error = "${input.hint} er nødvendig"
                }
            }
        } else {
            toast("Passordene matcher ikke!", Toast.LENGTH_LONG)
        }
        return identical
    }

    // Funksjon for å logge inn bruker med registrert email og passord
    private fun signIn() {
        if (identicalPassword()) {
            userEmail = binding.Email.text.toString().trim()
            userPassword = binding.Password.text.toString().trim()

            // Opprett bruker
            firebaseAuth.createUserWithEmailAndPassword(userEmail, userPassword)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        toast("Vellykket oppretting av bruker!", Toast.LENGTH_LONG)
                        sendEmailVerification()
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    } else {
                        toast("Autentisering feilet!", Toast.LENGTH_LONG)
                    }
                }
        }
    }

    // Når du har en vellykket registrering får du en mail fra firebase
    // om å validere emailen din.
    private fun sendEmailVerification() {
        firebaseUser?.let {
            it.sendEmailVerification().addOnCompleteListener { task ->
                if(task.isSuccessful) {
                    toast("Email sendt til $userEmail", Toast.LENGTH_LONG)
                }
            }
        }
    }
}
