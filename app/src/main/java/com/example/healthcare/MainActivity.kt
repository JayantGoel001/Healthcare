package com.example.healthcare

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.example.healthcare.fragments.*
import eu.long1.spacetablayout.SpaceTabLayout


class MainActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val fragmentList: ArrayList<Fragment> = ArrayList()
        fragmentList.add(Analysis())
        fragmentList.add(News())
        fragmentList.add(Location())
        fragmentList.add(Profile())

        val tabLayout: SpaceTabLayout = findViewById(R.id.spaceTabLayout)

        val viewPager: ViewPager = findViewById(R.id.viewPager)

        tabLayout.initialize(viewPager, supportFragmentManager, fragmentList, savedInstanceState)
        tabLayout.setTabFourIcon(R.drawable.ic_person_black_24dp)
    }

    override fun onStart() {

        val sharedPreferences=getSharedPreferences("name_email_pass", Context.MODE_PRIVATE)
        val email=sharedPreferences.getString("email","1234")!!
        val pass=sharedPreferences.getString("pass","!@#$")!!
        val name=sharedPreferences.getString("name","!!!!")!!

        //Toast.makeText(this,"$email   $name  $pass ",Toast.LENGTH_LONG).show()
        if(email=="1234" || pass=="!@#$" || name=="!!!!")
        {
            val intent=Intent(this,LoginAct::class.java)
            intent.flags=Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
        super.onStart()
    }


}
