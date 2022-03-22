package com.app.aline.views.loginsystem

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.app.aline.data.datasources.SharedStorage
import com.app.aline.data.models.User
import com.app.aline.databinding.ActivityLoginBinding
import com.app.aline.viewmodel.NetworkViewModel
import com.app.aline.viewmodel.UserViewModel
import com.app.aline.views.home.HomeActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    private lateinit var networkViewModel: NetworkViewModel
    private lateinit var userViewModel: UserViewModel

    private var code = "+20"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        networkViewModel = ViewModelProvider(this)[NetworkViewModel::class.java]
        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.forgotPassword.setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }

        binding.login.setOnClickListener {
            binding.progress.visibility = VISIBLE
            networkViewModel.networkState(this).observe(this) {
                binding.progress.visibility = GONE
                if (it) {
                    signIn()
                } else {
                    Toast.makeText(this, "Check Network is Available", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.signUp.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
            finish()
        }


    }

    private fun signIn() {
        val userEmail = binding.userEmail.text.toString().trim()
        val password = binding.password.text.toString().trim()

        if (userEmail.isNotEmpty() && password.isNotEmpty()) {

            FirebaseAuth.getInstance()
                .signInWithEmailAndPassword(userEmail.toString(), password)
                .addOnSuccessListener {

                    val liveData = userViewModel.getUserData(it.user!!.uid)
                    liveData.observe(this) { dataSnapshot ->


                        if (dataSnapshot != null) {
                            var user: User? = null

                            try {
                                user = dataSnapshot.getValue(User::class.java)

                            } catch (e: Exception) {
                                Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
                                Log.e("this", e.message!!)
                            }
                            if (user != null) {
                                SharedStorage.saveLoginData(this, user)

                                startActivity(Intent(this, HomeActivity::class.java))
                                finish()


                            } else {

                                binding.progress.visibility = GONE

                                Toast.makeText(this, "Error Data or Not Found", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }


                        binding.progress.visibility = GONE

                        binding.userEmail.setText("")
                        binding.password.setText("")
                    }

                }.addOnFailureListener {
                    binding.progress.visibility = GONE
                    Toast.makeText(
                        this,
                        "Error Data or Not Found User ${it.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }

        } else {
            Toast.makeText(this, "Error Data", Toast.LENGTH_SHORT).show()
        }
    }

}