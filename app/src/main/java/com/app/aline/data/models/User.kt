package com.app.aline.data.models

data class User (
    val id:String?=null,
    var userName:String?=null,
    var fullName:String?=null,
    var userImg:String?=null,
    var userEmail:String?=null,
    var userPhone:String?=null,
    var userIdNum:String?=null,
    var userBankAccount:String?=null,
    var isAdmin:Boolean? = null,
)