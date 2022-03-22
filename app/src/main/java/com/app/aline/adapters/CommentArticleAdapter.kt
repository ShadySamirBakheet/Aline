package com.app.aline.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.textfield.TextInputEditText
import com.app.aline.R
import com.app.aline.data.models.ArticleCommentView
import com.app.aline.utils.Methods

class CommentArticleAdapter (private val context: Context?) :
    RecyclerView.Adapter<CommentArticleAdapter.ViewHolder>() {
    var articleComments: ArrayList<ArticleCommentView> = ArrayList()
    var size = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.item_comment, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = articleComments[position]
        holder.apply {

            comment.text = item.comment.comment
            lastDate.text = Methods.getDateString(item.comment.date)

            userName.text = item.comment.userName

            if (item.comment.userImage != null) {
                if (context != null) {
                    Glide.with(context).load(item.comment.userImage)
                        .placeholder(R.drawable.user)
                        .into(userimg)
                }
            }

            replay.setOnClickListener {
                inputLay.visibility = View.VISIBLE
            }

            btnSend.setOnClickListener {
                val textComment = commentReplay.text.toString().trim()
                onItemClickListener.let {
                    if (it != null) {
                        it(textComment, item.comment.id.toString())
                    }
                }
            }

            if (item.replays != null && item.replays!!.isNotEmpty()){
                usersComments.apply {
                    setHasFixedSize( true)
                    layoutManager = LinearLayoutManager(context)
                    adapter= CommentArticleReplayAdapter(context,item.replays)
                }
            }

        /*itemView.setOnClickListener {
                if (SharedStorage.getUserName(context!!) != item.userID) {
                    context.startActivity(
                        Intent(context, ChatActivity::class.java)
                            .putExtra("userId", item.userID)
                            .putExtra("userImg", item.userImage)
                            .putExtra("userName", item.userName)
                    )
                }
            }*/
        }
    }


    private var onItemClickListener: ((String,String) -> Unit)? = null

    fun setOnSaveListener(listener: (String,String) -> Unit) {
        onItemClickListener = listener
    }


    override fun getItemCount(): Int {
        return size
    }

    fun addData(articleComments: ArrayList<ArticleCommentView>) {
        this.articleComments = articleComments
        this.articleComments.reverse()
        size = articleComments.size
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var comment: TextView = itemView.findViewById(R.id.comment)
        var userName: TextView = itemView.findViewById(R.id.userName)
        var lastDate: TextView = itemView.findViewById(R.id.lastDate)
        var userimg: ImageView = itemView.findViewById(R.id.userimg)
        var replay: ImageView = itemView.findViewById(R.id.replay)
        var btnSend: ImageView = itemView.findViewById(R.id.btnSend)
        var commentReplay: TextInputEditText = itemView.findViewById(R.id.commentReplay)
        var inputLay: LinearLayout = itemView.findViewById(R.id.inputLay)
        var usersComments: RecyclerView = itemView.findViewById(R.id.usersComments)
    }
}