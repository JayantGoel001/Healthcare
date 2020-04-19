package com.example.healthcare.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Vibrator
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.healthcare.Items.Example
import com.example.healthcare.MainActivity
import com.example.healthcare.R
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.fragment_analysis.view.*
import com.example.healthcare.StatsWork.My_adapter
import kotlinx.android.synthetic.main.fragment_analysis.*
import kotlinx.android.synthetic.main.fragment_analysis.view.switchBetweenGridLin

class Analysis : Fragment() {
    val url = "https://api.covid19india.org/data.json"
    lateinit var mcontext: Context
    lateinit var que: RequestQueue
    private lateinit var grid:ImageButton
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view=inflater.inflate(R.layout.fragment_analysis, container, false)
        mcontext = activity!!.applicationContext
        que = Volley.newRequestQueue(mcontext)
        grid=view.findViewById(R.id.switchBetweenGridLin)
        loadData(view)
        return view
    }
    var LayoutType:Int=0
    private fun loadData(view: View) {

        val request = StringRequest(
            url, Response.Listener { response ->

                val builder = GsonBuilder()
                val gson = builder.create()

                val users = gson.fromJson(response, Example::class.java)
                val list = users.statewise
                val vib = mcontext.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                var adapter:My_adapter
                if(LayoutType==0)
                {
                    view.recyclerView.layoutManager = LinearLayoutManager(mcontext)
                    grid.setBackgroundResource(R.drawable.ic_grid_on_black_24dp)
                    adapter = My_adapter(mcontext, list, vib,LayoutType)
                    view.recyclerView.visibility = RecyclerView.VISIBLE
                    view.recyclerView.adapter = adapter
                    view.progressBar.visibility = ProgressBar.GONE
                }

                switchBetweenGridLin.setOnClickListener {
                    if (LayoutType==0)
                    {
                        view.recyclerView.layoutManager = GridLayoutManager(context,2,RecyclerView.VERTICAL,false)
                        LayoutType=1
                        switchBetweenGridLin.setBackgroundResource(R.drawable.ic_view_headline_black_24dp)
                        adapter = My_adapter(mcontext, list, vib,LayoutType)
                        view.recyclerView.visibility = RecyclerView.VISIBLE
                        view.recyclerView.adapter = adapter
                        view.progressBar.visibility = ProgressBar.GONE
                    }
                    else
                    {
                        view.recyclerView.layoutManager = LinearLayoutManager(mcontext)
                        LayoutType=0
                        switchBetweenGridLin.setBackgroundResource(R.drawable.ic_grid_on_black_24dp)
                        adapter = My_adapter(mcontext, list, vib,LayoutType)
                        view.recyclerView.visibility = RecyclerView.VISIBLE
                        view.recyclerView.adapter = adapter
                        view.progressBar.visibility = ProgressBar.GONE
                    }
                }
            },
            Response.ErrorListener {
                view.contianer.visibility = LinearLayout.GONE
                view.animationView.visibility = LinearLayout.VISIBLE
                view.retry_box.visibility = LinearLayout.VISIBLE

                view.retry_box.setOnClickListener {
                    startActivity(Intent(mcontext, MainActivity::class.java))
                    activity?.finish()
                }

                Log.i("error", it.message.toString())
            })

        que.add(request)

    }


}
