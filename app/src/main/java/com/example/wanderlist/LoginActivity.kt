package com.example.wanderlist

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.wanderlist.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        if (auth.currentUser != null) {
            goToMain()
            return
        }

        binding.btnLogin.setOnClickListener {
            login()
        }

        binding.tvGoToSignup.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }
    }

    private fun login() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Upiši email i lozinku", Toast.LENGTH_SHORT).show()
            return
        }

        setLoading(true)

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                setLoading(false)
                if (task.isSuccessful) {
                    goToMain()
                } else {
                    Toast.makeText(
                        this,
                        "Prijava neuspješna: ${task.exception?.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }

    private fun setLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnLogin.isEnabled = !isLoading
        binding.btnLogin.text = if (isLoading) "" else "Prijavi se"
        binding.tvGoToSignup.isEnabled = !isLoading
    }

    private fun goToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}