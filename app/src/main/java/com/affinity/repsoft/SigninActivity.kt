package com.affinity.repsoft

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.affinity.repsoft.databinding.ActivitySigninBinding
import com.affinity.repsoft.repo.MyPreference
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SigninActivity : AppCompatActivity() {

    private lateinit var binding : ActivitySigninBinding
    private lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySigninBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        binding.rememberMeCheckbox.isChecked = MyPreference(this).isRemember

        binding.btnBack.setOnClickListener {
            onBackPressed()
        }

        binding.tvForgotPassword.setOnClickListener {
            val intent = Intent(this, PasswordActivity::class.java)
            startActivity(intent)
        }

        binding.btnToSignUp.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent)
        }

        binding.btnSignIn.setOnClickListener {
            if(isInternetAvailable(this)){
            if(checkAllFields()){
                val email = binding.etEmail.text.toString().trim()
                val pass = binding.etPass.text.toString().trim()

                // Disable the button and show the ProgressBar
                binding.btnSignIn.isEnabled = false
                binding.btnSignIn.text = ""
                binding.loadingSpinner.visibility = View.VISIBLE

                auth.signInWithEmailAndPassword(email, pass).addOnCompleteListener {
                    if(it.isSuccessful){
                        // If successful
                        Toast.makeText(this, "Successfully Signed In!!", Toast.LENGTH_SHORT).show()
                        MyPreference(this).isRemember = binding.rememberMeCheckbox.isChecked

                        // Go to another activity
                        val intent = Intent(this, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                        startActivity(intent)
                        finish()

                    } else {
                        // Enable the button and hide the ProgressBar
                        binding.btnSignIn.isEnabled = true
                        binding.btnSignIn.text = "Sign In"
                        binding.loadingSpinner.visibility = View.GONE

                        Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
            }}else {
                Toast.makeText(this, "No Internet Connection!!", Toast.LENGTH_SHORT).show()
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
        } else if(binding.etPass.text.isNullOrEmpty()){
            Toast.makeText(this, "Password is Required!!", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun isInternetAvailable(context: Context): Boolean {
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

    override fun onStart() {
        super.onStart()

        if(MyPreference(this).isRemember){
            if(auth.currentUser != null){
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }
}