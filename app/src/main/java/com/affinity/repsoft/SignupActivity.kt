package com.affinity.repsoft

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import com.affinity.repsoft.databinding.ActivityMainBinding
import com.affinity.repsoft.databinding.ActivitySignupBinding
import com.affinity.repsoft.repo.MyPreference
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SignupActivity : AppCompatActivity() {

    private lateinit var binding : ActivitySignupBinding
    private lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        binding.btnBack.setOnClickListener {
            onBackPressed()
        }

        binding.btnToSignIn.setOnClickListener {
            val intent = Intent(this, SigninActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent)
        }

        binding.btnSignUp.setOnClickListener {
            if (isInternetAvailable(this)) {
                if (checkAllField()) {
                    val email = binding.etEmail.text.toString().trim()
                    val pass = binding.etPass.text.toString().trim()

                    // Disable the button and show the ProgressBar
                    binding.btnSignUp.isEnabled = false
                    binding.btnSignUp.text = ""
                    binding.loadingSpinner.visibility = View.VISIBLE

                    auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(this) {
                        // If successful account is created
                        if (it.isSuccessful) {
                            auth.signInWithEmailAndPassword(email, pass)
                                .addOnCompleteListener { itt ->
                                    if (itt.isSuccessful) {
                                        // If successful
                                        Toast.makeText(
                                            this,
                                            "Successfully Signed In!!",
                                            Toast.LENGTH_SHORT
                                        ).show()

                                        // Go to another activity
                                        val intent = Intent(this, MainActivity::class.java)
                                        intent.flags =
                                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                                        startActivity(intent)
                                        finish()
                                    } else {
                                        Toast.makeText(
                                            this,
                                            itt.exception.toString(),
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                        } else {

                            // Enable the button and hide the ProgressBar
                            binding.btnSignUp.isEnabled = true
                            binding.btnSignUp.text = "Sign Up"
                            binding.loadingSpinner.visibility = View.GONE

                            Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } else {
                Toast.makeText(this, "No Internet Available!!", Toast.LENGTH_SHORT).show()
            }
        }
    }

        private fun checkAllField(): Boolean {
        val email = binding.etEmail.text.toString()
        if(binding.etName.text.toString().isEmpty()) {
            Toast.makeText(this, "Name is Required!!", Toast.LENGTH_SHORT).show()
            return false
        } else if(binding.etEmail.text.toString().isEmpty()) {
            Toast.makeText(this, "Email is Required!!", Toast.LENGTH_SHORT).show()
            return false
        } else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Toast.makeText(this, "Enter a Valid Email!!", Toast.LENGTH_SHORT).show()
            return false
        } else if(binding.etPass.text.isNullOrEmpty() or binding.etConfirmPass.text.isNullOrEmpty()){
            Toast.makeText(this, "Password is Required!!", Toast.LENGTH_SHORT).show()
            return false
        } else if(binding.etPass.text.toString().trim().length < 6) {
            Toast.makeText(this, "Password is Short!!", Toast.LENGTH_SHORT).show()
            return false
        } else if(binding.etPass.text.toString().trim() != binding.etConfirmPass.text.toString().trim()){
            Toast.makeText(this, "Password don't Match!!", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }



    fun isInternetAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val networkCapabilities = connectivityManager.activeNetwork ?: return false
            val activeNetwork =
                connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false

            return when {
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                else -> false
            }
        } else {
            val networkInfo = connectivityManager.activeNetworkInfo
            return networkInfo != null && networkInfo.isConnected
        }
    }
}