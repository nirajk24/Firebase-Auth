package com.affinity.repsoft

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import com.affinity.repsoft.databinding.ActivityPasswordBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class PasswordActivity : AppCompatActivity() {

    private lateinit var binding : ActivityPasswordBinding
    private lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        binding.btnBack.setOnClickListener {
            onBackPressed()
        }

        binding.btnSignIn.setOnClickListener {
            if(checkAllFields()){
                val email = binding.etEmail.text.toString().trim()

                // Disable the button and show the ProgressBar
                binding.btnSignIn.isEnabled = false
                binding.btnSignIn.text = ""
                binding.loadingSpinner.visibility = View.VISIBLE

                auth.sendPasswordResetEmail(email).addOnCompleteListener {
                    if(it.isSuccessful){
                        Toast.makeText(this, "Email Sent!!", Toast.LENGTH_SHORT).show()

                        onBackPressed()
                    } else {
                        // Enable the button and hide the ProgressBar
                        binding.btnSignIn.isEnabled = true
                        binding.btnSignIn.text = "Done"
                        binding.loadingSpinner.visibility = View.GONE

                        Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }



    private fun checkAllFields() : Boolean{
        val email = binding.etEmail.text.toString()
        if(binding.etEmail.text.toString().isEmpty()) {
            Toast.makeText(this, "Email is Required!!", Toast.LENGTH_SHORT).show()
            return false
        } else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Toast.makeText(this, "Enter a Valid Email!!", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }
}