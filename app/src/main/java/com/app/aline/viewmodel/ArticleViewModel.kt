package com.app.aline.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.app.aline.data.datasources.FirebaseQueryLiveData
import com.app.aline.data.models.*

class ArticleViewModel(application: Application) : AndroidViewModel(application) {

    private val articleRef = FirebaseDatabase.getInstance().getReference("articles/")

    private val liveData = FirebaseQueryLiveData(articleRef)

    fun getArticlesData(): LiveData<DataSnapshot?> {
        return liveData
    }

    fun getArticleImagesData(article: Article): LiveData<DataSnapshot?> {
        return FirebaseQueryLiveData(articleRef.child("${article.deptId}/articles/${article.id}/images/"))
    }

    fun getArticleComment(article: Article): LiveData<DataSnapshot?> {
        return FirebaseQueryLiveData(articleRef.child("${article.deptId}/articles/${article.id}/comments"))
    }

    fun deleteArticleData(article: Article): Task<Void> {
        return articleRef.child("${article.deptId}").child("articles").child("${article.id}")
            .removeValue()
    }

    fun setArticleData(articleDept: ArticleDept): Task<Void> {
        return articleRef.child("${articleDept.id}").setValue(articleDept)
    }

    fun setArticleData(article: Article): Task<Void> {
        return articleRef.child("${article.deptId}").child("articles").child("${article.id}")
            .updateChildren(article.toMap())
    }

    fun setArticleData(article: ArticleImages, deptId: String): Task<Void> {
        return articleRef.child(deptId).child("articles").child("${article.articleId}")
            .child("images").child("${article.id}").setValue(article)
    }

    fun setArticleComment(article: Article, articleComment: ArticleComment): Task<Void> {
        return articleRef.child("${article.deptId}").child("articles").child(article.id.toString())
            .child("comments").child(
                articleComment.id.toString()
            ).setValue(articleComment)
    }

    fun setArticleCommentReplay(
        article: Article,
        articleCommentReplay: ArticleCommentReplay
    ): Task<Void> {
        return articleRef.child("${article.deptId}").child("articles").child(article.id.toString())
            .child("comments").child(
                articleCommentReplay.commentId.toString()
            ).child("replays").child(articleCommentReplay.id.toString()).setValue(articleCommentReplay)
    }

}