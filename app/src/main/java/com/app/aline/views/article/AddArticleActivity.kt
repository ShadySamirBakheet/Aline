package com.app.aline.views.article

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.storage.FirebaseStorage
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.default
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import com.app.aline.R
import com.app.aline.adapters.DeptArrayAdapter
import com.app.aline.adapters.ImageAddedAdapter
import com.app.aline.data.datasources.SharedStorage
import com.app.aline.data.datasources.TempData
import com.app.aline.data.models.Article
import com.app.aline.data.models.ArticleDept
import com.app.aline.data.models.ArticleImages
import com.app.aline.databinding.ActivityAddArticleBinding
import com.app.aline.utils.FileUtils
import com.app.aline.utils.PermissionCheck
import com.app.aline.viewmodel.ArticleViewModel
import com.app.aline.viewmodel.NetworkViewModel
import java.io.File

class AddArticleActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddArticleBinding

    private lateinit var networkViewModel: NetworkViewModel
    private lateinit var articleViewModel: ArticleViewModel
    private lateinit var imageAddedAdapter: ImageAddedAdapter


    var article: Article = Article()
    var articleDept: ArticleDept = ArticleDept()

    private var sendingFile: File? = null
    private var sendingFiles: ArrayList<File> = ArrayList()

    var isEdit = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        networkViewModel = ViewModelProvider(this)[NetworkViewModel::class.java]
        articleViewModel = ViewModelProvider(this)[ArticleViewModel::class.java]

        binding = ActivityAddArticleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        isEdit = intent.getBooleanExtra("isEdit", false)

        imageAddedAdapter = ImageAddedAdapter(this)

        binding.imageAdded.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = imageAddedAdapter
        }

        if (isEdit) {
            binding.deptAdd.visibility = GONE
            binding.dept.visibility = GONE

            Glide.with(this).load(TempData.article.articleImage)
                .placeholder(R.drawable.user)
                .into(binding.setImage)

            getImages()

            binding.article.setText(TempData.article.article)
            binding.title.setText(TempData.article.articleTitle)

        } else {
            binding.deptAdd.visibility = VISIBLE
            binding.dept.visibility = VISIBLE
        }

        binding.dept.adapter = DeptArrayAdapter(TempData.depts, this)

        binding.addArticle.setOnClickListener {
            networkViewModel.networkState(this).observe(this) {
                if (it) {
                    binding.progress.visibility = VISIBLE
                    saveData()
                } else {
                    Toast.makeText(this, "Check Network is Available", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.getImage.setOnClickListener {
            PermissionCheck.readAndWriteExternalStorage(this)
            loadImage(1)
        }

        binding.imageAdd.setOnClickListener {
            PermissionCheck.readAndWriteExternalStorage(this)

            loadImage(2)
        }

        binding.goBack.setOnClickListener {
            finish()
        }

        binding.deptAdd.setOnClickListener {
            addDeptDialog()
        }

    }

    private fun getImages() {

         val images= ArrayList<String>()

        articleViewModel.getArticleImagesData(TempData.article).observe(this){
            images.clear()
            if (it != null && it.hasChildren()){
                it.children.forEach { imageSheet->
                    val image = imageSheet.getValue(ArticleImages::class.java)
                    if (image != null) {
                        images.add(image.image.toString())
                    }
                }
            }
            imageAddedAdapter.addData(images)
        }
    }

    private fun saveData() {
        val title = binding.title.text.toString().trim()
        val articled = binding.article.text.toString().trim()
        //    val deptNew = binding.deptAdd.text.toString().trim()

        if (title.isNotEmpty() && articled.isNotEmpty()) {

            getDept()

            if (isEdit) {
                article = Article(
                    id = TempData.article.id,
                    articleTitle = title,
                    article = articled,
                    userId = TempData.article.userId,
                    articleImage = TempData.article.articleImage,
                    deptId = TempData.article.deptId,
                )
            } else {

                article = Article(
                    id = "Article"+System.currentTimeMillis().toString(),
                    articleTitle = title,
                    article = articled,
                    userId = SharedStorage.getUserName(this).toString(),
                    deptId = articleDept.id
                )
            }

            if (sendingFile != null) {
                val ref = FirebaseStorage.getInstance().reference.child("articles_images")
                    .child(article.id + "Image" + ".png")

                var file = sendingFile
                GlobalScope.launch {
                    file = Compressor.compress(this@AddArticleActivity, sendingFile!!) {
                        default(format = Bitmap.CompressFormat.PNG)
                    }
                }

                ref.putFile(file!!.toUri()).addOnCompleteListener {
                    it.result!!.storage.downloadUrl.addOnSuccessListener { uri ->
                        if (uri != null) {
                            article.articleImage = uri.toString()

                            saveChangedDate()

                        }
                    }
                }.addOnFailureListener {
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                }

            } else {
                if (isEdit) {
                    saveChangedDate()
                } else {
                    binding.progress.visibility = GONE
                    Toast.makeText(this, "Not found image", Toast.LENGTH_SHORT).show()
                }
            }

        } else {
            binding.progress.visibility = GONE
            Toast.makeText(this, "Error Data", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getDept() {
        articleDept = TempData.depts[binding.dept.selectedItemPosition]
    }


    private fun addDeptDialog() {
        val alertDialogBuilder = AlertDialog.Builder(this)
        val inflater = LayoutInflater.from(this)
        val dialogLayout = inflater.inflate(R.layout.popup_add_dept, null)

        alertDialogBuilder.setView(dialogLayout)
        val alertDialog = alertDialogBuilder.create()


       val deptAdd =  dialogLayout.findViewById<TextInputEditText>(R.id.deptAdd)

        dialogLayout.findViewById<TextView>(R.id.save).setOnClickListener {
            val deptName = deptAdd.text.toString().trim()

            if (deptName.isNotBlank()){
                val dept = ArticleDept(id = "Dept"+System.currentTimeMillis().toString(),deptTitle = deptName,)
                TempData.depts.add(dept)

                networkViewModel.networkState(this).observe(this    ){
                    if (it){

                        articleViewModel.setArticleData(dept).addOnSuccessListener {

                            binding.dept.adapter = DeptArrayAdapter(TempData.depts, this)
                            binding.dept.setSelection(TempData.depts.size-1)
                            alertDialog.dismiss()
                        }.addOnFailureListener {
                            Toast.makeText(this, "Error Occur", Toast.LENGTH_SHORT).show()
                        }

                    }else{
                        Toast.makeText(this, "Please open Network", Toast.LENGTH_SHORT).show()
                    }
                }

            }else{
                Toast.makeText(this, "Enter Department Name", Toast.LENGTH_SHORT).show()
            }

        }

        alertDialog.show()

    }


    private fun saveChangedDate() {
        if (sendingFiles.isNotEmpty()) {
            sendingFiles.forEach {fileSel->
                val articleImages = ArticleImages(
                    id = System.currentTimeMillis().toString(),
                    articleId = article.id
                )
                var file2 = fileSel
                GlobalScope.launch {
                    file2 = Compressor.compress(this@AddArticleActivity, fileSel) {
                        default(format = Bitmap.CompressFormat.PNG)
                    }
                }
                val ref2 =
                    FirebaseStorage.getInstance().reference.child("articles_images")
                        .child(articleImages.id + "Images" + ".png")
                ref2.putFile(file2.toUri()).addOnCompleteListener {
                    it.result!!.storage.downloadUrl.addOnSuccessListener { uri ->
                        articleImages.image = uri.toString()
                        articleViewModel.setArticleData(
                            articleImages,
                            article.deptId.toString()
                        )

                    }
                }
            }
        }

        articleViewModel.setArticleData(article).addOnCompleteListener {
            binding.progress.visibility = GONE
            Toast.makeText(this, "Done Added", Toast.LENGTH_SHORT).show()
            finish()
        }.addOnFailureListener {
            Toast.makeText(this, "Error occurred", Toast.LENGTH_SHORT).show()
            binding.progress.visibility = GONE
        }
    }

    private fun loadImage(id: Int) {
        val intent = Intent(Intent.ACTION_PICK)
        intent.apply {
            type = "image/*"
        }
        startActivityForResult(intent, id)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && data != null) {
            when (requestCode) {
                1 -> {
                    loadImageFun(data)
                }
                2 -> {
                    if (data.data != null) {
                        val uri = data.data!!

                        sendingFiles.add(File(FileUtils.getSmartFilePath(this, data.data!!) ?: ""))

                        imageAddedAdapter.addItem(uri)

                    } else {
                        Toast.makeText(this, "Error File", Toast.LENGTH_LONG).show()
                    }
                }

            }
        } else {
            Toast.makeText(this, "Error", Toast.LENGTH_LONG).show()
        }
    }

    private fun loadImageFun(data: Intent) {
        if (data.data != null) {
            val uri = data.data!!

            sendingFile = File(FileUtils.getSmartFilePath(this, data.data!!) ?: "")

            binding.setImage.setImageURI(uri)

            Toast.makeText(this, "Send File", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(this, "Error File", Toast.LENGTH_LONG).show()
        }
    }
}