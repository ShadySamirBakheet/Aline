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
import com.app.aline.utils.Methods

class MessageViewModel(application: Application): AndroidViewModel(application)   {

    private val msgRef = FirebaseDatabase.getInstance().getReference("messages/")

    private val liveData = FirebaseQueryLiveData(msgRef)

    fun getUsersData(): LiveData<DataSnapshot?> {
        return liveData
    }

    fun getMessagesData(rootUsers:String): LiveData<DataSnapshot?> {
        return FirebaseQueryLiveData(msgRef.child(rootUsers))
    }

    fun getMessagesLastData(rootUsers:String): LiveData<DataSnapshot?> {
        return FirebaseQueryLiveData(msgRef.child(rootUsers).limitToLast(1))
    }

    fun getMessagesLastMsg(rootUsers:String): LiveData<Message?> {
        return  liveData {
            val mags = getMessagesLastData(rootUsers).value

            Log.d("LOG_TAG", "msg.toString()")
            if (mags != null) {
                if (mags.hasChildren()) {
                    var msg: Message? = null
                    mags.children.forEach { item ->
                        msg = item.getValue(Message::class.java)
                        Log.d("LOG_TAG", msg.toString())

                        emit(msg)
                    }
                }
            }

        }
    }

    fun setMessageData(message: Message): Task<Void> {
        return msgRef.child(Methods.getMessageId(message.senderId.toString(),
            message.receiverID.toString())+"/"+message.msgID).setValue(message)
    }

}