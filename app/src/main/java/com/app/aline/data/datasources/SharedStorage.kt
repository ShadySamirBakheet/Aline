package com.app.aline.data.datasources

import android.content.Context
import android.content.SharedPreferences
import com.app.aline.data.models.ArticleDept
import com.app.aline.data.models.User

class SharedStorage {
    companion object{

        fun saveSelectedDeptData(context: Context,dept: ArticleDept){
            val sharedPreferences: SharedPreferences = context.getSharedPreferences("c", Context.MODE_PRIVATE)
            val edit = sharedPreferences.edit()
            edit.putString("SelectedDept_id", dept.id)
            edit.apply()
        }

        fun getSelectedDept(context: Context): String? {
            val sharedPreferences: SharedPreferences = context.getSharedPreferences("login", Context.MODE_PRIVATE)
            return sharedPreferences.getString("SelectedDept_id","")
        }

        fun saveLoginData(context: Context,user: User){
            val sharedPreferences: SharedPreferences = context.getSharedPreferences("login", Context.MODE_PRIVATE)
            val edit = sharedPreferences.edit()
            edit.putString("login_id", user.id)
            edit.putString("userEmail", user.userEmail)
            edit.putString("fullName", user.fullName)
            edit.putString("user_img", user.userImg)
            edit.putBoolean("isAdmin", user.isAdmin == true)
            edit.apply()
        }

        fun getUserName(context: Context): String? {
            val sharedPreferences: SharedPreferences = context.getSharedPreferences("login", Context.MODE_PRIVATE)

            return sharedPreferences.getString("login_id","")
        }

        fun getFullName(context: Context): String {
            val sharedPreferences: SharedPreferences = context.getSharedPreferences("login", Context.MODE_PRIVATE)

            return sharedPreferences.getString("fullName","").toString()
        }

        fun getLoginImagesData(context: Context): String {
            val sharedPreferences: SharedPreferences = context.getSharedPreferences("login", Context.MODE_PRIVATE)
            return sharedPreferences.getString("user_img","").toString()
        }

        fun getLoginIsAdminData(context: Context): Boolean {
            val sharedPreferences: SharedPreferences = context.getSharedPreferences("login", Context.MODE_PRIVATE)
            return sharedPreferences.getBoolean("isAdmin",false)
        }

    }
}