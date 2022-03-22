package com.app.aline.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.aline.R
import com.app.aline.data.models.ArticleDept

class DeptApadter(private val context: Context?,val depts: ArrayList<ArticleDept>) : RecyclerView.Adapter<DeptApadter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.item_dept , parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = depts[position]
        holder.apply {
            name.text = item.deptTitle

            itemView.setOnClickListener {
             onItemClickListener.let {
                 if (it != null) {
                     it(item)
                 }
             }
            }
        }

    }


    private var onItemClickListener: ((ArticleDept) -> Unit)? = null

    fun setOnItemClickListener(listener: (ArticleDept) -> Unit) {
        onItemClickListener = listener
    }


    override fun getItemCount(): Int {
        return depts.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var name: TextView =itemView.findViewById(R.id.name)
    }
}