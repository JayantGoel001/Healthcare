package com.example.healthcare.fragments

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.healthcare.Items.Example
import com.example.healthcare.Items.Statewise
import com.example.healthcare.MainActivity
import com.example.healthcare.R
import com.example.healthcare.WorkAnalysis.Adapter_horizontal
import com.example.healthcare.WorkAnalysis.CirclePagerIndicatorDecoration
import com.example.healthcare.WorkAnalysis.My_adapter_analysis
import com.example.healthcare.WorkAnalysis.User
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.fragment_news.view.*

class News : Fragment() {

    val url = "https://newsapi.org/v2/everything?q=covid%corona&apiKey=cb9951ac79724fe7a06b2c30afb1d831"
    val url2 = "https://api.covid19india.org/data.json"

    lateinit var mcontext: Context
    lateinit var que: RequestQueue


    @SuppressLint("SetTextI18n")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_news, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(vieww: View, savedInstanceState: Bundle?) {
        mcontext = activity!!.applicationContext
        que = Volley.newRequestQueue(mcontext)

        loadDataForRec1(vieww)

        val request = StringRequest(url2, Response.Listener { response ->
            val builder = GsonBuilder()
            val gson = builder.create()
            val users = gson.fromJson(response, Example::class.java)
            val list = users.statewise
            var x: Statewise? = null
            for (it in list) {
                if (it.state.toString().equals("total", true)) {
                    x = it
                }
            }
            x?.let {
                vieww.todays_inc.text = "+${it.deltaconfirmed}"
                val inc = it.confirmed.toString()
                val animator = ValueAnimator.ofInt(0, inc.toInt())
                animator.duration = 1500
                animator.addUpdateListener { it1 ->
                    vieww.total_conf.text = it1.animatedValue.toString()
                }
                animator.start()
            }
        },
            Response.ErrorListener {
                Log.i("error", it.message.toString())
            })
        que.add(request)
        vieww.cross.setOnClickListener {
            vieww.relative_layout_help.visibility = RelativeLayout.GONE
        }
        loadData(vieww)
        super.onViewCreated(vieww, savedInstanceState)
    }
    fun loadData(view: View) {

        val request = StringRequest(
            url, Response.Listener { response ->

                val builder = GsonBuilder()
                val gson = builder.create()

                val users = gson.fromJson(response, User::class.java)

                val adapter = My_adapter_analysis(mcontext, users)
                view.recyclerView_analysis.visibility = RecyclerView.VISIBLE
                view.progressBar_analysis.visibility = RecyclerView.GONE
                view.recyclerView_analysis.layoutManager = LinearLayoutManager(mcontext)
                view.recyclerView_analysis.adapter = adapter

            },
            Response.ErrorListener {
                Log.i("error", it.message.toString())

                view.progressBar_analysis.visibility = RecyclerView.GONE
                view.container_analysis.visibility = LinearLayout.GONE
                view.animationView_analysis.visibility = LinearLayout.VISIBLE
                view.retry_box_analysis.visibility = LinearLayout.VISIBLE

                view.retry_box_analysis.setOnClickListener {
                    startActivity(Intent(mcontext, MainActivity::class.java))
                    activity?.finish()
                }

            })

        que.add(request)

    }

    fun loadDataForRec1(view: View) {

        val list = mutableListOf(R.drawable.prevention, R.drawable.symptoms,
            R.drawable.corona1,R.drawable.corororo,R.drawable.co,R.drawable.cor
        )
        PagerSnapHelper().attachToRecyclerView(view.recyclerView_analysis_1)
        val adapter = Adapter_horizontal(mcontext, list)
        view.recyclerView_analysis_1.adapter = adapter
        view.recyclerView_analysis_1.addItemDecoration(CirclePagerIndicatorDecoration())
        view.recyclerView_analysis_1.layoutManager = LinearLayoutManager(mcontext, LinearLayoutManager.HORIZONTAL, false)


    }


}