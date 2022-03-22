package com.app.aline.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.app.aline.R
import com.app.aline.data.models.ArticleCommentReplay
import com.app.aline.utils.Methods

class CommentArticleReplayAdapter(private val context: Context?,
                                  var articleComments: List<ArticleCommentReplay>?
) :
    RecyclerView.Adapter<CommentArticleReplayAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.item_comment_replay, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = articleComments!!.get(position)
        holder.apply {

            comment.text = item.comment
            lastDate.text = Methods.getDateString(item.date)

            userName.text = item.userName

            if (item.userImage != null) {
                if (context != null) {
                    Glide.with(context).load(item.userImage)
                        .placeholder(R.drawable.user)
                        .into(userimg)
                }
            }

        }
    }

    override fun getItemCount(): Int {
        return articleComments?.size ?: 0
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var comment: TextView = itemView.findViewById(R.id.comment)
        var userName: TextView = itemView.findViewById(R.id.userName)
        var lastDate: TextView = itemView.findViewById(R.id.lastDate)
        var userimg: ImageView = itemView.findViewById(R.id.userimg)
    }
}