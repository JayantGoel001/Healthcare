package com.example.healthcare.WorkAnalysis

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.healthcare.R

class Adapter_horizontal(private val myContext: Context, private val list: MutableList<Int>) :
    RecyclerView.Adapter<ViewHolder_horizontal>() {

    override fun onCreateViewHolder(parent: ViewGroup, i: Int): ViewHolder_horizontal {
        val view = LayoutInflater.from(myContext).inflate(R.layout.list_horizontal, parent, false)
        return ViewHolder_horizontal(view)
    }

    override fun onBindViewHolder(holder: ViewHolder_horizontal, position: Int) {
        holder.home_image.setImageDrawable(myContext.resources.getDrawable(list[position]))
    }

    override fun getItemCount(): Int = list.size

}