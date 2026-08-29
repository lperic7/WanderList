package com.example.wanderlist

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.wanderlist.databinding.ActivitySignupBinding
import com.google.firebase.auth.FirebaseAuth

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.btnSignup.setOnClickListener {
            signup()
        }

        binding.tvGoToLogin.setOnClickListener {
            finish()
        }
    }

    private fun signup() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        val confirmPassword = binding.etConfirmPassword.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Upiši email i lozinku", Toast.LENGTH_SHORT).show()
            return
        }

        if (password != confirmPassword) {
            Toast.makeText(this, "Lozinke se ne podudaraju", Toast.LENGTH_SHORT).show()
            return
        }

        if (password.length < 6) {
            Toast.makeText(this, "Lozinka mora imati barem 6 znakova", Toast.LENGTH_SHORT).show()
            return
        }

        setLoading(true)

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                setLoading(false)
                if (task.isSuccessful) {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(
                        this,
                        "Registracija neuspješna: ${task.exception?.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }

    private fun setLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnSignup.isEnabled = !isLoading
        binding.btnSignup.text = if (isLoading) "" else "Registriraj se"
        binding.tvGoToLogin.isEnabled = !isLoading
    }
}