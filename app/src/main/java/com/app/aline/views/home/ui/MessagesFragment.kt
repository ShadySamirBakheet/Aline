package com.app.aline.views.home.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.aline.adapters.UserMessageAdapter
import com.app.aline.data.datasources.SharedStorage
import com.app.aline.data.models.Message
import com.app.aline.data.models.MessageUser
import com.app.aline.data.models.User
import com.app.aline.databinding.FragmentMessagesBinding
import com.app.aline.utils.Methods
import com.app.aline.viewmodel.MessageViewModel
import com.app.aline.viewmodel.NetworkViewModel
import com.app.aline.viewmodel.UserViewModel

class MessagesFragment : Fragment() {

    private var _binding: FragmentMessagesBinding? = null

    private val binding get() = _binding!!

    private lateinit var networkViewModel: NetworkViewModel
    private lateinit var userViewModel: UserViewModel
    private lateinit var messageViewModel: MessageViewModel
    private lateinit var userAdapter: UserMessageAdapter

    private lateinit var usersMsg: ArrayList<MessageUser>
    private lateinit var users: ArrayList<User>

    var myUserName: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        networkViewModel = ViewModelProvider(this)[NetworkViewModel::class.java]
        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]
        messageViewModel = ViewModelProvider(this)[MessageViewModel::class.java]

        _binding = FragmentMessagesBinding.inflate(inflater, container, false)
        val root: View = binding.root
        users = ArrayList()
        usersMsg = ArrayList()

        myUserName = SharedStorage.getUserName(requireContext()).toString()
        userAdapter = UserMessageAdapter(context, viewLifecycleOwner, messageViewModel, myUserName)

        binding.progress.visibility = VISIBLE

        binding.users.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = userAdapter
        }

        networkViewModel.networkState(context).observe(viewLifecycleOwner) {
            if (it) {
                if (SharedStorage.getLoginIsAdminData(requireContext())) {
                    getData()
                } else {
                    getData2()
                }
            } else {
                Toast.makeText(context, "Please Open Network", Toast.LENGTH_SHORT).show()
            }
        }

        return root
    }

    private fun getData3() {

        userViewModel.getUsersData().observe(viewLifecycleOwner) {
            if (it != null) {
                users.clear()
                usersMsg.clear()
                for (item in it.children) {
                    try {
                        val user = item.getValue(User::class.java)!!
                        if (user.userPhone != myUserName) {
                            messageViewModel.getMessagesLastData(
                                Methods.getMessageId(
                                    myUserName,
                                    user.userPhone.toString()
                                )
                            ).observe(viewLifecycleOwner) {
                                if (it != null) {
                                    if (it.hasChildren()) {
                                        var msg: Message? = null
                                        it.children.forEach { item ->
                                            msg = item.getValue(Message::class.java)
                                        }
                                        Log.e("MSG", "true")
                                        if (msg != null) {
                                            Log.e("MSG", msg.toString())
                                            usersMsg.add(
                                                MessageUser(
                                                    user,
                                                    msg!!.dataMsg.toString(), Methods.getDateString(
                                                        msg!!.date
                                                    ), msg!!.date
                                                )
                                            )
                                        }
                                    }
                                }
                                if (usersMsg.isNotEmpty()) {
                                    // userAdapter.addData(users)
                                    userAdapter.addDataMsg(usersMsg)
                                    binding.empty.visibility = GONE
                                } else {
                                    binding.empty.visibility = VISIBLE
                                }
                            }
                            users.add(user)
                        }
                    } catch (e: Exception) {
                        Log.e("Error", e.message.toString())
                    } finally {
                        binding.progress.visibility = GONE
                    }
                }
                Log.e("MSG", usersMsg.toString())
                if (users.isNotEmpty()) {

                } else {
                    binding.empty.visibility = VISIBLE
                }
            } else {
                binding.progress.visibility = GONE

            }
        }
    }


    private fun getData2() {
        userViewModel.getUsersData().observe(viewLifecycleOwner) {
            usersMsg.clear()
            if (it != null) {
                for (item in it.children) {
                    try {
                        val user = item.getValue(User::class.java)!!
                        if (user.userName != myUserName) {
                            val userMsg = MessageUser(user, null, null, null)
                            usersMsg.add(userMsg)
                        }
                    } catch (e: Exception) {
                        Log.e("Error", e.message.toString())
                    } finally {
                        binding.progress.visibility = GONE
                    }
                }
                Log.e("MSG", usersMsg.toString())

                if (usersMsg.isNotEmpty()) {
                    userAdapter.addDataMsg(usersMsg)
                    binding.empty.visibility = GONE
                } else {
                    binding.empty.visibility = VISIBLE
                }
            } else {
                binding.progress.visibility = GONE

            }
        }
    }

    private fun getData() {
        userViewModel.getUsersData().observe(viewLifecycleOwner) {
            if (it != null) {
                users.clear()
                usersMsg.clear()
                for (item in it.children) {
                    try {
                        val user = item.getValue(User::class.java)!!
                        if (user.userName != myUserName) {
                            //  usersMsg.add(MessageUser(user, "msg!!.dataMsg","Methods.getDateString(msg!!.date)"))
                            messageViewModel.getMessagesLastData(
                                Methods.getMessageId(
                                    myUserName,
                                    user.id.toString()
                                )
                            ).observe(viewLifecycleOwner) {
                                if (it != null) {
                                    if (it.hasChildren()) {
                                        var msg: Message? = null
                                        it.children.forEach { item ->
                                            msg = item.getValue(Message::class.java)
                                        }
                                        Log.e("MSG", "true")
                                        if (msg != null) {
                                            Log.e("MSG", msg.toString())
                                            val userMsg = MessageUser(
                                                user,
                                                msg!!.dataMsg.toString(),
                                                Methods.getDateString(
                                                    msg!!.date
                                                ), msg!!.date
                                            )

                                            if (!usersMsg.contains(userMsg)) {
                                                usersMsg.add(userMsg)
                                            }

                                            if (usersMsg.isNotEmpty()) {
                                                Log.e("MSGSize", usersMsg.size.toString())
                                                userAdapter.addDataMsg(usersMsg)
                                                binding.empty.visibility = GONE
                                            } else {
                                                binding.empty.visibility = VISIBLE
                                            }
                                        }
                                    }
                                }

                            }
                        }
                    } catch (e: Exception) {
                        Log.e("Error", e.message.toString())
                    } finally {
                        binding.progress.visibility = GONE
                        binding.empty.visibility = VISIBLE
                    }
                }
                Log.e("MSG", usersMsg.toString())

            } else {
                binding.progress.visibility = GONE
                binding.empty.visibility = VISIBLE
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}