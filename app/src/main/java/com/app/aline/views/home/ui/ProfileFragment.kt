package com.app.aline.views.home.ui

import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.default
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import com.app.aline.R
import com.app.aline.data.datasources.SharedStorage
import com.app.aline.data.models.User
import com.app.aline.databinding.FragmentProfileBinding
import com.app.aline.utils.FileUtils
import com.app.aline.utils.PermissionCheck
import com.app.aline.viewmodel.NetworkViewModel
import com.app.aline.viewmodel.UserViewModel
import com.app.aline.views.loginsystem.LoginActivity
import java.io.File


class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var networkViewModel: NetworkViewModel
    private lateinit var userViewModel: UserViewModel
    private lateinit var auth: FirebaseAuth

    var sendingFile: File? = null
    var isEdit = true
    var isAdmin = false

    var imagePath :String?=null

    var user: User?=null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        networkViewModel = ViewModelProvider(this)[NetworkViewModel::class.java]
        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]

        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        isAdmin = SharedStorage.getLoginIsAdminData(requireContext())

        auth = FirebaseAuth.getInstance()

        getData()

        binding.getImage.setOnClickListener {
            PermissionCheck.readAndWriteExternalStorage(context)
            loadImage()
        }

        //disableEdit()


        binding.saveEdit.setOnClickListener {
            if (isEdit){
                saveChange()
            }else{
                enableEdit()
            }
        }

        binding.signOut.setOnClickListener {
          showDialogFun()
        }

        return root
    }

    private fun showDialogFun() {
        val builder = AlertDialog.Builder(context)

        builder.setTitle("Sign Out")
            .setMessage("Are you want to Sign Out")
            .setPositiveButton("Ok") { dialogInterface, _ ->

                auth.signOut()
                startActivity(Intent(context, LoginActivity::class.java))
               activity?.finish()
                dialogInterface.dismiss()
            }
            .setNegativeButton("Cancel") { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
        builder.create().show()
    }

    private fun getData() {
        binding.progress.visibility = VISIBLE
        userViewModel.getUserData(SharedStorage.getUserName(requireContext()).toString()).observe(viewLifecycleOwner){

            binding.progress.visibility = GONE
            if (it != null) {

                try {
                    user = it.getValue(User::class.java)
                }catch (e: Exception){
                    Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                    Log.e("this", e.message!!)
                }

                if (user != null){

                    binding.userName.setText(user!!.userName)
                    binding.userEmail.setText(user!!.userEmail)
                    binding.userPhone.setText(user!!.userPhone)
                    binding.fullName.setText(user!!.fullName)
                    binding.idNo.setText(user!!.userIdNum)
                    binding.bankAccount.setText(user!!.userBankAccount)
                    setImage(user!!.userImg)

                    if (user!!.isAdmin == false){
                        binding.isAdmin.visibility = VISIBLE
                    }else{
                        binding.isAdmin.visibility = GONE
                    }

                }else{
                    Toast.makeText(context, "Error Data or Not Found", Toast.LENGTH_SHORT).show()
                }
            }

        }
    }

    private fun setImage(userImg: String?) {
        if (userImg != null){
            Glide.with(this).load(userImg)
                .placeholder(R.drawable.user)
                .into(binding.setImage)
        }
        if (sendingFile != null){
            binding.setImage.setImageURI(sendingFile!!.toUri())
        }
    }

    private fun enableEdit() {
//        isEdit = true
//
//        binding.userName.isEnabled = true
//        binding.userEmail.isEnabled = true
//        binding.userPhone.isEnabled = true
//        binding.getImage.isEnabled = true
//        binding.saveEdit.isEnabled = true


    }

    private fun disableEdit(){
//
//        isEdit = false
//
//        binding.userName.isEnabled = false
//        binding.userEmail.isEnabled = false
//        binding.userPhone.isEnabled = false
//        binding.getImage.isEnabled = false
//        binding.saveEdit.isEnabled = false

    }

    private fun saveChange() {
        binding.progress.visibility = VISIBLE
        networkViewModel.networkState(context).observe(viewLifecycleOwner) {
            if (it) {
                saveChangedImage()
            } else {
                Toast.makeText(context, "Check Network is Available", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private  fun saveChangedImage() {
        if (sendingFile != null){
            val ref = FirebaseStorage.getInstance().reference.child("users_images").child(user?.userPhone+".png")

            var file= sendingFile
            GlobalScope.launch {
                file = Compressor.compress(requireContext(), sendingFile!!){
                    default(format =  Bitmap.CompressFormat.PNG)
                }
            }

            ref.putFile(file!!.toUri()).addOnCompleteListener {
                it.result!!.storage.downloadUrl.addOnSuccessListener {uri->
                    if (uri != null) {
                        imagePath = uri.toString()
                    }
                    saveChangedDate()
                }
            }.addOnFailureListener {
                Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
            }

        }else{
            saveChangedDate()
        }
    }

    private fun saveChangedDate() {
        val phoneNumber = binding.userPhone.text.toString().trim()
        val userEmail = binding.userEmail.text.toString().trim().lowercase()
        val bankAccount = binding.bankAccount.text.toString().trim()
        val fullName = binding.fullName.text.toString().trim()
        val idNo = binding.idNo.text.toString().trim()

        user!!.userBankAccount = bankAccount
        user!!.userEmail = userEmail
        user!!.userPhone = phoneNumber
        user!!.fullName = fullName
        user!!.userIdNum = idNo

        if (imagePath != null){
            user!!.userImg = imagePath
        }

        sendingFile = null

        userViewModel.setUserData(user!!).addOnCompleteListener {
            SharedStorage.saveLoginData(requireContext(), user!!)
            binding.progress.visibility = GONE

            Toast.makeText(context, "Done", Toast.LENGTH_SHORT).show()

            disableEdit()
        }.addOnFailureListener{
            binding.progress.visibility = GONE
            disableEdit()
        }

    }

    private fun loadImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.apply {
            type = "image/*"
        }
        startActivityForResult(intent, 1)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && data != null) {
            when(requestCode){
                1->{
                    loadImageFun(data)
                }

            }
        }else{
            Toast.makeText(context,"Error", Toast.LENGTH_LONG).show()
        }
    }


    private fun loadImageFun(data: Intent) {
        if (data.data != null){
            val uri = data.data!!


            sendingFile = File(FileUtils.getSmartFilePath(requireContext(), data.data!!) ?: "")

            binding.setImage.setImageURI(uri)

            Toast.makeText(context,"Send File", Toast.LENGTH_LONG).show()
        }else{
            Toast.makeText(context,"Error File", Toast.LENGTH_LONG).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}