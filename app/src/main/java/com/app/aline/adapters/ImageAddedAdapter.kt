package com.app.aline.adapters

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.app.aline.R
import java.util.*
import kotlin.collections.ArrayList

class ImageAddedAdapter (private val context: Context?) :
    RecyclerView.Adapter<ImageAddedAdapter.ViewHolder>() {
    private var images: ArrayList<Any> = ArrayList()
  private  var size = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.item_image_added, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = images[position]
        holder.apply {

            if (item is String){
                if (context != null) {
                    Glide.with(context).load(item)
                        .placeholder(R.drawable.image1)
                        .into(userImg)
                }
            }else{

                userImg.setImageURI(item as Uri)
            }

            clear.setOnClickListener {
                images.removeAt(position)
                size = images.size
                notifyDataSetChanged()
            }
        }
    }


    override fun getItemCount(): Int {
        return size
    }

    fun addItem(uri: Any) {
        images.add(uri)
        size = images.size
        notifyDataSetChanged()
    }

    fun addData(imagesAdd: ArrayList<String>) {
        val remove = ArrayList<String>()
        if (images.isNotEmpty()){
            images.forEach {
                if (it is String){
                    remove.add(it);
                }
            }
        }
        images.removeAll(remove)
        images.addAll(imagesAdd)
        size = images.size
        notifyDataSetChanged()

    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var userImg: ImageView = itemView.findViewById(R.id.userImg)
        var clear: ImageView = itemView.findViewById(R.id.clear)
    }
}