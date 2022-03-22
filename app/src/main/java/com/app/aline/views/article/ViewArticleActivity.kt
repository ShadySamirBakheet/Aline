package com.app.aline.views.article

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.smarteist.autoimageslider.SliderAnimations
import com.smarteist.autoimageslider.SliderView
import com.app.aline.adapters.CommentArticleAdapter
import com.app.aline.adapters.ImageSliderAdapter
import com.app.aline.data.datasources.SharedStorage
import com.app.aline.data.datasources.TempData
import com.app.aline.data.models.ArticleComment
import com.app.aline.data.models.ArticleCommentReplay
import com.app.aline.data.models.ArticleCommentView
import com.app.aline.data.models.ArticleImages
import com.app.aline.databinding.ActivityViewArticleBinding
import com.app.aline.viewmodel.ArticleViewModel
import com.app.aline.viewmodel.NetworkViewModel
import java.util.*
import kotlin.collections.ArrayList

class ViewArticleActivity : AppCompatActivity() {

    private lateinit var binding: ActivityViewArticleBinding

    private lateinit var networkViewModel: NetworkViewModel
    private lateinit var articleViewModel: ArticleViewModel
    private lateinit var commentArticleAdapter: CommentArticleAdapter
    private lateinit var imageViewAdapter: ImageSliderAdapter


    private lateinit var articleComments:  ArrayList<ArticleCommentView>
    private lateinit var images:  ArrayList<String?>

    var userId = ""
    var userName = ""
    var userImage = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        networkViewModel = ViewModelProvider(this)[NetworkViewModel::class.java]
        articleViewModel = ViewModelProvider(this)[ArticleViewModel::class.java]

        binding = ActivityViewArticleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        articleComments = ArrayList()
        images = ArrayList()
        commentArticleAdapter = CommentArticleAdapter(this)
        imageViewAdapter = ImageSliderAdapter(this)

        userId = SharedStorage.getUserName(this).toString()
        userName =  SharedStorage.getFullName(this)
        userImage=  SharedStorage.getLoginImagesData(this)

        binding.usersComments.apply {
            setHasFixedSize( true)
            layoutManager = LinearLayoutManager(context)
            adapter= commentArticleAdapter
        }

        commentArticleAdapter.setOnSaveListener {text,id->
            if (text.isNotEmpty()){
                val articleCommentReplay = ArticleCommentReplay("ConReplay${System.currentTimeMillis()}",id,text,userId,userName,userImage,Date())
                articleViewModel.setArticleCommentReplay(TempData.article,articleCommentReplay)
            }else{
                Toast.makeText(this, "Please Enter Replay to Comment", Toast.LENGTH_SHORT).show()
            }
        }

        binding.imageSlider.apply {
            setSliderTransformAnimation(SliderAnimations.ZOOMOUTTRANSFORMATION)
            setSliderAnimationDuration(1000)
            autoCycleDirection = SliderView.AUTO_CYCLE_DIRECTION_RIGHT
            scrollTimeInSec = 4 //set scroll delay in seconds :
            startAutoCycle()
            setSliderAdapter(imageViewAdapter)
        }


        binding.body.text= TempData.article.article
        binding.title.text= TempData.article.articleTitle

        binding.goBack.setOnClickListener {
            finish()
        }

        binding.btnSend.setOnClickListener {
            networkViewModel.networkState(this).observe(this){
                if (it){
                    saveComment()
                }else {
                    Toast.makeText(this, "Check Network is Available", Toast.LENGTH_SHORT).show()
                }
            }
        }
        getData()
    }

    private fun getData() {

        networkViewModel.networkState(this).observe(this){
            if (it){
               articleViewModel.getArticleComment(TempData.article).observe(this){data->
                   articleComments.clear()
                   if (data != null) {
                       if (data.hasChildren()){
                           data.children.forEach {
                               val articleComment = it.getValue(ArticleComment::class.java)
                               if (articleComment != null) {
                                   var  articleCommentView = ArticleCommentView(articleComment)
                                   if (it.hasChild("replays")){
                                       val articleCommentReplays = ArrayList<ArticleCommentReplay>()
                                       it.child("replays").children.forEach { replay->
                                           val articleCommentReplay = replay.getValue(ArticleCommentReplay::class.java)
                                           if (articleCommentReplay != null) {
                                               articleCommentReplays.add(articleCommentReplay)
                                           }
                                       }
                                       if (articleCommentReplays.isNotEmpty()){
                                           articleCommentView.replays = articleCommentReplays
                                       }
                                   }

                                   articleComments.add(articleCommentView)
                               }
                           }
                       }
                   }

                   commentArticleAdapter.addData(articleComments)
               }
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
                    images.add(0, TempData.article.articleImage.toString())
                    imageViewAdapter.addData(images)
                }
            }else {
                Toast.makeText(this, "Check Network is Available", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun saveComment() {
        val  comment = binding.comment.text.toString().trim()
        if (comment.isNotEmpty()) {
            val articleComment = ArticleComment(id = "Comm"+System.currentTimeMillis(), comment = comment,
                articleId = TempData.article.id, userID = userId,
            date = Date(), userImage = userImage, userName =userName
            )
            articleViewModel.setArticleComment(TempData.article,articleComment).addOnCompleteListener {
                binding.comment.setText("")
            }
        }
    }
}