package com.app.aline.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.app.aline.data.datasources.FirebaseQueryLiveData
import com.app.aline.data.models.Message
import com.app.aline.data.models.MessageUser
import com.app.aline.data.models.User
import com.app.aline.utils.Methods


class UserViewModel (application: Application): AndroidViewModel(application){

    private val userRef = FirebaseDatabase.getInstance().getReference("users/")
    private val msgRef = FirebaseDatabase.getInstance().getReference("messages/")

    private val liveData = FirebaseQueryLiveData(userRef)

    fun getUsersData(): LiveData<DataSnapshot?> {
        return liveData
    }

    fun getUserData(userName:String): LiveData<DataSnapshot?> {
        return FirebaseQueryLiveData(userRef.child(userName))
    }

    fun setUserData(user: User): Task<Void> {
        return userRef.child(user.id.toString()).setValue(user)
    }

    fun getUserWithLastMSG(myUserName:String):LiveData<ArrayList<MessageUser>>{
        val responseLiveDataResult: LiveData<ArrayList<MessageUser>> = liveData {
            var usersMsg: ArrayList<MessageUser> = ArrayList()
            getUsersData().value.apply {
                if (this != null) {
                    usersMsg.clear()
                    for (item in this.children) {
                        try {
                            val user = item.getValue(User::class.java)!!
                            if (user.userName != myUserName) {
                                //  usersMsg.add(MessageUser(user, "msg!!.dataMsg","Methods.getDateString(msg!!.date)"))
                                getMessagesLastData(
                                    Methods.getMessageId(
                                        myUserName,
                                        user.userName.toString()
                                    )
                                ).value.apply {
                                    if (this != null) {
                                        if (this.hasChildren()) {
                                            var msg: Message? = null
                                            this.children.forEach { item ->
                                                msg = item.getValue(Message::class.java)
                                            }
                                            Log.e("MSG", "true")
                                            if (msg != null) {
                                                Log.e("MSG", msg.toString())
                                                usersMsg.add(
                                                    MessageUser(
                                                        user,
                                                        msg!!.dataMsg.toString(),
                                                        Methods.getDateString(
                                                            msg!!.date
                                                        ), msg!!.date
                                                    )
                                                )

                                                emit(usersMsg)
                                            }
                                        }
                                    }

                                }
                            }
                        } catch (e: Exception) {
                            Log.e("Error", e.message.toString())
                        }

                    }
                    Log.e("MSG R", usersMsg.toString())

                }
            }
            emit(usersMsg)
        }

        return responseLiveDataResult
    }


    private fun getMessagesLastData(rootUsers:String): LiveData<DataSnapshot?> {
        return FirebaseQueryLiveData(msgRef.child(rootUsers).limitToLast(1))
    }

}