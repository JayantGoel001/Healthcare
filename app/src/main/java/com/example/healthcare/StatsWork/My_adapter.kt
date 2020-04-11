package com.example.healthcare.StatsWork

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.os.Vibrator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.healthcare.Items.Statewise
import com.example.healthcare.R


class My_adapter(private val myContext: Context, var list: MutableList<Statewise>, val vib: Vibrator,val LayoutType:Int) :
    RecyclerView.Adapter<My_viewHolder>() {

    val backG: ArrayList<Int> = arrayListOf(
        R.drawable.bg1,
        R.drawable.bg2,
        R.drawable.bg3,
        R.drawable.bg4,
        R.drawable.bg5
    )

    override fun onCreateViewHolder(parent: ViewGroup, i: Int): My_viewHolder {
        if (vib.hasVibrator())
            vib.cancel()
        val view = if(LayoutType==0) {
            LayoutInflater.from(myContext).inflate(R.layout.list, parent, false)
        } else {
            LayoutInflater.from(myContext).inflate(R.layout.grid, parent, false)
        }
        return My_viewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: My_viewHolder, position: Int) {

        holder.title.text = list[position].state.toString().trim()
        val total = list[position].confirmed.toString()
        holder.totalInfected.text = total

        holder.firstLetter.background = ContextCompat.getDrawable(myContext, backG[position % 5])
        holder.firstLetter.text = list[position].state.toCharArray()[0].toString()
        holder.totalInfected.text = list[position].confirmed.toString()
        holder.date.text = list[position].lastupdatedtime.subSequence(0, 10).toString()

        holder.itemView.setOnClickListener {

            val intent = Intent(myContext, MoreDetail::class.java)
            intent.flags = FLAG_ACTIVITY_NEW_TASK
            intent.putExtra("data", list[position])
            myContext.startActivity(intent)

        }

    }

    override fun getItemCount(): Int = list.size

}