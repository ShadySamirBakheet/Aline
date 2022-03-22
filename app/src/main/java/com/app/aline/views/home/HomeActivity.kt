package com.app.aline.views.home

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.app.aline.R
import com.app.aline.data.datasources.SharedStorage
import com.app.aline.databinding.ActivityHomeBinding
import com.app.aline.views.article.AddArticleActivity
import com.app.aline.views.loginsystem.LoginActivity

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        if (SharedStorage.getLoginIsAdminData(this)) {
            navView.inflateMenu(R.menu.admin_nav_menu)
        } else {
            navView.inflateMenu(R.menu.bottom_nav_menu)
        }


        val navController = findNavController(R.id.nav_host_fragment_activity_home)

        navView.setupWithNavController(navController)

        binding.addArticle.setOnClickListener {
            startActivity(Intent(this, AddArticleActivity::class.java))
        }

        auth = FirebaseAuth.getInstance()

        binding.signOut.setOnClickListener {
            showDialogFun()
        }

        if (SharedStorage.getLoginIsAdminData(this)) {
            binding.addArticle.visibility = View.VISIBLE
        } else {
            binding.addArticle.visibility = View.GONE
        }
    }

    private fun showDialogFun() {
        val builder = AlertDialog.Builder(this)

        builder.setTitle("Sign Out")
            .setMessage("Are you want to Sign Out")
            .setPositiveButton("Ok") { dialogInterface, _ ->

                startActivity(Intent(this, LoginActivity::class.java))
                auth.signOut()

                finish()
                dialogInterface.dismiss()
            }
            .setNegativeButton("Cancel") { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
        builder.create().show()
    }
}