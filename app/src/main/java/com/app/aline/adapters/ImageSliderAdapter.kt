package com.app.aline.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.smarteist.autoimageslider.SliderViewAdapter
import com.app.aline.R

class ImageSliderAdapter  (private val context: Context?) :
    SliderViewAdapter<ImageSliderAdapter.SliderAdapterVH>() {

    var images: ArrayList<String?> = ArrayList()
    var size = 0

    override fun onCreateViewHolder(parent: ViewGroup): SliderAdapterVH {
        val inflate: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_slider_image, null)
        return SliderAdapterVH(inflate)
    }

    override fun onBindViewHolder(holder: SliderAdapterVH, position: Int) {
        val item = images[position]
        holder.apply {
            if (item != null) {
                if (context != null) {
                    Glide.with(context).load(item)
                        .placeholder(R.drawable.image1)
                        .into(userImg)
                }
            }

        }
    }


    override fun getCount(): Int {
        return size
    }

    fun addData(images: ArrayList<String?>) {
        this.images = images
        size = images.size
        notifyDataSetChanged()
    }


    inner class SliderAdapterVH(itemView: View) : ViewHolder(itemView) {
        var userImg: ImageView = itemView.findViewById(R.id.userImg)
    }
}