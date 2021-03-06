package com.app.aline.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.CheckedTextView
import com.app.aline.R
import com.app.aline.data.models.ArticleDept

class DeptArrayAdapter  (val dataSource: ArrayList<ArticleDept>, var context: Context): BaseAdapter() {

    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    init {
        //dataSource.add(0,AreasResponse.Data("",0,context.getString(R.string.area),0,0,""))
    }

    override fun getCount(): Int {
        return dataSource.size
    }

    override fun getItem(position: Int): ArticleDept{
        return dataSource[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        val vh: ItemHolder
        if (convertView == null) {
            view = inflater.inflate(R.layout.item_spinner, parent, false)
            vh = ItemHolder(view)
            view?.tag = vh
        } else {
            view = convertView
            vh = view.tag as ItemHolder
        }
        val item = dataSource[position]

        vh.itemName.text = item.deptTitle

        return view
    }


    private class ItemHolder(row: View?) {
        val itemName: CheckedTextView = row?.findViewById(R.id.text1) as CheckedTextView
    }
}