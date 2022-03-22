package com.app.aline.adapters

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.app.aline.R
import com.app.aline.data.datasources.TempData
import com.app.aline.data.models.Article
import com.app.aline.data.models.ArticleDept
import com.app.aline.views.article.AddArticleActivity
import com.app.aline.views.article.ViewArticleActivity
import java.util.ArrayList

class ArticleAdapter   (private val context: Context?,private val uid:String) : RecyclerView.Adapter<ArticleAdapter.ViewHolder>() {
    private  var  articles: ArrayList<Article>? = null
    private  var  allArticles: ArrayList<Article>? = null
    private  var size = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.item_article , parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = articles?.get(position)
        holder.apply {

            title.text = item!!.articleTitle

            if (item.userId == uid){
                controllers.visibility=View.VISIBLE
            }else{
                controllers.visibility=View.GONE
            }

            if (context != null) {
                Glide.with(context).load(item.articleImage)
                    .placeholder(R.drawable.user)
                    .into(image)
            }

            itemView.setOnClickListener {
                TempData.article = item
                context?.startActivity(Intent(context, ViewArticleActivity::class.java))
            }
            edit.setOnClickListener {
                TempData.article = item
                context?.startActivity(Intent(context, AddArticleActivity::class.java).putExtra("isEdit",true))
            }
            delete.setOnClickListener {
            onItemDeleteListener.let {
                if (it != null) {
                    it(item)
                }
            }
            }
        }
    }


    private var onItemDeleteListener: ((Article) -> Unit)? = null

    fun setOnItemDeleteListener(listener: (Article) -> Unit) {
        onItemDeleteListener = listener
    }


    override fun getItemCount(): Int {
        return size
    }

    fun addData(articles: ArrayList<Article>) {
        this.articles = articles
        this.allArticles = ArrayList(articles)
        this.articles!!.reverse()
        this.allArticles!!.reverse()
        size = articles.size
        Log.e("Size",size.toString())
        notifyDataSetChanged()
    }

    fun setView(dept: ArticleDept) :Int{
        articles?.clear()
        Log.e("Adapter", dept.id+allArticles.toString())
        allArticles?.forEach {
            if (it.deptId == dept.id){
                articles?.add(it)
            }
        }
        Log.e("Size",size.toString())
        size = articles!!.size
        notifyDataSetChanged()
        return size
    }

    fun setAll(): Int {
        this.articles = ArrayList(allArticles)
        size = articles!!.size
        notifyDataSetChanged()

        Log.e("Size",size.toString())
        return size
    }

    fun clearData() {
        size =0
        notifyDataSetChanged()    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var controllers: LinearLayout =itemView.findViewById(R.id.controllers)
        var title: TextView =itemView.findViewById(R.id.title)
        var image: ImageView =itemView.findViewById(R.id.image)
        var edit: ImageView =itemView.findViewById(R.id.edit)
        var delete: ImageView =itemView.findViewById(R.id.delete)
    }
}