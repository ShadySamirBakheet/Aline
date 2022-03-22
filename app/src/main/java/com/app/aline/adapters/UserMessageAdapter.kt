package com.app.aline.adapters

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.app.aline.R
import com.app.aline.data.datasources.SharedStorage
import com.app.aline.data.models.Message
import com.app.aline.data.models.MessageUser
import com.app.aline.data.models.User
import com.app.aline.utils.Methods
import com.app.aline.viewmodel.MessageViewModel
import com.app.aline.views.chats.ChatActivity

class UserMessageAdapter(
    private val context: Context?,
    private val viewLifecycleOwner: LifecycleOwner,
    private val messageViewModel: MessageViewModel,
    private val myUserName: String
) :
    RecyclerView.Adapter<UserMessageAdapter.ViewHolder>() {

    private var users: ArrayList<User> = ArrayList()
    private var usersMsg: ArrayList<MessageUser> = ArrayList()
    private var size = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.item_user_message, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = usersMsg[position]
        holder.apply {
            userName.text = item.user.fullName
            lastDate.text = item.dateStr
            lastMsg.text = item.message

            messageViewModel.getMessagesLastData(
                Methods.getMessageId(
                    myUserName,
                    item.user.id.toString()
                )
            ).observe(viewLifecycleOwner) {mags->
                if (mags != null) {
                    if (mags.hasChildren()) {
                        var msg: Message?
                        mags.children.forEach { item ->
                            msg = item.getValue(Message::class.java)
                            Log.d("LOG_TAG", msg.toString())
                            if (msg != null) {
                                lastDate.text = Methods.getDateString(msg!!.date)
                                lastMsg.text = msg!!.dataMsg
                            }
                        }
                    }
                }

            }

            if (item.user.userImg != null) {
                if (context != null) {
                    Glide.with(context).load(item.user.userImg)
                        .placeholder(R.drawable.logo1)
                        .into(userimg)
                }
            }

            itemView.setOnClickListener {
                context?.startActivity(
                    Intent(context, ChatActivity::class.java)
                        .putExtra("userId", item.user.id)
                        .putExtra("userImg", item.user.userImg)
                        .putExtra("userName", item.user.fullName)
                )
            }
        }

    }


    private var onItemClickListener: ((String) -> Unit)? = null

    fun setOnItemClickListener(listener: (String) -> Unit) {
        onItemClickListener = listener
    }


    override fun getItemCount(): Int {
        return size
    }

    fun addData(users: ArrayList<User>) {
        this.users.clear()
        if (SharedStorage.getLoginIsAdminData(context!!)) {
            users.forEach {
                if (it.isAdmin != true) {
                    this.users.add(it)
                }
            }
        } else {
            users.forEach {
                if (it.isAdmin == true) {
                    this.users.add(it)
                }
            }
        }

        size = this.users.size

        notifyDataSetChanged()
    }

    fun addDataMsg(usersMsg: ArrayList<MessageUser>) {
        this.usersMsg.clear()
        if (SharedStorage.getLoginIsAdminData(context!!)) {
            usersMsg.forEach {
                if (it.user.isAdmin != true) {
                    this.usersMsg.add(it)
                }
            }
        } else {
            usersMsg.forEach {
                if (it.user.isAdmin == true) {
                    this.usersMsg.add(it)
                }
            }
        }
        this.usersMsg.sortBy {
            it.date
        }
        this.usersMsg.reverse()
        size = this.usersMsg.size
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var userName: TextView = itemView.findViewById(R.id.userName)
        var lastMsg: TextView = itemView.findViewById(R.id.lastMsg)
        var lastDate: TextView = itemView.findViewById(R.id.lastDate)
        var userimg: ImageView = itemView.findViewById(R.id.userimg)
    }
}