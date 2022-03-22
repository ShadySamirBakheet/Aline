package com.app.aline.views.loginsystem

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.app.aline.data.datasources.SharedStorage
import com.app.aline.data.models.User
import com.app.aline.databinding.ActivitySignUpBinding
import com.app.aline.viewmodel.NetworkViewModel
import com.app.aline.viewmodel.UserViewModel
import com.app.aline.views.home.HomeActivity

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding

    private lateinit var networkViewModel: NetworkViewModel
    private lateinit var userViewModel: UserViewModel
    private lateinit var auth: FirebaseAuth



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()

        networkViewModel = ViewModelProvider(this)[NetworkViewModel::class.java]
        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]

        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.progress.visibility = GONE

        binding.login.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        binding.signUp.setOnClickListener {

            binding.progress.visibility = VISIBLE

            networkViewModel.networkState(this).observe(this) {
                if (it) {
                    signUp()
                } else {

                    binding.progress.visibility = GONE

                    Toast.makeText(this, "Check Network is Available", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun signUp() {

        val password = binding.password.text.toString().trim()
        val confirmPassword = binding.confirmPassword.text.toString().trim()

        val userEmail = binding.email.text.toString().trim().lowercase()
        val userName = binding.userName.text.toString().trim()
        val fullName = binding.fullName.text.toString().trim()

        if (fullName.isNotEmpty() && password.isNotEmpty() &&
            userEmail.isNotEmpty() && userName.isNotEmpty() &&
            confirmPassword.isNotEmpty() && password == confirmPassword
        ){

            auth.createUserWithEmailAndPassword(userEmail,password).addOnSuccessListener {
                it.user?.uid
                val user = User(id=  it.user?.uid, userName =  userName, fullName = fullName,
                    userEmail= userEmail)

                userViewModel.setUserData(user).addOnSuccessListener {
                    SharedStorage.saveLoginData(this,user)
                    startActivity(Intent(this, HomeActivity::class.java))
                    finish()
                }.addOnFailureListener{
                    Toast.makeText(this, "Error ${it.message}", Toast.LENGTH_SHORT).show()
                }

                binding.progress.visibility = GONE

            }.addOnFailureListener {

                binding.progress.visibility = GONE

                Toast.makeText(this, "Error ${it.message}", Toast.LENGTH_SHORT).show()
            }

        }else{
            Toast.makeText(this, "Error Data Enterd", Toast.LENGTH_SHORT).show()
        }
    }

}